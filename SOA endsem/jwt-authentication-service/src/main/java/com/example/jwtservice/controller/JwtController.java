package com.example.jwtservice.controller;

import com.example.jwtservice.dto.JwtResponse;
import com.example.jwtservice.dto.UserRequest;
import com.example.jwtservice.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jwt")
@Tag(name = "JWT", description = "JWT token issuance")
public class JwtController {

    private final JwtService jwtService;

    public JwtController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Generate a JWT",
            description = """
                    Accepts a user payload that the caller is expected to have already authenticated.
                    This educational endpoint is public for assignment testing and does not authenticate
                    the supplied identity. In production, restrict token issuance to a trusted
                    authentication service and validate its authorization.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UserRequest.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "userId": 101,
                                      "username": "Harshi",
                                      "email": "harshi@example.com",
                                      "role": "USER"
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "JWT generated successfully",
                            content = @Content(schema = @Schema(implementation = JwtResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Malformed JSON or invalid request fields"),
                    @ApiResponse(responseCode = "500", description = "Token generation failed")
            }
    )
    public ResponseEntity<JwtResponse> generateToken(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(jwtService.generateToken(userRequest));
    }
}
