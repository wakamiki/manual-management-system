package com.example.manual.controller;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.manual.entity.User;
import com.example.manual.repository.UserRepository;
import com.example.manual.service.UserService;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService,UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public Optional<User> getFindByUserId(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt;
    }

}
