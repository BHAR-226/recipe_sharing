package com.bhar.recipe_sharing.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentRequest(
    @NotBlank(message = "Content is required")
    String content,

    @NotNull(message = "userId is required")
    Long userId,

    @NotNull(message = "recipeId is required")
    Long recipeId
) {
}