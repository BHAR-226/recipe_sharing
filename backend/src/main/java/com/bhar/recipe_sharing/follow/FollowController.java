package com.bhar.recipe_sharing.follow;

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

import com.bhar.recipe_sharing.follow.dto.FollowRequest;
import com.bhar.recipe_sharing.user.dto.UserSummary;

import jakarta.validation.Valid;

@RestController 
@RequestMapping ("/api/follow")
public class FollowController {
    private FollowService fService;
    public FollowController (FollowService Service){
        this.fService = Service;
    }
    
    @PostMapping ("/follow")
    public ResponseEntity<Void> subscribe(@Valid @RequestBody FollowRequest request){
        fService.subscribe(
            request.followerId(),
            request.followingId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

   @DeleteMapping("/{followerId}/{followingId}")
   @ResponseStatus(HttpStatus.NO_CONTENT)   
   public void delete(@PathVariable Long followerId, 
    @PathVariable Long followingId){
        fService.unfollow(
            followerId,
            followingId
        );
    }

   @GetMapping("/followers/{userId}")
    public List<UserSummary> getFollowers(@PathVariable Long userId) {
        return fService.getFollowers(userId);
}

    @GetMapping("/following/{userId}")
    public List<UserSummary> getFollowing(@PathVariable Long userId) {
        return fService.getFollowing(userId);
}
}