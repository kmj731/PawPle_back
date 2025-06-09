package com.project.spring.pawple.application.auth;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {
    @Value("${spring.security.jwt.secret}")
    String secret;
    @Value("${spring.security.jwt.expires}")
    Integer expires;
    SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());
        return createToken(claims, userDetails.getUsername());
    }
    
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        return createToken(claims, username);
    }

    public String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plus(expires, ChronoUnit.SECONDS)))
            .signWith(key)
            .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try { extractAllClaims(token); return true; }
        catch(Exception e) { return false; }
    }

    public String extractUsername(String token) {
        return (String)extractAllClaims(token).get("username");
    }

    public Map<String,Object> extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
