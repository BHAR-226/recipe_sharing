package com.bhar.recipe_sharing.like;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bhar.recipe_sharing.like.dto.LikeRequest;

import jakarta.validation.Valid;

@RestController 
@RequestMapping ("/api/like")
public class LikeController {
    private LikeService lService;
    public LikeController (LikeService service){
        this.lService = service;
    }

    @GetMapping ("/recipe/{id}")
    public int likesInRecipe(@PathVariable Long id){
        return lService.likeNumberByRecipe(id);
    }

    @PostMapping ("/like")
    @ResponseStatus(HttpStatus.CREATED)
    public void createLike (@Valid @RequestBody LikeRequest request){
        lService.createLike(request.userId(), request.recipeId());
    }

    @DeleteMapping ("/unlike/{userId}/{recipeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlike(@PathVariable Long userId, @PathVariable Long recipeId){
        lService.unlike(userId, recipeId);
    }
}