package com.bhar.recipe_sharing.auth;

import java.io.Serializable;

public record UserPrincipal(
    Long id,
    String username,
    String role
) implements Serializable {
}

