package com.bhar.recipe_sharing.recipe;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bhar.recipe_sharing.user.UserModel;
import com.bhar.recipe_sharing.user.UserRepository;

@Service 
public class RecipeService {

    private final RecipeRepository recipeRepo;
    private final UserRepository userRepo;

    public RecipeService(RecipeRepository recipeRepo, UserRepository userRepo) {
        this.recipeRepo = recipeRepo;
        this.userRepo = userRepo;
    }

    public RecipeModel createRecipe(String title,String description,
        String ingredients, String steps, Long authorId){

        UserModel author = userRepo.findById(authorId).
        orElseThrow(()->new IllegalArgumentException("Author not found : "+authorId));
        
        RecipeModel recipe = new RecipeModel();
        recipe.setTitle(title);
        recipe.setDescription(description);
        recipe.setIngredients(ingredients);
        recipe.setSteps(steps);
        recipe.setAuthor(author);
        return recipeRepo.save(recipe);
    }

    public RecipeModel editRecipe(Long id,String title,String description,
        String ingredients, String steps){

        RecipeModel recipe = recipeRepo.findById(id).
        orElseThrow(()-> new IllegalArgumentException("Recipe not found : "+ id));

        recipe.setTitle(title);
        recipe.setDescription(description);
        recipe.setIngredients(ingredients);
        recipe.setSteps(steps);

        return recipeRepo.save(recipe);
    }

    public RecipeModel getRecipe(Long id){
        return recipeRepo.findById(id)
        .orElseThrow(()-> new IllegalArgumentException("Recipe not found : "+ id));
    }
    public List<RecipeModel> getRecipes(){
        return recipeRepo.findAll();
    }
    public List<RecipeModel> getByTitle(String title){
        return recipeRepo.findByTitle(title);
    }

    public void deleteRecipe(Long id){
        recipeRepo.deleteById(id);
    }
}
