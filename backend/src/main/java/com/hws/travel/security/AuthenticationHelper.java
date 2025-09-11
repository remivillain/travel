package com.hws.travel.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuthenticationHelper {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Récupère l'ID utilisateur depuis le JWT dans l'en-tête Authorization
     */
    public Long getCurrentUserId() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");
                
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String jwt = authHeader.substring(7);
                    return jwtUtil.extractUserId(jwt);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Récupère l'email depuis l'Authentication (fallback)
     */
    public String getCurrentUserEmail(Authentication authentication) {
        return authentication != null ? authentication.getName() : null;
    }
}
