
package com.hws.travel.controller;

import com.hws.travel.security.JwtUtil;

import jakarta.transaction.Transactional;

import com.hws.travel.entity.Role;
import com.hws.travel.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    @Transactional
    public Map<String, String> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        User user = authService.authenticate(email, password);

        String role = user.getRoles().stream()
        .findFirst()
        .map(Role::getName)
        .orElse(null);

        String token = jwtUtil.generateToken(email, role);
        return Map.of("token", token);
    }
}
