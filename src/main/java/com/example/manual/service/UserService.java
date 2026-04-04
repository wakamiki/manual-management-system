package com.example.manual.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.manual.entity.Users;
import com.example.manual.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<Users> findByUserId(Long id) {
        return userRepository.findById(id);
    }
}

