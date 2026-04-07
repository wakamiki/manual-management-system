package com.example.manual.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.manual.entity.User;
import com.example.manual.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(){

    }

    public void updateUser(){

    }

    public void cangeRole(){

    }

    public void deactivateUser(){

    }

    public void activateUser(){

    }

    public void resetPassword(){
        
    }

    public Optional<User> findByUserId(Long id) {
        return userRepository.findById(id);
    }
}

