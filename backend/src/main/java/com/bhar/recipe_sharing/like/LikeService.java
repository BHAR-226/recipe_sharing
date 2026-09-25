package com.bhar.recipe_sharing.like;

import org.springframework.stereotype.Service;

import com.bhar.recipe_sharing.recipe.RecipeModel;
import com.bhar.recipe_sharing.recipe.RecipeRepository;
import com.bhar.recipe_sharing.user.UserModel;
import com.bhar.recipe_sharing.user.UserRepository;

@Service
public class LikeService {
    private final LikeRepository likeRepo;
    private final UserRepository userRepo;
    private final RecipeRepository recipeRepo;

    public LikeService(LikeRepository likeRepo, UserRepository userRepo, RecipeRepository recipeRepo) {
        this.likeRepo = likeRepo;
        this.userRepo = userRepo;
        this.recipeRepo = recipeRepo;
    }

    public LikeModel createLike(Long userId, Long recipeId) {
        UserModel user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found : " + userId));
        RecipeModel recipe = recipeRepo.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found : " + recipeId));

        LikeModel like = new LikeModel();
        like.setUser(user);
        like.setRecipe(recipe);
        return likeRepo.save(like);
    }

    public void unlike(Long userId, Long recipeId) {
        LikeModel like = likeRepo.findByUserIdAndRecipeId(userId, recipeId)
            .orElseThrow(() -> new IllegalArgumentException("Like not found"));

        likeRepo.delete(like);
}

    public int likeNumberByRecipe(Long recipeId){
        RecipeModel recipe = recipeRepo.findById(recipeId)
        .orElseThrow(() -> new IllegalArgumentException(
                "Recipe not found : " + recipeId));
        return likeRepo.countByRecipe(recipe);
    }
}