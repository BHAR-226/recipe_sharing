package com.bhar.recipe_sharing.comment;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping ("/comment")
public class CommentController {
    private CommentService cService;
    public CommentController(CommentService service){
        this.cService = service;
    }

    @PostMapping ("/user")
    public List<CommentModel> getUserComments(@RequestBody Long userId){
        //the user id have to be send in the request body
        return cService.getUserComments(userId);
    }

    @PostMapping ("/recipe")
    public List<CommentModel> getRecipeComment(@RequestBody Long recipeId){
                //the recipe id have to be send in the request body
        return cService.getRecipeComments(recipeId);
    }

    @PostMapping ("/new-comment")
    public CommentModel newComment (@RequestBody CommentModel comment){
        return cService.createComment(
            comment.getContent(),
            comment.getUser().getId(),
            comment.getRecipe().getId()
        );
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id){
        cService.deleteComment(id);
    }
}
