package com.example.manual.service;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.manual.dto.PasswordChangeRequestDto;
import com.example.manual.dto.UserFormDto;
import com.example.manual.entity.User;
import com.example.manual.enums.UserRole;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.exception.NotFoundException;
import com.example.manual.exception.UnauthorizedException;
import com.example.manual.repository.UserRepository;

@Service
public class UserPermissionService {

  private static final Logger log = LoggerFactory.getLogger(UserPermissionService.class);

  private final UserRepository userRepository;

  public UserPermissionService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  // =========================================
  // 権限判定
  // =========================================

  public boolean canUpdateLastLoginAt(User playUser) {
    if (!playUser.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    return true;
  }

  boolean canShowUserManagementPage(User targetUser) {
    log.info("start");
    if (!targetUser.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (targetUser.getRole() != UserRole.ADMIN && targetUser.getRole() != UserRole.GUEST) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    return true;
  }

  public boolean canShowUpdateMode(User targetUser) {
    log.info("start");
    if (!targetUser.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (targetUser.getRole() != UserRole.ADMIN) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    return true;
  }

  public boolean canShowChangePasswordPage(User playUser) {
    log.info("start");
    if (!playUser.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    return true;
  }

  public boolean canCreateUser(
      UserFormDto formDto,
      User playUser) {
    log.info("start");

    if (!playUser.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (playUser.getRole() != UserRole.ADMIN) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    if (formDto.getLoginId() == null || formDto.getLoginId().isBlank()) {
      throw new NotFoundException("userIdは必須入力項目です。");
    }
    if (formDto.getDisplayName() == null || formDto.getDisplayName().isBlank()) {
      throw new NotFoundException("user名は必須入力項目です。");
    }
    if (formDto.getRole() == null) {
      throw new NotFoundException("Roleは必須入力項目です。");
    }
    return true;
  }

  public boolean canUpdateUser(
      UserFormDto formDto,
      User playUser) {
    log.info("start");
    if (!playUser.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (playUser.getRole() != UserRole.ADMIN) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    return true;
  }

  public boolean canDeactivateUser(User targetUser, User playUser) {
    log.info("start");
    if (!playUser.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (playUser.getRole() != UserRole.ADMIN) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    if (!targetUser.isActive()) {
      throw new InvalidStateException("対象のユーザーは既に停止中です。");
    }
    return true;
  }

  public boolean canActivateUser(User targetUser, User playUser) {
    log.info("start");
    if (!playUser.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (playUser.getRole() != UserRole.ADMIN) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    if (targetUser.isActive()) {
      throw new InvalidStateException("対象のユーザーは既に有効になっています。");
    }
    return true;
  }

  public boolean canResetPassword(User playUser) {
    log.info("start");
    if (!playUser.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (playUser.getRole() != UserRole.ADMIN) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    return true;
  }

  public boolean isUserIdTaken(UserFormDto formDto) {
    log.info("start");
    if (formDto.getId() == null) {
      return userRepository.existsByLoginId(formDto.getLoginId());
    }
    return userRepository.existsByLoginIdAndIdNot(formDto.getLoginId(), formDto.getId());
  }

  public boolean canChangePassword(
      User playUser,
      PasswordChangeRequestDto passwordDto) {
    log.info("start");
    if (!playUser.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (playUser.getRole() == UserRole.GUEST) {
      throw new UnauthorizedException("ゲストユーザーにはパスワード変更の権限がありません。");
    }
    if (passwordDto.getConfirmPassword() == null ||
        passwordDto.getCurrentPassword() == null ||
        passwordDto.getNewPassword() == null ||
        passwordDto.getConfirmPassword().isBlank() ||
        passwordDto.getCurrentPassword().isBlank() ||
        passwordDto.getNewPassword().isBlank()) {
      throw new NotFoundException("入力されていない項目があります。");
    }
    if (!Objects.equals(passwordDto.getConfirmPassword(), passwordDto.getNewPassword())) {
      throw new InvalidStateException("新しいパスワードと確認用パスワードが違っています。");
    }

    return true;
  }

  public boolean isGuest(User playUser) {
    log.info("start");
    if (playUser.getRole() == UserRole.GUEST) {
      return true;
    }
    return false;
  }
}
