package com.bhar.recipe_sharing.like.dto;

import jakarta.validation.constraints.NotNull;

public record LikeRequest(
    @NotNull(message = "userId is required")
    Long userId,

    @NotNull(message = "recipeId is required")
    Long recipeId
) {
}