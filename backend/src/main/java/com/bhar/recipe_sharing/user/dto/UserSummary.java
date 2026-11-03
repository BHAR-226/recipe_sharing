package com.bhar.recipe_sharing.user.dto;

import com.bhar.recipe_sharing.user.UserModel;

public record UserSummary(
    Long id,
    String username,
    String avatarUrl
) {
    public static UserSummary from(UserModel user) {
        if (user == null) return null;
        return new UserSummary(user.getId(), user.getUsername(), user.getAvatarUrl());
    }
}
