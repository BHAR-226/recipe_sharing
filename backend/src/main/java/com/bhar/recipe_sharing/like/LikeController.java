package com.bhar.recipe_sharing.like;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping ("/like")
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
    public LikeModel createLike (@RequestBody LikeModel like){
        return lService.createLike(
            like.getUser().getId(),
            like.getRecipe().getId()
        );
    }

    @DeleteMapping ("/unlike")
    public void unlike(@RequestBody LikeModel like){
        lService.unlike(like.getUser().getId(), like.getRecipe().getId());
    }
}
