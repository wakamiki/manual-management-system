package com.example.manual.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.manual.service.ManualService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class LoginController {

private static final Logger log =
        LoggerFactory.getLogger(LoginController.class);

  @GetMapping("/login")
  public String showLoginView(Principal principal) {
    log.info("start");

    return"login";
  }

}
