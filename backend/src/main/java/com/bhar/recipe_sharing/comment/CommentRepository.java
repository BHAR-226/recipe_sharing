package com.bhar.recipe_sharing.comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bhar.recipe_sharing.recipe.RecipeModel;
import com.bhar.recipe_sharing.user.UserModel;

public interface CommentRepository extends JpaRepository<CommentModel, Long>{
    public List<CommentModel> findByUser(UserModel user);

    public List<CommentModel> findByRecipe(RecipeModel recipe    );

}
