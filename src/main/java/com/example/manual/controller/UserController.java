package com.example.manual.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.manual.dto.PasswordChangeRequestDto;
import com.example.manual.dto.UserFormDto;
import com.example.manual.dto.UserViewDto;
import com.example.manual.service.UserPermissionService;
import com.example.manual.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

  private final UserPermissionService userPermissionService;

  private static final Logger log = LoggerFactory.getLogger(UserController.class);

  private final UserService userService;

  public UserController(UserService userService, UserPermissionService userPermissionService) {
    this.userService = userService;
    this.userPermissionService = userPermissionService;
  }

  // =============================================
  // 画面表示
  // =============================================

  // user-management表示
  @GetMapping
  public String showUserManagementPage(
      Principal principal, Model model,
      @PageableDefault(size = 10) Pageable pageable) {
    log.info("start");
    UserViewDto viewDto = userService.showUserManagementPage(principal, pageable);
    UserFormDto formDto = new UserFormDto();
    model.addAttribute("viewDto", viewDto);
    model.addAttribute("formDto", formDto);
    return "user-management";
  }

  // 更新モード表示
  @GetMapping("/{userId}/action")
  public String showUserUpdateMode(
      Principal principal,
      @PathVariable Long userId,
      RedirectAttributes message,
      Model model,
      @PageableDefault(size = 10) Pageable pageable) {
    log.info("start");
    UserViewDto viewDto = userService.showUserUpdateMode(principal, userId, pageable);
    UserFormDto formDto = userService.toFormData(userId);
    model.addAttribute("viewDto", viewDto);
    model.addAttribute("formDto", formDto);
    message.addFlashAttribute("message", "ユーザーを取得しました。");
    message.addFlashAttribute("messageType", "success");
    return "user-management";
  }

  // パスワード変更画面
  @GetMapping("/change-password")
  public String showChangePasswordPage(Principal principal) {
    log.info("start");
    userService.showChangePasswordPage(principal);
    return "password-change";
  }

  // =============================================
  // DB処理
  // =============================================

  @PostMapping("/create")
  public String createUser(
      RedirectAttributes message,
      Principal principal,
      @Valid @ModelAttribute UserFormDto formDto,
      Model model,
      Pageable pageable) {
    log.info("start");

    if (userPermissionService.isUserIdTaken(formDto)) {
      String duplicateMessage = "userIDは既に使われています。別のuserIDを入力してください。";
      model.addAttribute("duplicateMessage", duplicateMessage);
      UserViewDto viewDto = userService.concertToUserViewDto(formDto, principal, pageable);
      model.addAttribute("viewDto", viewDto);
      model.addAttribute("formDto", formDto);
      return "user-management";
    }
    String password = userService.createUser(
        formDto, principal);
    String issuedInitialPassword = String.format("新規ユーザーを登録完了。初期パスワードを発行しました。");
    message.addFlashAttribute("message", issuedInitialPassword);
    message.addFlashAttribute("messageType", "success");
    message.addFlashAttribute("isCredentialNotice", true);
    message.addFlashAttribute("issuedPassword", password);
    return "redirect:/users";
  }

  @PostMapping("/{userId}/update")
  public String updateUser(
      RedirectAttributes message,
      Principal principal,
      @PathVariable Long userId,
      @Valid @ModelAttribute UserFormDto formDto,
      Model model,
      Pageable pageable) {
    log.info("start");

    if (userService.updateUser(formDto, principal, userId)) {
      String duplicateMessage = "userIDは既に使われています。別のuserIDを入力してください。";
      model.addAttribute("duplicateMessage", duplicateMessage);
      UserViewDto viewDto = userService.concertToUserViewDto(formDto, principal, pageable);
      model.addAttribute("viewDto", viewDto);
      formDto = userService.toFormData(userId);
      model.addAttribute("formDto", formDto);
      return "user-management";
    }
    message.addFlashAttribute("message", "更新処理が成功しました。");
    message.addFlashAttribute("messageType", "success");
    return "redirect:/users";
  }

  @PostMapping("/{userId}/deactivate")
  public String deactivateUser(
      RedirectAttributes message,
      Principal principal,
      @PathVariable Long userId,
      @Valid @ModelAttribute UserFormDto formDto) {
    log.info("start");
    userService.deactivateUser(principal, formDto, userId);
    message.addFlashAttribute("message", "ユーザーを停止しました。");
    message.addFlashAttribute("messageType", "success");
    return "redirect:/users";
  }

  @PostMapping("/{userId}/activate")
  public String activateUser(
      RedirectAttributes message,
      @PathVariable Long userId,
      Principal principal,
      @Valid @ModelAttribute UserFormDto formDto) {
    log.info("start");
    userService.activateUser(principal, userId);
    message.addFlashAttribute("message", "ユーザーを有効にしました。");
    message.addFlashAttribute("messageType", "success");
    return "redirect:/users";
  }

  @PostMapping("/{userId}/reset-password")
  public String resetPassword(
      RedirectAttributes message,
      Principal principal,
      @PathVariable Long userId) {
    log.info("start");
    // 自分自身のリセット禁止 一時パスを通知で渡す
    // 初回ログインで強制パスワード変更要請
    // 連続実行抑止: 連続リセットを制限
    String password = userService.resetPassword(
        principal, userId);
    String issuedInitialPassword = String.format("パスワードを再発行しました。");
    message.addFlashAttribute("message", issuedInitialPassword);
    message.addFlashAttribute("messageType", "warning");
    message.addFlashAttribute("isCredentialNotice", true);
    message.addFlashAttribute("issuedPassword", password);
    return "redirect:/users";
  }

  @PostMapping("/action/change-password")
  public String changePassword(
      @Valid @ModelAttribute PasswordChangeRequestDto passwordDto,
      Principal principal,
      RedirectAttributes message) {
    log.info("start");
    userService.changePassword(principal, passwordDto);
    message.addFlashAttribute("message", "パスワードを変更しました。");
    message.addFlashAttribute("messageType", "success");
    return "redirect:manuals/index";
  }

  @GetMapping("/{userId}/operation-histories")
  public String getOperationHistories(RedirectAttributes message) {
    log.info("start");
    message.addFlashAttribute("message", "処理に成功しました。");
    message.addFlashAttribute("messageType", "success");
    return "redirect:/users";
  }

}
