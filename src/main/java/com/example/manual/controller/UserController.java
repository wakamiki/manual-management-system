package com.example.manual.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  @GetMapping
  public void getAllUsers() {}

  @GetMapping("/{userId}")
  public void getUserById() {}

  @PostMapping
  public void createUser() {}

  @PutMapping("/{userId}")
  public void updateUser() {}

  @PutMapping("/{userId}/deactivate")
  public void deactivateUser() {}

  @PutMapping("/{userId}/activate")
  public void activateUser() {}

  @PutMapping("/{userId}/reset-password")
  public void resetPassword() {}

  @GetMapping("/{userId}/operation-histories")
  public void getOperationHistories() {}
}
