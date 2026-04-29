package com.example.manual.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

  private static final Logger log = LoggerFactory.getLogger(LoginController.class);

  private final String guestLoginId;
  private final String guestPassword;
  private final AuthenticationManager authenticationManager;

  public LoginController(
      @Value("${app.guest-login.login-id}") String guestLoginId,
      @Value("${app.guest-login.password}") String guestPassword,
      AuthenticationManager authenticationManager) {
    this.guestLoginId = guestLoginId;
    this.guestPassword = guestPassword;
    this.authenticationManager = authenticationManager;
  }

  @GetMapping("/")
  public String showLoginView(Principal principal, RedirectAttributes message) {
    return "redirect:/login";
  }

  @GetMapping("/login")
  public String showLoginPage(Principal principal, RedirectAttributes message) {
    if (principal != null) {
      return "redirect:/manuals/index";
    }
    return "login";
  }

  @PostMapping("/login/guest")
  public String showGuestLogin(HttpServletRequest request, RedirectAttributes message) {
    try {
      if (guestLoginId == null || guestLoginId.isBlank() ||
          guestPassword == null || guestPassword.isBlank()) {
        throw new AuthenticationCredentialsNotFoundException("guest login config missing");
      }

      Authentication auth = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(guestLoginId, guestPassword));

      SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(auth);
      SecurityContextHolder.setContext(context);
      request.getSession(true).setAttribute(
          HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
          context);

      return "redirect:/manuals/index";
    } catch (AuthenticationException e) {
      log.warn("guest login failed. loginId={}, reason={}", guestLoginId, e.getClass().getSimpleName(), e);
      message.addFlashAttribute("message", "ゲストログインに失敗しました。");
      message.addFlashAttribute("messageType", "error");
      return "redirect:/login";
    }
  }
}
