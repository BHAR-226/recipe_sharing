package com.bhar.recipe_sharing.user;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserModel, Long>{

    public Optional<UserModel> findByEmailIgnoreCase(String email);

    public Optional<UserModel> findByUsernameIgnoreCase(String username);

    public boolean existsByEmailIgnoreCase(String email);

    public boolean existsByUsernameIgnoreCase(String username);
}
