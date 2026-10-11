package com.bhar.recipe_sharing.user.dto;

import java.time.LocalDateTime;

public record UserResponse(
    Long id,
    String username,
    String email,
    String role,
    String profileImageUrl,
    LocalDateTime createdAt
) {
}
