package com.bhar.recipe_sharing.recipe;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bhar.recipe_sharing.user.UserModel;

public interface RecipeRepository extends JpaRepository<RecipeModel, Long>{

public List<RecipeModel> findByTitleContainingIgnoreCase(String title);

    public List<RecipeModel> findByAuthor(UserModel author);
    
}
