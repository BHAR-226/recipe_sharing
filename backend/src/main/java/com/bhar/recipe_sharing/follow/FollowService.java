package com.bhar.recipe_sharing.follow;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bhar.recipe_sharing.exception.NotFoundException;
import com.bhar.recipe_sharing.user.UserModel;
import com.bhar.recipe_sharing.user.UserRepository;
import com.bhar.recipe_sharing.user.dto.UserSummary;

@Service 
public class FollowService {
    private final FollowRepository followRepo;
    private final UserRepository userRepo;
    public FollowService (FollowRepository followRepo, UserRepository userRepo){
        this.followRepo = followRepo;
        this.userRepo = userRepo;
    }

    public void subscribe(Long followerId, Long followingId){
        
        UserModel follower = userRepo.findById(followerId)
        .orElseThrow(()->new NotFoundException("User not found : "+ followerId));

        UserModel following = userRepo.findById(followingId)
        .orElseThrow(() ->new NotFoundException(
                        "Following user not found : " + followingId));

        FollowModel follow = new FollowModel();
        follow.setFollower(follower);
        follow.setFollowing(following);
        
        followRepo.save(follow);
    }

    public void unfollow (Long followerId, Long followingId){
        FollowModel follow = followRepo.findByFollowerIdAndFollowingId(followerId, followingId)
        .orElseThrow(()->new NotFoundException("Follow not found : follower "
            + followerId + " -> following " + followingId));
        followRepo.delete(follow);
    }

    public List<UserSummary> getFollowers(Long userId) {
        requireUser(userId);
        return followRepo.findFollowersOf(userId)
            .stream()
            .map(UserSummary::from)
            .toList();
    }

    public List<UserSummary> getFollowing(Long userId) {
        requireUser(userId);
        return followRepo.findFollowingOf(userId)
            .stream()
            .map(UserSummary::from)
            .toList();
    }

    private UserModel requireUser(Long userId) {
        return userRepo.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found : " + userId));
    }

}