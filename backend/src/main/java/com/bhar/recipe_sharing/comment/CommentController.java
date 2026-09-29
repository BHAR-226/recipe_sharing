package com.bhar.recipe_sharing.comment;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bhar.recipe_sharing.comment.dto.CommentRequest;
import com.bhar.recipe_sharing.comment.dto.CommentResponse;

import jakarta.validation.Valid;

@RestController 
@RequestMapping ("/api/comment")
public class CommentController {
    private CommentService cService;
    public CommentController(CommentService service){
        this.cService = service;
    }

    @GetMapping ("/user/{userId}")
    public List<CommentResponse> getUserComments(@PathVariable Long userId){
        return cService.getUserComments(userId);
    }

    @GetMapping ("/recipe/{recipeId}")
    public List<CommentResponse> getRecipeComment(@PathVariable Long recipeId){
        return cService.getRecipeComments(recipeId);
    }

    @PostMapping ("/new-comment")
    public ResponseEntity<CommentResponse> newComment (@Valid @RequestBody CommentRequest request){
        CommentResponse created = cService.createComment(
            request.content(),
            request.userId(),
            request.recipeId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long id){
        cService.deleteComment(id);
    }
}