package com.bhar.recipe_sharing.follow;

import java.time.LocalDateTime;

import com.bhar.recipe_sharing.user.UserModel;

import jakarta.persistence.*;

@Entity
@Table(name = "follows",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"follower_id", "following_id"})
    }
)
public class FollowModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private UserModel follower;

    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    private UserModel following;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserModel getFollower() {
        return follower;
    }

    public void setFollower(UserModel follower) {
        this.follower = follower;
    }

    public UserModel getFollowing() {
        return following;
    }

    public void setFollowing(UserModel following) {
        this.following = following;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}