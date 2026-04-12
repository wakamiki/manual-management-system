package com.example.manual.service;

import java.security.Principal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.manual.dto.UserRequestDto;
import com.example.manual.dto.UserResponseDto;
import com.example.manual.entity.User;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.repository.UserRepository;

import jakarta.validation.Valid;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final User user;

    public UserService(UserRepository userRepository,User user) {
        this.userRepository = userRepository;
        this.user = user;
    }

    public UserResponseDto createUser(@Valid UserRequestDto requestDto){
        if (!canCreateUser(requestDto)) {
            throw new InvalidStateException("判定エラー");            
        }
        User user = new User();
        user.createNew(requestDto.getLoginId(), requestDto.getDisplayName(), requestDto.getRole());
        return responseDto;
    }

    public UserResponseDto updateUser(@Valid UserRequestDto requestDto,Principal principal){
        if (!canUpdateUser(requestDto,principal)) {
            throw new InvalidStateException("判定エラー");            
        }
        return responseDto;
    }

    public UserResponseDto changeRole(@Valid UserRequestDto requestDto,Principal principal){
        if (!canChangeRole(requestDto,principal)) {
            throw new InvalidStateException("判定エラー");            
        }
        return responseDto;
    }

    public UserResponseDto deactivateUser(Principal principal){
        if (!canDeactivateUser(principal)) {
             throw new InvalidStateException("判定エラー");           
        }
        return responseDto;
    }

    public UserResponseDto activateUser(Principal principal){
        if (!canActivateUser(principal)) {
            throw new InvalidStateException("判定エラー");            
        }
        return responseDto;
    }

    public UserResponseDto resetPassword(Principal principal) {
        if (!canResetPassword(principal)) {
            throw new InvalidStateException("判定エラー");            
        }
        return responseDto;
    }

    public User userSaved(User user,Principal principal) {
        if (!canUserSaved(user,principal)) {
            throw new InvalidStateException("判定エラー");
        }
        return userRepository.save(user);
    }

//取得系

    public User getUserByPrincipal(Principal principal) {
        Optional<User> userOpt = userRepository.findByLoginId(principal.getName());
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

    //権限判定
        private boolean canCreateUser(UserRequestDto requestDto){

        return true;
    }  
        private boolean canUpdateUser(UserRequestDto requestDto){
        return true;
    }
        
        private boolean canChangeRole(UserRequestDto requestDto){
        return true;
    }

        private boolean canDeactivateUser(UserRequestDto requestDto){
        return true;
    }

        private boolean canActivateUser(UserRequestDto requestDto){
        return true;
    }

        private boolean canResetPassword(UserRequestDto requestDto) {
        return true;
    }

        private boolean canUserSaved(UserRequestDto requestDto){
        return true;
    }

}
