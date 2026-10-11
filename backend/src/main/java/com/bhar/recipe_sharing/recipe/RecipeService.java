package com.bhar.recipe_sharing.recipe;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bhar.recipe_sharing.exception.NotFoundException;
import com.bhar.recipe_sharing.recipe.dto.RecipeResponse;
import com.bhar.recipe_sharing.security.AuthorizationService;
import com.bhar.recipe_sharing.storage.FileStorageService;
import com.bhar.recipe_sharing.user.UserModel;
import com.bhar.recipe_sharing.user.UserRepository;

@Service 
public class RecipeService {

    private final RecipeRepository recipeRepo;
    private final UserRepository userRepo;
    private final FileStorageService fileStorage;
    private final AuthorizationService authorization;

    public RecipeService(RecipeRepository recipeRepo, UserRepository userRepo,
        FileStorageService fileStorage, AuthorizationService authorization) {
        this.recipeRepo = recipeRepo;
        this.userRepo = userRepo;
        this.fileStorage = fileStorage;
        this.authorization = authorization;
    }

    public RecipeResponse createRecipe(String title,String description,
        String ingredients, String steps, String currentEmail){

        UserModel author = authorization.currentUser(currentEmail);

        RecipeModel recipe = new RecipeModel();
        recipe.setTitle(title);
        recipe.setDescription(description);
        recipe.setIngredients(ingredients);
        recipe.setSteps(steps);
        recipe.setAuthor(author);
        return RecipeResponse.from(recipeRepo.save(recipe));
    }

    public RecipeResponse editRecipe(Long id,String title,String description,
        String ingredients, String steps, String currentEmail){

        RecipeModel recipe = recipeRepo.findById(id).
        orElseThrow(()-> new NotFoundException("Recipe not found : "+ id));

        authorization.requireOwnerOrAdmin(recipe.getAuthor().getId(), currentEmail);

        recipe.setTitle(title);
        recipe.setDescription(description);
        recipe.setIngredients(ingredients);
        recipe.setSteps(steps);

        return RecipeResponse.from(recipeRepo.save(recipe));
    }

    public RecipeResponse getRecipe(Long id){
        return RecipeResponse.from(recipeRepo.findById(id)
        .orElseThrow(()-> new NotFoundException("Recipe not found : "+ id)));
    }

    public List<RecipeResponse> getRecipes(){
        return recipeRepo.findAll().stream().map(RecipeResponse::from).toList();
    }

    public List<RecipeResponse> getByTitle(String title){
        return recipeRepo.findByTitleContainingIgnoreCase(title).stream().map(RecipeResponse::from).toList();
    }

    public void deleteRecipe(Long id, String currentEmail){
        RecipeModel recipe = recipeRepo.findById(id)
        .orElseThrow(()-> new NotFoundException("Recipe not found : "+ id));
        authorization.requireOwnerOrAdmin(recipe.getAuthor().getId(), currentEmail);
        fileStorage.delete(recipe.getImageUrl());
        recipeRepo.deleteById(id);
    }

    public RecipeResponse uploadImage(Long id, MultipartFile file, String currentEmail){
        RecipeModel recipe = recipeRepo.findById(id)
        .orElseThrow(()-> new NotFoundException("Recipe not found : "+ id));

        authorization.requireOwnerOrAdmin(recipe.getAuthor().getId(), currentEmail);

        String newUrl = fileStorage.store(file, FileStorageService.RECIPES);
        String oldUrl = recipe.getImageUrl();
        recipe.setImageUrl(newUrl);
        RecipeResponse response = RecipeResponse.from(recipeRepo.save(recipe));

        if (oldUrl != null && !oldUrl.equals(newUrl)){
            fileStorage.delete(oldUrl);
        }
        return response;
    }

    public RecipeResponse deleteImage(Long id, String currentEmail){
        RecipeModel recipe = recipeRepo.findById(id)
        .orElseThrow(()-> new NotFoundException("Recipe not found : "+ id));

        authorization.requireOwnerOrAdmin(recipe.getAuthor().getId(), currentEmail);

        String oldUrl = recipe.getImageUrl();
        recipe.setImageUrl(null);
        RecipeResponse response = RecipeResponse.from(recipeRepo.save(recipe));
        fileStorage.delete(oldUrl);
        return response;
    }

    public List<RecipeResponse> userRecipes (Long userId){
        UserModel author = userRepo.findById(userId).
        orElseThrow(()->new NotFoundException("user not found : "+ userId));
        return recipeRepo.findByAuthor(author).stream()
        .map(RecipeResponse::from).toList();
    }
}