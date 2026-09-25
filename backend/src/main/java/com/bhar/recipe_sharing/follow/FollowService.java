package com.bhar.recipe_sharing.follow;

import com.bhar.recipe_sharing.user.UserRepository;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bhar.recipe_sharing.user.UserModel;

@Service 
public class FollowService {
    private final FollowRepository followRepo;
    private final UserRepository userRepo;
    public FollowService (FollowRepository followRepo, UserRepository userRepo){
        this.followRepo = followRepo;
        this.userRepo = userRepo;
    }

    public void subscribe(Long followerId, Long followingId){
        
        UserModel follower = new UserModel();
        follower = userRepo.findById(followerId)
        .orElseThrow(()->new IllegalArgumentException("follower not found : "+ followerId));

        UserModel following = userRepo.findById(followingId)
        .orElseThrow(() ->new IllegalArgumentException(
                        "Following user not found : " + followingId));

        FollowModel follow = new FollowModel();
        follow.setFollower(follower);
        follow.setFollowing(following);
        
        followRepo.save(follow);
    };

    public void unfollow (Long followerId, Long followingId){
        FollowModel follow = new FollowModel();
        follow = followRepo.findByFollowerIdAndFollowingId(followerId, followingId)
        .orElseThrow(()->new IllegalArgumentException("not found"));
        followRepo.delete(follow);
    }

    public List<UserModel> getFollowers(Long followerId){
        // UserModel follower = new UserModel();
        // follower = userRepo.findById(followerId)
        // .orElseThrow(()->new IllegalArgumentException("follower not found : "+ followerId));

        return followRepo.findByFollowerId(followerId);
    }
    
    public List<UserModel> getFollowing(Long followingId){
        // UserModel following = new UserModel();
        // following = userRepo.findById(followingId)
        // .orElseThrow(()->new IllegalArgumentException("subscription not found : "+ followerId));

        return followRepo.findByFollowingId(followingId);
    }

}
