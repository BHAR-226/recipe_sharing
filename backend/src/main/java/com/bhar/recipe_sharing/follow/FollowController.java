package com.bhar.recipe_sharing.follow;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bhar.recipe_sharing.user.UserModel;

@RestController 
@RequestMapping ("/follow")
public class FollowController {
    private FollowService fService;
    public FollowController (FollowService Service){
        this.fService = Service;
    }
    
    @PostMapping ("/follow")
    public void subscribe(@RequestBody FollowModel follow){
        fService.subscribe(
            follow.getFollower().getId(),
            follow.getFollowing().getId()
        );
    }

    @DeleteMapping ("/unfollow")
    public void unfollow (@RequestBody FollowModel follow){
        //the request body have to content the follower and following id
        fService.unfollow(
            follow.getFollower().getId(),
            follow.getFollowing().getId()
        );
    }

    @GetMapping("/follower/{id}")
    public List<UserModel> getFollower(@PathVariable Long followerId){
        return fService.getFollowers(followerId);
    }
    @GetMapping ("/following/{id}")
    public List<UserModel> getFollowing(@PathVariable  Long followingId){
        return fService.getFollowing(followingId);
    }
}
