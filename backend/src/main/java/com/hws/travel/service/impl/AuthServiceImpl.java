package com.hws.travel.service.impl;

import com.hws.travel.entity.User;
import com.hws.travel.repository.UserRepository;
import com.hws.travel.service.AuthService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User authenticate(String email, String password) {
        User user = userRepository.findByEmailWithRoles(email).orElse(null);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return user;
    }
}
