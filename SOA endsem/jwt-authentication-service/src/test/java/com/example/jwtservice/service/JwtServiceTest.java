package com.example.jwtservice.service;

import com.example.jwtservice.dto.JwtResponse;
import com.example.jwtservice.dto.UserRequest;
import com.example.jwtservice.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private JwtService jwtService;

    @Test
    void returnsBearerResponseWithGeneratedTokenAndExpiration() {
        UserRequest user = new UserRequest(101, "Harshi", "harshi@example.com", "USER");
        when(jwtUtil.generateToken(user)).thenReturn("signed.jwt.token");
        when(jwtUtil.getExpirationSeconds()).thenReturn(3600L);

        JwtResponse response = jwtService.generateToken(user);

        assertEquals("signed.jwt.token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
        verify(jwtUtil).generateToken(user);
    }
}
