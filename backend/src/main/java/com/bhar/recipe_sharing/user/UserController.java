package com.bhar.recipe_sharing.user;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping () 
public class UserController {
    private UserService uService;
    public UserController (UserService service){
        this.uService = service;
    }

    @GetMapping ("/user")
    public List<UserModel> getUsers(){
        return uService.getUsers();
    }

    @GetMapping("/user/{id}")
    public UserModel getUser(@PathVariable Long id){
        return uService.getUserById(id);
    }

    @GetMapping("/user/{email}")
    public UserModel getUser( @PathVariable String email){
        return uService.getUserByEmail(email);
    }
    @GetMapping("/user/{name}")
    public UserModel getUserByEmail ( @PathVariable String name){
        return uService.getUserByName(name);
    }

    @PostMapping("/user/create-user")
    public UserModel addUser(@RequestBody UserModel user){

        return uService.createUser(
            user.getEmail(),
            user.getUsername(),
            user.getPwdHash(),
            user.getRole()
        );
    }

    @PutMapping ("/user/edit-user")
    public UserModel editUser (@RequestBody UserModel user){
        return uService.editUser(
            user.getId(),
            user.getUsername()
        );
    }

    @DeleteMapping("/user/{id}")
    public void deleteUser(@PathVariable Long id){
        uService.deleteUserById(id);
    }

}
