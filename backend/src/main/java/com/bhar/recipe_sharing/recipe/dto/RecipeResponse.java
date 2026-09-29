package com.bhar.recipe_sharing.recipe.dto;

import java.time.LocalDateTime;

import com.bhar.recipe_sharing.recipe.RecipeModel;
import com.bhar.recipe_sharing.user.dto.UserSummary;

public record RecipeResponse(
    Long id,
    String title,
    String description,
    String ingredients,
    String steps,
    UserSummary author,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static RecipeResponse from(RecipeModel recipe) {
        return new RecipeResponse(
            recipe.getId(),
            recipe.getTitle(),
            recipe.getDescription(),
            recipe.getIngredients(),
            recipe.getSteps(),
            UserSummary.from(recipe.getAuthor()),
            recipe.getCreatedAt(),
            recipe.getUpdatedAt()
        );
    }
}