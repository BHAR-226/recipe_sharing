package com.bhar.recipe_sharing.recipe.dto;

import jakarta.validation.constraints.NotBlank;

public record RecipeUpdateRequest(
    @NotBlank(message = "Title is required")
    String title,

    String description,
    String ingredients,
    String steps
) {
}