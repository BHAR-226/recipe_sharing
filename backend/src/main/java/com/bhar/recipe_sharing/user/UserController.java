package com.bhar.recipe_sharing.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bhar.recipe_sharing.user.dto.UserProfileRequest;
import com.bhar.recipe_sharing.user.dto.UserRequest;
import com.bhar.recipe_sharing.user.dto.UserResponse;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/users")
public class UserController {
    private UserService uService;
    public UserController (UserService service){
        this.uService = service;
    }

    @GetMapping
    public Page<UserResponse> getUsers(@PageableDefault(size = 20) Pageable pageable){
        return uService.getUsers(pageable);
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id){
        return uService.getUserById(id);
    }

    @GetMapping("/username/{username}")
    public UserResponse getUserByUsername(@PathVariable String username){
        return uService.getUserByUsername(username);
    }

    @GetMapping("/email/{email}")
    public UserResponse getUserByEmail(@PathVariable String email){
        return uService.getUserByEmail(email);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request){
        UserResponse created = uService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public UserResponse updateProfile(@PathVariable Long id,
        @Valid @RequestBody UserProfileRequest request){
        return uService.updateProfile(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        uService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

}
