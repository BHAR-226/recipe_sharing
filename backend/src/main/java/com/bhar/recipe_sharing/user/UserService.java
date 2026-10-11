package com.bhar.recipe_sharing.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bhar.recipe_sharing.user.dto.UserProfileRequest;
import com.bhar.recipe_sharing.user.dto.UserRequest;
import com.bhar.recipe_sharing.user.dto.UserResponse;
import com.bhar.recipe_sharing.exception.NotFoundException;
import com.bhar.recipe_sharing.security.AuthorizationService;
import com.bhar.recipe_sharing.storage.FileStorageService;
import com.bhar.recipe_sharing.user.exception.EmailAlreadyUsedException;
import com.bhar.recipe_sharing.user.exception.UsernameAlreadyUsedException;

@Service 
public class UserService {
    
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorage;
    private final AuthorizationService authorization;

    public UserService (UserRepository userRepo, PasswordEncoder passwordEncoder,
        FileStorageService fileStorage, AuthorizationService authorization){
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.fileStorage = fileStorage;
        this.authorization = authorization;
    }

    public Page<UserResponse> getUsers (Pageable pageable){
        return userRepo.findAll(pageable).map(this::toResponse);
    }

    public UserResponse getUserById(Long id){
        return toResponse(findUserById(id));
    }

    public UserResponse getUserByUsername(String username){
        return toResponse(
            userRepo.findByUsernameContainingIgnoreCase(username)
            .orElseThrow(()-> new NotFoundException("User not found : "+ username))
        );
    }

    public UserResponse getUserByEmail(String email){
        return toResponse(
            userRepo.findByEmailIgnoreCase(email)
            .orElseThrow(()-> new NotFoundException("User not found : "+ email))
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

    public UserResponse updateProfile(Long id, UserProfileRequest request, String currentEmail){
        authorization.requireSelfOrAdmin(id, currentEmail);

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

    public void deleteUserById (Long id, String currentEmail){
        authorization.requireSelfOrAdmin(id, currentEmail);
        UserModel user = findUserById(id);
        fileStorage.delete(user.getProfileImageUrl());
        userRepo.deleteById(id);
    }

    public UserResponse uploadProfileImage(Long id, MultipartFile file, String currentEmail){
        authorization.requireSelfOrAdmin(id, currentEmail);
        UserModel user = findUserById(id);

        String newUrl = fileStorage.store(file, FileStorageService.PROFILES);
        String oldUrl = user.getProfileImageUrl();
        user.setProfileImageUrl(newUrl);
        UserResponse response = toResponse(userRepo.save(user));

        if (oldUrl != null && !oldUrl.equals(newUrl)){
            fileStorage.delete(oldUrl);
        }
        return response;
    }

    public UserResponse deleteProfileImage(Long id, String currentEmail){
        authorization.requireSelfOrAdmin(id, currentEmail);
        UserModel user = findUserById(id);

        String oldUrl = user.getProfileImageUrl();
        user.setProfileImageUrl(null);
        UserResponse response = toResponse(userRepo.save(user));
        fileStorage.delete(oldUrl);
        return response;
    }

    public boolean ifUserExist (Long id){
        return userRepo.existsById(id);
    }

    private UserModel findUserById(Long id){
        return userRepo.findById(id)
        .orElseThrow(()-> new NotFoundException("User not found : "+ id));
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
            user.getProfileImageUrl(),
            user.getCreatedAt()
        );
    }

}
