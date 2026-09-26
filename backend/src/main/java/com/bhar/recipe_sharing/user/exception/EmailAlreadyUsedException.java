package com.bhar.recipe_sharing.user.exception;

public class EmailAlreadyUsedException extends RuntimeException {

    public EmailAlreadyUsedException(String email) {
        super("Email already used : " + email);
    }
}
