package com.bhar.recipe_sharing.comment.dto;

import java.time.LocalDateTime;

import com.bhar.recipe_sharing.comment.CommentModel;
import com.bhar.recipe_sharing.user.dto.UserSummary;

public record CommentResponse(
    Long id,
    String content,
    UserSummary user,
    LocalDateTime createdAt
) {
    public static CommentResponse from(CommentModel comment) {
        return new CommentResponse(
            comment.getId(),
            comment.getContent(),
            UserSummary.from(comment.getUser()),
            comment.getCreatedAt()
        );
    }
}