package com.bhar.recipe_sharing.user.dto;

import java.time.LocalDateTime;
import com.bhar.recipe_sharing.user.UserModel;

public record UserResponse(
    Long id,
    String username,
    String email,
    String role,
    String avatarUrl,
    LocalDateTime createdAt
) {
    public static UserResponse from(UserModel user) {
        if (user == null) return null;
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getAvatarUrl(),
            user.getCreatedAt()
        );
    }
}
