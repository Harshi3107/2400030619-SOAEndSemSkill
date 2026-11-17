package com.example.jwtservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class UserRequest {

    @NotNull(message = "userId is required.")
    @Positive(message = "userId must be greater than zero.")
    private Integer userId;

    @NotBlank(message = "username is required.")
    @Size(max = 100, message = "username must be at most 100 characters.")
    private String username;

    @NotBlank(message = "email is required.")
    @Email(message = "email must be a valid email address.")
    @Size(max = 254, message = "email must be at most 254 characters.")
    private String email;

    @NotBlank(message = "role is required.")
    @Pattern(regexp = "[A-Z][A-Z0-9_]{0,49}", message = "role must contain uppercase letters, digits, or underscores.")
    private String role;

    public UserRequest() {
    }

    public UserRequest(Integer userId, String username, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
