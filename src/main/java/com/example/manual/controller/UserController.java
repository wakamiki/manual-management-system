package com.example.manual.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    log.info("[{}][START] args={}");
    UserViewDto viewDto = userService.showUserManagementPage(principal, pageable);
    UserFormDto formDto = new UserFormDto();
    model.addAttribute("viewDto", viewDto);
    model.addAttribute("formDto", formDto);
    log.info("[{}][END] result={}");
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
    log.info("[{}][START] args={}");
    UserViewDto viewDto = userService.showUserUpdateMode(principal, userId, pageable);
    UserFormDto formDto = userService.toFormData(userId);
    model.addAttribute("viewDto", viewDto);
    model.addAttribute("formDto", formDto);
    message.addFlashAttribute("message", "ユーザーを取得しました。");
    message.addFlashAttribute("messageType", "success");
    log.info("[{}][END] result={}");
    return "user-management";
  }

  // パスワード変更画面
  @GetMapping("/change-password")
  public String showChangePasswordPage(Principal principal, Model model) {
    log.info("[{}][START] args={}");
    boolean canGuest = userService.showChangePasswordPage(principal);
    if (!model.containsAttribute("passwordChangeRequestDto")) {
      model.addAttribute("passwordChangeRequestDto", new PasswordChangeRequestDto());
    }
    model.addAttribute("canGuest", canGuest);
    log.info("[{}][END] result={}");
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
      BindingResult bindingResult,
      Model model,
      Pageable pageable) {
    log.info("[{}][START] args={}");
    if (bindingResult.hasErrors()) {
      UserViewDto viewDto = userService.showUserManagementPage(principal, pageable);
      model.addAttribute("formDto", formDto);
      model.addAttribute("viewDto", viewDto);
      model.addAttribute("message", "必須項目が入力されていません。");
      model.addAttribute("messageType", "error");
      log.info("[{}][END] result={}");
      return "user-management";
    }
    if (userPermissionService.isUserIdTaken(formDto)) {
      String duplicateMessage = "userIDは既に使われています。別のuserIDを入力してください。";
      model.addAttribute("duplicateMessage", duplicateMessage);
      UserViewDto viewDto = userService.concertToUserViewDto(formDto, principal, pageable);
      model.addAttribute("viewDto", viewDto);
      model.addAttribute("formDto", formDto);
      log.info("[{}][END] result={}");
      return "user-management";
    }
    String password = userService.createUser(
        formDto, principal);
    String issuedInitialPassword = String.format("新規ユーザーを登録完了。初期パスワードを発行しました。");
    message.addFlashAttribute("message", issuedInitialPassword);
    message.addFlashAttribute("messageType", "success");
    message.addFlashAttribute("isCredentialNotice", true);
    message.addFlashAttribute("issuedPassword", password);
    log.info("[{}][END] result={}");
    return "redirect:/users";
  }

  @PostMapping("/{userId}/update")
  public String updateUser(
      RedirectAttributes message,
      Principal principal,
      @PathVariable Long userId,
      @Valid @ModelAttribute UserFormDto formDto,
      BindingResult bindingResult,
      Model model,
      Pageable pageable) {
    log.info("[{}][START] args={}");
    if (bindingResult.hasErrors()) {
      UserViewDto viewDto = userService.showUserManagementPage(principal, pageable);
      model.addAttribute("formDto", formDto);
      model.addAttribute("viewDto", viewDto);
      model.addAttribute("message", "必須項目が入力されていません。");
      model.addAttribute("messageType", "error");
      log.info("[{}][END] result={}");
      return "user-management";
    }
    if (userService.updateUser(formDto, principal, userId)) {
      String duplicateMessage = "userIDは既に使われています。別のuserIDを入力してください。";
      model.addAttribute("duplicateMessage", duplicateMessage);
      UserViewDto viewDto = userService.concertToUserViewDto(formDto, principal, pageable);
      model.addAttribute("viewDto", viewDto);
      formDto = userService.toFormData(userId);
      model.addAttribute("formDto", formDto);
      log.info("[{}][END] result={}");
      return "user-management";
    }
    message.addFlashAttribute("message", "更新処理が成功しました。");
    message.addFlashAttribute("messageType", "success");
    log.info("[{}][END] result={}");
    return "redirect:/users";
  }

  @PostMapping("/{userId}/deactivate")
  public String deactivateUser(
      RedirectAttributes message,
      Principal principal,
      @PathVariable Long userId,
      @Valid @ModelAttribute UserFormDto formDto,
      BindingResult bindingResult,
      Model model,
      @PageableDefault(size = 10) Pageable pageable) {
    log.info("[{}][START] args={}");
    if (bindingResult.hasErrors()) {
      UserViewDto viewDto = userService.showUserManagementPage(principal, pageable);
      model.addAttribute("formDto", formDto);
      model.addAttribute("viewDto", viewDto);
      model.addAttribute("message", "必須項目が入力されていません。");
      model.addAttribute("messageType", "error");
      log.info("[{}][END] result={}");
      return "user-management";
    }
    // 既存処理
    userService.deactivateUser(principal, formDto, userId);
    message.addFlashAttribute("message", "ユーザーを停止しました。");
    message.addFlashAttribute("messageType", "success");
    log.info("[{}][END] result={}");
    return "redirect:/users";
  }

  @PostMapping("/{userId}/activate")
  public String activateUser(
      RedirectAttributes message,
      @PathVariable Long userId,
      Principal principal,
      @Valid @ModelAttribute UserFormDto formDto,
      BindingResult bindingResult,
      Model model,
      @PageableDefault(size = 10) Pageable pageable) {
    log.info("[{}][START] args={}");
    if (bindingResult.hasErrors()) {
      UserViewDto viewDto = userService.showUserManagementPage(principal, pageable);
      model.addAttribute("formDto", formDto);
      model.addAttribute("viewDto", viewDto);
      model.addAttribute("message", "必須項目が入力されていません。");
      model.addAttribute("messageType", "error");
      log.info("[{}][END] result={}");
      return "user-management";
    }
    // 既存処理
    userService.activateUser(principal, userId);
    message.addFlashAttribute("message", "ユーザーを有効にしました。");
    message.addFlashAttribute("messageType", "success");
    log.info("[{}][END] result={}");
    return "redirect:/users";
  }

  @PostMapping("/{userId}/reset-password")
  public String resetPassword(
      RedirectAttributes message,
      Principal principal,
      @PathVariable Long userId) {
    log.info("[{}][START] args={}");
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
    log.info("[{}][END] result={}");
    return "redirect:/users";
  }

  @PostMapping("/action/change-password")
  public String changePassword(
      @Valid @ModelAttribute PasswordChangeRequestDto passwordDto,
      BindingResult bindingResult,
      Principal principal,
      RedirectAttributes message,
      Model model) {
    log.info("[{}][START] args={}");
    if (bindingResult.hasErrors()) {
      boolean canGuest = userService.showChangePasswordPage(principal);
      model.addAttribute("canGuest", canGuest);
      model.addAttribute("message", "必須項目が入力されていません。");
      model.addAttribute("messageType", "error");
      log.info("[{}][END] result={}");
      return "password-change";
    }
    // 既存処理
    userService.changePassword(principal, passwordDto);
    message.addFlashAttribute("message", "パスワードを変更しました。");
    message.addFlashAttribute("messageType", "success");
    log.info("[{}][END] result={}");
    return "redirect:/manuals/index";
  }

  @GetMapping("/{userId}/operation-histories")
  public String getOperationHistories(RedirectAttributes message) {
    log.info("[{}][START] args={}");
    message.addFlashAttribute("message", "処理に成功しました。");
    message.addFlashAttribute("messageType", "success");
    log.info("[{}][END] result={}");
    return "redirect:/users";
  }

}
