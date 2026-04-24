package com.example.manual.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UnauthorizedException.class)
  public String handleUnauthorized(UnauthorizedException e, RedirectAttributes message, HttpServletRequest request) {
    message.addFlashAttribute("message", e.getMessage());
    message.addFlashAttribute("messageType", "error");
    return resolveRedirectPath(request.getRequestURI());
  }

  @ExceptionHandler(InvalidStateException.class)
  public String handleInvalidState(InvalidStateException e, RedirectAttributes message, HttpServletRequest request) {
    message.addFlashAttribute("message", e.getMessage());
    message.addFlashAttribute("messageType", "error");
    return resolveRedirectPath(request.getRequestURI());
  }

  @ExceptionHandler(NotFoundException.class)
  public String handleNotFound(NotFoundException e, RedirectAttributes message, HttpServletRequest request) {
    message.addFlashAttribute("message", e.getMessage());
    message.addFlashAttribute("messageType", "error");
    return resolveRedirectPath(request.getRequestURI());
  }

  private String resolveRedirectPath(String uri) {
    if (uri.startsWith("/users")) {
      return "redirect:/users";
    }
    if (uri.startsWith("/categories")) {
      return "redirect:/categories";
    }
    if (uri.startsWith("/my-page")) {
      return "redirect:/my-page";
    }
    if (uri.startsWith("/login")) {
      return "redirect:/login";
    }
    return "redirect:/manuals/index";
  }

}
