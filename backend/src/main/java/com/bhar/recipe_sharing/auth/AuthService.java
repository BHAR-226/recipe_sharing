package com.bhar.recipe_sharing.auth;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bhar.recipe_sharing.auth.dto.AuthResponse;
import com.bhar.recipe_sharing.auth.dto.LoginRequest;
import com.bhar.recipe_sharing.exception.NotFoundException;
import com.bhar.recipe_sharing.user.UserModel;
import com.bhar.recipe_sharing.user.UserRepository;
import com.bhar.recipe_sharing.user.UserService;
import com.bhar.recipe_sharing.user.dto.UserRequest;
import com.bhar.recipe_sharing.user.dto.UserResponse;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
        UserRepository userRepo,
        UserService userService,
        PasswordEncoder passwordEncoder,
        JwtService jwtService
    ) {
        this.userRepo = userRepo;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {
        String login = request.login().trim();

        UserModel user = userRepo.findByEmailIgnoreCase(login)
            .or(() -> userRepo.findByUsernameIgnoreCase(login))
            .orElseThrow(() -> new BadCredentialsException("Identifiants incorrects"));

        if (!passwordEncoder.matches(request.password(), user.getPwdHash())) {
            throw new BadCredentialsException("Identifiants incorrects");
        }

        String token = jwtService.generateToken(user);
        return AuthResponse.of(token, UserResponse.from(user));
    }

    public AuthResponse register(UserRequest request) {
        UserResponse createdResponse = userService.createUser(request);
        UserModel user = userRepo.findById(createdResponse.id())
            .orElseThrow(() -> new NotFoundException("User not found : " + createdResponse.id()));

        String token = jwtService.generateToken(user);
        return AuthResponse.of(token, createdResponse);
    }

    public UserResponse me(UserPrincipal principal) {
        if (principal == null || principal.id() == null) {
            throw new BadCredentialsException("Utilisateur non authentifié");
        }
        UserModel user = userRepo.findById(principal.id())
            .orElseThrow(() -> new NotFoundException("User not found : " + principal.id()));
        return UserResponse.from(user);
    }
}
