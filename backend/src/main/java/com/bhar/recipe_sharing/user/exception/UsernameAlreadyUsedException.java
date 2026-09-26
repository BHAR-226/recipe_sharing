package com.bhar.recipe_sharing.user.exception;

public class UsernameAlreadyUsedException extends RuntimeException {

    public UsernameAlreadyUsedException(String username) {
        super("Username already used : " + username);
    }
}
