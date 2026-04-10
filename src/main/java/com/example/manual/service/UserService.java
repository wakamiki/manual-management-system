package com.example.manual.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.manual.entity.Manual;
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

    public void resetPassword() {

    }

    public User userSaved(User user) {
        return userRepository.save(user);
    }

    public User getUserByloginId(String loginId) {
        Optional<User> userOpt = userRepository.findByLoginId(loginId);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "指定したユーザーが存在しません");
        }
        User user = userOpt.get();
        return user;
    }

    public String getDisplayNameByLoginId(String loginId) {
        Optional<User> userOpt = userRepository.findByLoginId(loginId);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "指定したユーザーが存在しません");
        }
        User user = userOpt.get();
        return user.getDisplayName();
    }

    //小メソッド群


}
