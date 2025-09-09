package com.hws.travel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final com.hws.travel.security.JwtFilter jwtFilter;

    public SecurityConfig(com.hws.travel.security.JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // désactive CSRF pour simplifier les tests API
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/health", "/api/auth/login", "/v3/api-docs/**", "/swagger-ui/**", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin()) // permet les frames H2
            )
            .addFilterBefore(jwtFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
