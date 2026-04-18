package com.example.manual.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

private static final Logger log =
        LoggerFactory.getLogger(LoginController.class);

  @GetMapping("/")
  public String showLoginView(Principal principal,
    RedirectAttributes message) {
    log.info("start");

    return "redirect:/login";
  }

  @GetMapping("/login")
  public String showLoginPage(Principal principal,
    RedirectAttributes message) {
    log.info("start");

    if (principal != null) {
      return "redirect:/manuals/index";
    }
    message.addFlashAttribute("message", "ログインに失敗しました。");
    message.addFlashAttribute("messageType", "error");
    return "login";
  }

}
