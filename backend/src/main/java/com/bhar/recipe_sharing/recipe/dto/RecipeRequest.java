package com.bhar.recipe_sharing.recipe.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public record RecipeRequest(
    @NotBlank(message = "Title is required")
    String title,

    String description,
    String ingredients,
    String steps,

    List<String> images,

    @NotNull(message = "authorId is required")
    Long authorId
) {
}
