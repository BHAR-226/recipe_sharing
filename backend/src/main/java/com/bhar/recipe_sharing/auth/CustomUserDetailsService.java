package com.bhar.recipe_sharing.auth;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bhar.recipe_sharing.user.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        var user = userRepository.findByEmailIgnoreCase(email.trim())
            .orElseThrow(() ->
                new UsernameNotFoundException("Invalid email or password")
            );

        return User.withUsername(user.getEmail())
            .password(user.getPwdHash())
            .roles(user.getRole())
            .build();
    }
}