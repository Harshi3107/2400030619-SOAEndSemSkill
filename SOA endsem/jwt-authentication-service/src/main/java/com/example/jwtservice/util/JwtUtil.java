package com.example.jwtservice.util;

import com.example.jwtservice.dto.UserRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey signingKey;
    private final long expirationMillis;
    private final String issuer;

    public JwtUtil(
            SecretKey signingKey,
            @Value("${jwt.expiration}") long expirationMillis,
            @Value("${jwt.issuer}") String issuer) {
        if (expirationMillis <= 0) {
            throw new IllegalStateException("jwt.expiration must be greater than zero.");
        }
        if (issuer == null || issuer.isBlank()) {
            throw new IllegalStateException("jwt.issuer must not be blank.");
        }

        this.signingKey = signingKey;
        this.expirationMillis = expirationMillis;
        this.issuer = issuer;
    }

    public String generateToken(UserRequest user) {
        Instant issuedAt = Instant.now();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getUserId())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .issuer(issuer)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(issuedAt.plusMillis(expirationMillis)))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpirationSeconds() {
        return expirationMillis / 1000;
    }
}
