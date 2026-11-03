package com.bhar.recipe_sharing.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "Username or email is required")
    String login,

    @NotBlank(message = "Password is required")
    String password
) {
}

