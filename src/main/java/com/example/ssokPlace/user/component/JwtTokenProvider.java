package com.example.ssokPlace.user.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.access-exp-ms:900000}")
    private long accessExpMs;

    private SecretKey key;

    @PostConstruct
    void init() {
        if (secret == null || secret.isBlank()) {
            this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            return;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(secret);
            this.key = Keys.hmacShaKeyFor(decoded);
        } catch (IllegalArgumentException e) {
            // Base64 아니면 raw 문자열로 취급
            this.key = Keys.hmacShaKeyFor(secret.getBytes());
        }
    }

    public String generateAccessToken(String email, Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessExpMs))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getEmail(String token) {
        Claims c = Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody();
        return c.get("email", String.class);
    }

    public String getUserId(String token) {
        return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody().getSubject();
    }
}