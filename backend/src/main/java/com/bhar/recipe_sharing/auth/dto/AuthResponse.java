package com.bhar.recipe_sharing.auth.dto;

import com.bhar.recipe_sharing.user.dto.UserResponse;

public record AuthResponse(
    String token,
    String tokenType,
    UserResponse user
) {
    public static AuthResponse of(String token, UserResponse user) {
        return new AuthResponse(token, "Bearer", user);
    }
}

