package com.example.manual.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

private static final Logger log =
        LoggerFactory.getLogger(LoginController.class);

  @GetMapping("/")
  public String showLoginView(Principal principal) {
    log.info("start");

    return "redirect:/login";
  }

  @GetMapping("/login")
  public String showLoginPage(Principal principal) {
    log.info("start");

    if (principal != null) {
      return "redirect:/manuals/index";
    }
    return "login";
  }

}
