package com.hws.travel.controller;

import com.hws.travel.security.JwtUtil;

import com.hws.travel.entity.Role;
import com.hws.travel.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.hws.travel.exception.NoRoleAssignedException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final com.hws.travel.service.AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(com.hws.travel.service.AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
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
        String token = jwtUtil.generateToken(email, roles);
        return Map.of("token", token);
    }
}
