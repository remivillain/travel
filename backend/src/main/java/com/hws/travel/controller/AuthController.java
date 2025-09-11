package com.hws.travel.controller;

import com.hws.travel.security.JwtUtil;
import com.hws.travel.entity.Role;
import com.hws.travel.entity.User;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;
import com.hws.travel.exception.NoRoleAssignedException;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Gestion de l'authentification avec JWT")
public class AuthController {
    private final com.hws.travel.service.AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(com.hws.travel.service.AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    @Operation(
        summary = "Authentification utilisateur", 
        description = "Permet à un utilisateur de s'authentifier avec email/mot de passe et récupérer un token JWT"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentification réussie - token JWT retourné"),
        @ApiResponse(responseCode = "401", description = "Identifiants incorrects"),
        @ApiResponse(responseCode = "400", description = "Aucun rôle associé à cet utilisateur")
    })
    public Map<String, String> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        User user = authService.authenticate(email, password);
        List<String> roles = user.getRoles().stream()
            .map(Role::getName)
            .toList();
        if (roles.isEmpty()) {
            throw new NoRoleAssignedException("Aucun rôle associé à cet utilisateur. Veuillez contacter un administrateur.");
        }
        String token = jwtUtil.generateToken(email, roles, user.getId());
        return Map.of("token", token);
    }
}
