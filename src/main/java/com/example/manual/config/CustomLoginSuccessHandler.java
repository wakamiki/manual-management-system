package com.example.manual.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.manual.entity.User;
import com.example.manual.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
    private static final Logger log = LoggerFactory.getLogger(CustomLoginSuccessHandler.class);
    private final UserService userService;

    public CustomLoginSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException {

        String loginId = authentication.getName();
        try {
            userService.updateLastLoginAt(loginId);
        } catch (Exception e) {
            log.warn("Failed to update lastLoginAt. loginId={}", loginId, e);
        }
        User playUser = userService.getUserByLoginId(loginId);
        if (playUser.isPasswordChangeRequired()) {
            // パスワード変更画面
            response.sendRedirect(request.getContextPath() + "/users/change-password");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/manuals/index");
    }
}