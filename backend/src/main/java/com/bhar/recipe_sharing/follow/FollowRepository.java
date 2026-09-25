package com.bhar.recipe_sharing.follow;

import java.util.List;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bhar.recipe_sharing.user.UserModel;

public interface FollowRepository extends JpaRepository<FollowModel, Long>{
    
    public List<UserModel> findByFollowingId (Long followingId);
    public List<UserModel> findByFollowerId(Long followerId);
    public Optional<FollowModel> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
}