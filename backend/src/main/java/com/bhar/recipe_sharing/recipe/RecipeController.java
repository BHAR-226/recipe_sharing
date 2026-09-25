package com.bhar.recipe_sharing.recipe;


import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping("/recipe")
public class RecipeController {

    private RecipeService rServie;
    public RecipeController(RecipeService service){
        this.rServie = service;
    }

    @GetMapping ()
    public List<RecipeModel> getRecipes(){
        return rServie.getRecipes();
    }
    @GetMapping("/{id}")
    public RecipeModel getRecipeById(@PathVariable Long id){
        return rServie.getRecipe(id);
    }

    @PostMapping("/create-recipe")
    public RecipeModel createRecipe(@RequestBody RecipeModel recipe){
        return rServie.createRecipe(
        recipe.getTitle(),
        recipe.getDescription(),
        recipe.getIngredients(),
        recipe.getSteps(),
        recipe.getAuthor().getId()
        );
    }

    @PutMapping("/edit-recipe/{id}")
    public RecipeModel editRecipe(@RequestBody RecipeModel recipe, @PathVariable Long id){
        return rServie.editRecipe(id,
        recipe.getTitle(),
        recipe.getDescription(),
        recipe.getIngredients(),
        recipe.getSteps()
        );

    }

    @DeleteMapping ("/{id}")
    public void deleteRecipe(@PathVariable Long id){
        rServie.deleteRecipe(id);
    }

}
