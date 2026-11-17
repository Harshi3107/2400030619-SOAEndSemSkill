package com.example.jwtservice.service;

import com.example.jwtservice.dto.JwtResponse;
import com.example.jwtservice.dto.UserRequest;
import com.example.jwtservice.exception.JwtGenerationException;
import com.example.jwtservice.util.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtUtil jwtUtil;

    public JwtService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public JwtResponse generateToken(UserRequest user) {
        try {
            String token = jwtUtil.generateToken(user);
            return new JwtResponse(token, "Bearer", jwtUtil.getExpirationSeconds());
        } catch (RuntimeException exception) {
            throw new JwtGenerationException("Unable to generate a JWT for this request.", exception);
        }
    }
}
