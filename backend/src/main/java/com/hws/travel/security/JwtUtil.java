package com.hws.travel.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import java.time.Instant;
import java.util.Date;
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
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(java.util.Base64.getEncoder().encodeToString(jwtSecret.getBytes())));
    }

    public String generateToken(String username, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(jwtExpiration)))
                .signWith(secretKey)
                .compact();
    }

    public Claims validateToken(String token) {
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
        return jws.getPayload();
    }

        public String extractUsername(String token) {
            try {
                Claims claims = validateToken(token);
                return claims.getSubject();
            } catch (Exception e) {
                return null;
            }
        }

        public boolean validateToken(String token, org.springframework.security.core.userdetails.UserDetails userDetails) {
            String username = extractUsername(token);
            return (username != null && username.equals(userDetails.getUsername()));
        }
}
