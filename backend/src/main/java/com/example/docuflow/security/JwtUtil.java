package com.example.docuflow.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    public static final int MIN_BYTES = 32;
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}") // expiration in seconds
    private long expirationSeconds;

    // issuer is the entity that generated the token, "docuflow-app", usually the application name
    @Value("${jwt.issuer:docuflow-app}")
    private String issuer;

    private Key signingKey;

    @PostConstruct
    public void init() {
        // Convert secret key string to bytes using UTF-8 charset
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < MIN_BYTES) {
            throw new IllegalStateException("JWT secret key must be at least 256 bits (32 bytes)");
        }
        // Generate the signing key from the byte array for HMAC-SHA algorithms
        signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username) {
        Instant now = Instant.now(); // Get current time instant
        return Jwts.builder()
                .setSubject(username) // the subject claim, meaning who the token is issued for, is usually the username
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expirationSeconds)))

                // Sign the token using the secret key and HMAC SHA-256 so it can’t be changed without detection.
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String username) {
        try {
            Claims claims = getClaims(token);
            return username.equals(claims.getSubject()) && !isTokenExpired(claims);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    // Claims are key-value pairs that carry data about the user and the token,
    // like username, user ID, roles, token expiration time, etc.
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
