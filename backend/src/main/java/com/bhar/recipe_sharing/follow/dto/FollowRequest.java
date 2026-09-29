package com.bhar.recipe_sharing.follow.dto;

import jakarta.validation.constraints.NotNull;

public record FollowRequest(
    @NotNull(message = "followerId is required")
    Long followerId,

    @NotNull(message = "followingId is required")
    Long followingId
) {
}