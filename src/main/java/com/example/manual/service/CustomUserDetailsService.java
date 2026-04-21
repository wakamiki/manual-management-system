package com.example.manual.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.manual.entity.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

  private final UserService userService;

  public CustomUserDetailsService(UserService userService) {
    this.userService = userService;
  }

  @Override
  public UserDetails loadUserByUsername(String loginId) {
    log.info("start");
    User targetUser = userService.getUserByLoginId(loginId);

    UserDetails userDetails = org.springframework.security.core.userdetails.User
        .withUsername(targetUser.getLoginId())
        .password(targetUser.getPassword())
        .roles(targetUser.getRole().name())
        .build();

    return userDetails;
  }
}
