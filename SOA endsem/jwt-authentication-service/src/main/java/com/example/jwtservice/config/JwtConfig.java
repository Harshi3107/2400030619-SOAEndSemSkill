package com.example.jwtservice.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class JwtConfig {

    @Bean
    public SecretKey jwtSigningKey(@Value("${jwt.secret}") String base64Secret) {
        final byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(base64Secret);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("jwt.secret must be a valid Base64-encoded key.", exception);
        }

        if (keyBytes.length < 32) {
            throw new IllegalStateException("jwt.secret must decode to at least 32 bytes for HS256.");
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
