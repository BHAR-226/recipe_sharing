package com.bhar.recipe_sharing.like;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bhar.recipe_sharing.auth.UserPrincipal;
import com.bhar.recipe_sharing.like.dto.LikeRequest;

import jakarta.validation.Valid;

@RestController 
@RequestMapping ("/api/like")
public class LikeController {
    private final LikeService lService;

    public LikeController (LikeService service){
        this.lService = service;
    }

    @GetMapping ("/recipe/{id}")
    public int likesInRecipe(@PathVariable Long id){
        return lService.likeNumberByRecipe(id);
    }

    @GetMapping ("/status/{userId}/{recipeId}")
    public boolean hasUserLiked(@PathVariable Long userId, @PathVariable Long recipeId){
        return lService.hasUserLiked(userId, recipeId);
    }

    @PostMapping ("/like")
    @ResponseStatus(HttpStatus.CREATED)
    public void createLike (
        @Valid @RequestBody LikeRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ){
        Long userId = (principal != null && principal.id() != null) ? principal.id() : request.userId();
        lService.createLike(userId, request.recipeId());
    }

    @DeleteMapping ("/unlike/{userId}/{recipeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlike(
        @PathVariable Long userId, 
        @PathVariable Long recipeId,
        @AuthenticationPrincipal UserPrincipal principal
    ){
        Long effectiveUserId = (principal != null && principal.id() != null) ? principal.id() : userId;
        lService.unlike(effectiveUserId, recipeId);
    }
}
