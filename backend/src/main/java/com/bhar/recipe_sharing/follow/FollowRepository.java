package com.bhar.recipe_sharing.follow;

import java.util.List;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bhar.recipe_sharing.user.UserModel;

public interface FollowRepository extends JpaRepository<FollowModel, Long>{

    @Query("SELECT f.follower FROM FollowModel f WHERE f.following.id = :userId")
    public List<UserModel> findFollowersOf(@Param("userId") Long userId);

    @Query("SELECT f.following FROM FollowModel f WHERE f.follower.id = :userId")
    public List<UserModel> findFollowingOf(@Param("userId") Long userId);

    public Optional<FollowModel> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
}