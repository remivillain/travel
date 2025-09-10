package com.hws.travel.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Collections;

import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey secretKey;

    @PostConstruct
    public void initSecretKey() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generateToken(String username, Collection<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(jwtExpiration)))
                .signWith(secretKey)
                .compact();
    }

    public List<String> extractRoles(String token) {
        try {
            Claims claims = validateToken(token);
            Object rolesObj = claims.get("roles");
            if (rolesObj instanceof List<?>) {
                List<String> roles = ((List<?>) rolesObj).stream()
                    .map(Object::toString)
                    .toList();
                org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JwtUtil.class);
                logger.info("Extracted roles: {}", roles);
                return roles;
            } else if (rolesObj instanceof String string) {
                return List.of(string);
            }
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Claims validateToken(String token) {
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
        return jws.getPayload();
    }

    public String extractEmail(String token) {
        try {
            Claims claims = validateToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String token, org.springframework.security.core.userdetails.UserDetails userDetails) {
        try {
            Claims claims = validateToken(token);
            String email = claims.getSubject();
            Date expiration = claims.getExpiration();
            boolean notExpired = expiration == null || expiration.after(new Date());
            return (email != null && email.equals(userDetails.getUsername()) && notExpired);
        } catch (Exception e) {
            return false;
        }
    }
}
