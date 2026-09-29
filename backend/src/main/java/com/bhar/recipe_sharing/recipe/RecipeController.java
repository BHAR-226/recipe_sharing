package com.bhar.recipe_sharing.recipe;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bhar.recipe_sharing.recipe.dto.RecipeRequest;
import com.bhar.recipe_sharing.recipe.dto.RecipeResponse;
import com.bhar.recipe_sharing.recipe.dto.RecipeUpdateRequest;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/recipe")
public class RecipeController {

    private RecipeService rServie;
    public RecipeController(RecipeService service){
        this.rServie = service;
    }

    @GetMapping ()
    public List<RecipeResponse> getRecipes(){
        return rServie.getRecipes();
    }
    @GetMapping("/{id}")
    public RecipeResponse getRecipeById(@PathVariable Long id){
        return rServie.getRecipe(id);
    }

    @PostMapping("/create-recipe")
    public ResponseEntity<RecipeResponse> createRecipe(@Valid @RequestBody RecipeRequest request){
        RecipeResponse created = rServie.createRecipe(
        request.title(),
        request.description(),
        request.ingredients(),
        request.steps(),
        request.authorId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/edit-recipe/{id}")
    public RecipeResponse editRecipe(@Valid @RequestBody RecipeUpdateRequest request, @PathVariable Long id){
        return rServie.editRecipe(id,
        request.title(),
        request.description(),
        request.ingredients(),
        request.steps()
        );

    }

    @GetMapping ("/user/{id}")
    public List<RecipeResponse> userRecipes(@PathVariable Long id){
        return rServie.userRecipes(id);
    }

    @GetMapping ("search/{title}")
    public List<RecipeResponse> searchWithTitle(@PathVariable String title){
        return rServie.getByTitle(title);
    }

    @DeleteMapping ("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable Long id){
        rServie.deleteRecipe(id);
    }

}