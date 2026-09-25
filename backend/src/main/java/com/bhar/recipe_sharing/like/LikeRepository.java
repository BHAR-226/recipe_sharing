package com.bhar.recipe_sharing.like;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bhar.recipe_sharing.recipe.RecipeModel;

public interface LikeRepository extends JpaRepository<LikeModel, Long>{
    public int countByRecipe(RecipeModel recipe);
    public Optional<LikeModel> findByUserIdAndRecipeId(Long userId, Long recipeId);
}
