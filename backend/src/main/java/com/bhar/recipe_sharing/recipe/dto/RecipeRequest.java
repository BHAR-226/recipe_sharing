package com.bhar.recipe_sharing.recipe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RecipeRequest(
    @NotBlank(message = "Title is required")
    String title,

    String description,
    String ingredients,
    String steps,

    @NotNull(message = "authorId is required")
    Long authorId
) {
}