package com.example.manual.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.manual.entity.Users;
import com.example.manual.service.UserService;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService UserService;

    public UserController(UserService userService) {
        this.UserService = userService;
    }

    @GetMapping("/{id}")
    public Long findByUserId(@PathVariable Long id) {
        Optional<Users> userOpt = UserService.findByUserId(id);
        if (userOpt.isPresent()) {
            return userOpt.get().getId();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "指定したユーザーが存在しません");
    }

    
}
