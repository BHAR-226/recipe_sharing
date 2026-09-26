package com.bhar.recipe_sharing.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bhar.recipe_sharing.user.dto.UserProfileRequest;
import com.bhar.recipe_sharing.user.dto.UserRequest;
import com.bhar.recipe_sharing.user.dto.UserResponse;
import com.bhar.recipe_sharing.user.exception.EmailAlreadyUsedException;
import com.bhar.recipe_sharing.user.exception.UserNotFoundException;
import com.bhar.recipe_sharing.user.exception.UsernameAlreadyUsedException;

@Service 
public class UserService {
    
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService (UserRepository userRepo, PasswordEncoder passwordEncoder){
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<UserResponse> getUsers (Pageable pageable){
        return userRepo.findAll(pageable).map(this::toResponse);
    }

    public UserResponse getUserById(Long id){
        return toResponse(findUserById(id));
    }

    public UserResponse getUserByUsername(String username){
        return toResponse(
            userRepo.findByUsernameIgnoreCase(username)
            .orElseThrow(()-> new UserNotFoundException("User not found : "+ username))
        );
    }

    public UserResponse getUserByEmail(String email){
        return toResponse(
            userRepo.findByEmailIgnoreCase(email)
            .orElseThrow(()-> new UserNotFoundException("User not found : "+ email))
        );
    }

    public UserResponse createUser(UserRequest request){
        String email = normalizeEmail(request.email());

        if (userRepo.existsByEmailIgnoreCase(email)){
            throw new EmailAlreadyUsedException(email);
        }
        if (userRepo.existsByUsernameIgnoreCase(request.username())){
            throw new UsernameAlreadyUsedException(request.username());
        }

        UserModel user= new UserModel();
        user.setEmail(email);
        user.setUsername(request.username());
        user.setPwdHash(passwordEncoder.encode(request.password()));
        return toResponse(userRepo.save(user));
    }

    public UserResponse updateProfile(Long id, UserProfileRequest request){
        UserModel user = findUserById(id);

        String email = normalizeEmail(request.email());
        if (!user.getEmail().equalsIgnoreCase(email)
            && userRepo.existsByEmailIgnoreCase(email)){
            throw new EmailAlreadyUsedException(email);
        }

        String username = request.username();
        if (!user.getUsername().equalsIgnoreCase(username)
            && userRepo.existsByUsernameIgnoreCase(username)){
            throw new UsernameAlreadyUsedException(username);
        }

        user.setEmail(email);
        user.setUsername(username);

        if (request.password() != null && !request.password().isBlank()){
            user.setPwdHash(passwordEncoder.encode(request.password()));
        }

        return toResponse(userRepo.save(user));
    }

    public void deleteUserById (Long id){
        findUserById(id);
        userRepo.deleteById(id);
    }

    public boolean ifUserExist (Long id){
        return userRepo.existsById(id);
    }

    private UserModel findUserById(Long id){
        return userRepo.findById(id)
        .orElseThrow(()-> new UserNotFoundException("User not found : "+ id));
    }

    private String normalizeEmail(String email){
        return email.trim().toLowerCase();
    }

    private UserResponse toResponse(UserModel user){
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getCreatedAt()
        );
    }

}
