package com.bhar.recipe_sharing.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;

import com.bhar.recipe_sharing.user.UserRepository;
import com.bhar.recipe_sharing.user.dto.UserRequest;
import com.bhar.recipe_sharing.user.dto.UserResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bhar.recipe_sharing.user.UserModel;
import com.bhar.recipe_sharing.auth.dto.LoginRequest;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final SessionAuthenticationStrategy sessionAuthenticationStrategy;
    private final UserRepository userRepository;

    public AuthController(
        AuthService authService,
        AuthenticationManager authenticationManager,
        SecurityContextRepository securityContextRepository,
        SessionAuthenticationStrategy sessionAuthenticationStrategy,
        UserRepository userRepository
    ) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.sessionAuthenticationStrategy = sessionAuthenticationStrategy;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
        @Valid @RequestBody UserRequest request
    ) {
        UserResponse created = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
        @Valid @RequestBody LoginRequest request,
        HttpServletRequest httpRequest,
        HttpServletResponse httpResponse
) {
    try {
        Authentication authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(
                request.email().trim(),
                request.password()
            )
        );

        sessionAuthenticationStrategy.onAuthentication(
            authentication, httpRequest, httpResponse
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
            context, httpRequest, httpResponse
        );

        UserModel user = userRepository
            .findByEmailIgnoreCase(authentication.getName())
            .orElseThrow();

        return ResponseEntity.ok(toResponse(user));

    } catch (BadCredentialsException ex) {
        SecurityContextHolder.clearContext();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Invalid email or password");
    }
}

@GetMapping("/me")
public UserResponse me(Authentication authentication) {
    UserModel user = userRepository
        .findByEmailIgnoreCase(authentication.getName())
        .orElseThrow();

    return toResponse(user);
}

@PostMapping("/logout")
public ResponseEntity<Void> logout(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication
) {
    new SecurityContextLogoutHandler().logout(
        request, response, authentication
    );

    return ResponseEntity.noContent().build();
}

@GetMapping("/csrf")
public String csrf(CsrfToken csrfToken) {
    return csrfToken.getToken();
}

    private UserResponse toResponse(UserModel user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getProfileImageUrl(),
            user.getCreatedAt()
        );
    }

}