package com.example.jwtservice.util;

import com.example.jwtservice.dto.UserRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private static final String ISSUER = "test-issuer";
    private static final SecretKey SIGNING_KEY = Keys.hmacShaKeyFor(
            "test-key-material-at-least-32-bytes-long".getBytes(StandardCharsets.UTF_8));

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SIGNING_KEY, 60_000, ISSUER);
    }

    @Test
    void generatesTokenWithExpectedIdentityClaims() {
        String token = jwtUtil.generateToken(sampleUser());

        assertTrue(jwtUtil.validateToken(token));
        assertEquals("Harshi", jwtUtil.extractUsername(token));

        Claims claims = jwtUtil.extractClaims(token);
        assertEquals(101, claims.get("userId", Integer.class));
        assertEquals("USER", claims.get("role", String.class));
        assertEquals("harshi@example.com", claims.get("email", String.class));
        assertEquals(ISSUER, claims.getIssuer());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void rejectsMalformedAndInvalidSignatureTokens() {
        assertFalse(jwtUtil.validateToken("not-a-jwt"));

        String token = jwtUtil.generateToken(sampleUser());
        SecretKey differentKey = Keys.hmacShaKeyFor(
                "another-key-material-at-least-32-bytes".getBytes(StandardCharsets.UTF_8));
        JwtUtil verifierWithDifferentKey = new JwtUtil(differentKey, 60_000, ISSUER);
        assertFalse(verifierWithDifferentKey.validateToken(token));
    }

    @Test
    void rejectsExpiredToken() throws InterruptedException {
        JwtUtil shortLivedJwtUtil = new JwtUtil(SIGNING_KEY, 1, ISSUER);
        String token = shortLivedJwtUtil.generateToken(sampleUser());
        Thread.sleep(20);

        assertFalse(shortLivedJwtUtil.validateToken(token));
    }

    @Test
    void reportsExpirationClaimInTheFutureForNewToken() {
        String token = jwtUtil.generateToken(sampleUser());

        assertTrue(jwtUtil.extractClaims(token).getExpiration().after(Date.from(Instant.now())));
    }

    private UserRequest sampleUser() {
        return new UserRequest(101, "Harshi", "harshi@example.com", "USER");
    }
}
