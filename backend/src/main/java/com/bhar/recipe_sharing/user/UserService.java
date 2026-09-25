package com.bhar.recipe_sharing.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service 
public class UserService {
    
    private final UserRepository userRepo;

    public UserService (UserRepository userRepo){
        this.userRepo = userRepo;
    }

    
    public List<UserModel> getUsers (){
        return userRepo.findAll();
    }
    public UserModel getUserById(Long id){
        return userRepo.findById(id)
        .orElseThrow(()-> new IllegalArgumentException("User not found : "+ id));
    }
    public UserModel getUserByName(String name){
        return userRepo.findByUsername(name)
        .orElseThrow(()-> new IllegalArgumentException("User not found : "+ name));

    }
    public UserModel getUserByEmail(String email){
        return userRepo.findByEmail(email)
        .orElseThrow(()-> new IllegalArgumentException("User not found : "+ email));

    }
    public UserModel createUser(String email, String username, String pwdHash,
         String role){
        UserModel user= new UserModel();
        user.setEmail(email);
        user.setUsername(username);
        user.setPwdHash(pwdHash);
        user.setRole(role);
        return userRepo.save(user);
    }

    public UserModel editUser(Long id, String username) {

        UserModel user = userRepo.findById(id).orElseThrow(
            ()-> new RuntimeException("User not found"));

        user.setUsername(username);

        return userRepo.save(user);
}
    public void deleteUserById (Long id){
        userRepo.deleteById(id);
    }
    public boolean ifUserExist (Long id){
        return userRepo.existsById(id);
    }

}
