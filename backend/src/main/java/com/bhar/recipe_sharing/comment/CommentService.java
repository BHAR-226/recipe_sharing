package com.bhar.recipe_sharing.comment;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bhar.recipe_sharing.comment.dto.CommentResponse;
import com.bhar.recipe_sharing.exception.NotFoundException;
import com.bhar.recipe_sharing.recipe.RecipeModel;
import com.bhar.recipe_sharing.recipe.RecipeRepository;
import com.bhar.recipe_sharing.user.UserModel;
import com.bhar.recipe_sharing.user.UserRepository;

@Service
public class CommentService {
    private final CommentRepository commentRepo;
    private final UserRepository userRepo;
    private final RecipeRepository recipeRepo;

    public CommentService(CommentRepository commentRepo,UserRepository userRepo,RecipeRepository recipeRepo) {
        this.commentRepo = commentRepo;
        this.userRepo = userRepo;
        this.recipeRepo = recipeRepo;
    }

    public CommentResponse createComment(String content,Long userId, Long recipeId) {
        CommentModel comment = new CommentModel();
        UserModel user = userRepo.findById(userId)
        .orElseThrow(()-> new NotFoundException("User not found : "+ userId));

        RecipeModel recipe = recipeRepo.findById(recipeId)
        .orElseThrow(()-> new NotFoundException("Recipe not found : "+ recipeId));

        comment.setUser(user);
        comment.setRecipe(recipe);
        comment.setContent(content);
        return CommentResponse.from(commentRepo.save(comment));
    }

    public List<CommentResponse> getUserComments(Long userId){
        UserModel user = userRepo.findById(userId)
        .orElseThrow(() -> new NotFoundException("User not found : " + userId));

        return commentRepo.findByUser(user).stream().map(CommentResponse::from).toList();
    }

    public List<CommentResponse> getRecipeComments(Long recipeId) {
        RecipeModel recipe = recipeRepo.findById(recipeId)
        .orElseThrow(()->new NotFoundException("Recipe not found : "+recipeId));

        return commentRepo.findByRecipe(recipe).stream().map(CommentResponse::from).toList();
    }

    public void deleteComment(Long id) {
        commentRepo.findById(id)
        .orElseThrow(()->new NotFoundException("Comment not found : "+ id));
        commentRepo.deleteById(id);
    }
}