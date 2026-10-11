package com.bhar.recipe_sharing.recipe;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bhar.recipe_sharing.recipe.dto.RecipeRequest;
import com.bhar.recipe_sharing.recipe.dto.RecipeResponse;
import com.bhar.recipe_sharing.recipe.dto.RecipeUpdateRequest;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/recipes")
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
    public ResponseEntity<RecipeResponse> createRecipe(@Valid @RequestBody RecipeRequest request,
        Authentication authentication){
        RecipeResponse created = rServie.createRecipe(
        request.title(),
        request.description(),
        request.ingredients(),
        request.steps(),
        authentication.getName()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/edit-recipe/{id}")
    public RecipeResponse editRecipe(@Valid @RequestBody RecipeUpdateRequest request, @PathVariable Long id,
        Authentication authentication){
        return rServie.editRecipe(id,
        request.title(),
        request.description(),
        request.ingredients(),
        request.steps(),
        authentication.getName()
        );

    }

    @GetMapping ("/user/{userId}")
    public List<RecipeResponse> userRecipes(@PathVariable Long userId){
        return rServie.userRecipes(userId);
    }

    @GetMapping ("search/{title}")
    public List<RecipeResponse> searchWithTitle(@PathVariable String title){
        return rServie.getByTitle(title);
    }

    @DeleteMapping ("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable Long id, Authentication authentication){
        rServie.deleteRecipe(id, authentication.getName());
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RecipeResponse uploadImage(@PathVariable Long id,
        @RequestParam("file") MultipartFile file, Authentication authentication){
        return rServie.uploadImage(id, file, authentication.getName());
    }

    @DeleteMapping("/{id}/image")
    public RecipeResponse deleteImage(@PathVariable Long id, Authentication authentication){
        return rServie.deleteImage(id, authentication.getName());
    }

}