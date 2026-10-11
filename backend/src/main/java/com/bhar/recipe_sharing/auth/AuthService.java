package com.bhar.recipe_sharing.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bhar.recipe_sharing.user.UserModel;
import com.bhar.recipe_sharing.user.UserRepository;
import com.bhar.recipe_sharing.user.dto.UserRequest;
import com.bhar.recipe_sharing.user.dto.UserResponse;
import com.bhar.recipe_sharing.user.exception.EmailAlreadyUsedException;
import com.bhar.recipe_sharing.user.exception.UsernameAlreadyUsedException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse register(UserRequest request) {
        String email = request.email().trim();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyUsedException("Email is already registered");
        }

        if (userRepository.existsByUsernameIgnoreCase(request.username().trim())) {
            throw new UsernameAlreadyUsedException("Username is already taken");
        }

    UserModel user = new UserModel();
        user.setUsername(request.username().trim());
        user.setEmail(email);
        user.setPwdHash(passwordEncoder.encode(request.password()));
        user.setRole("USER");

        UserModel saved = userRepository.save(user);

        return new UserResponse(
            saved.getId(),
            saved.getUsername(),
            saved.getEmail(),
            saved.getRole(),
            saved.getProfileImageUrl(),
            saved.getCreatedAt()
        );
    }
}