package com.bhar.recipe_sharing.recipe;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<RecipeModel, Long>{

    public List<RecipeModel> findByTitle(String title);

    
}
