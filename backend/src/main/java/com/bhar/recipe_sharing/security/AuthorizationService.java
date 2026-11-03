package com.bhar.recipe_sharing.security;

import org.springframework.stereotype.Service;

import com.bhar.recipe_sharing.exception.ForbiddenException;
import com.bhar.recipe_sharing.exception.NotFoundException;
import com.bhar.recipe_sharing.user.UserModel;
import com.bhar.recipe_sharing.user.UserRepository;

@Service
public class AuthorizationService {

    private final UserRepository userRepository;

    public AuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserModel currentUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new NotFoundException("User not found: " + email));
    }

    public boolean isAdmin(UserModel user) {
        return "ADMIN".equalsIgnoreCase(user.getRole());
    }

    public void requireOwnerOrAdmin(Long ownerId, String currentEmail) {
        UserModel current = currentUser(currentEmail);
        if (!isAdmin(current) && !current.getId().equals(ownerId)) {
            throw new ForbiddenException(
                "You are not allowed to modify this resource"
            );
        }
    }

    public void requireSelfOrAdmin(Long targetUserId, String currentEmail) {
        UserModel current = currentUser(currentEmail);
        if (!isAdmin(current) && !current.getId().equals(targetUserId)) {
            throw new ForbiddenException(
                "You are not allowed to modify this resource"
            );
        }
    }
}
