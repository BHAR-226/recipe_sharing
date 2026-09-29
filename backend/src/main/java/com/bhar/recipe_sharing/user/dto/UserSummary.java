package com.bhar.recipe_sharing.user.dto;

import com.bhar.recipe_sharing.user.UserModel;

public record UserSummary(
    Long id,
    String username
) {
    public static UserSummary from(UserModel user) {
        return new UserSummary(user.getId(), user.getUsername());
    }
}