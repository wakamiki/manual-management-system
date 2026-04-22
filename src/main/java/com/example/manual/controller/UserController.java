package com.example.manual.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.manual.dto.UserFormDto;
import com.example.manual.dto.UserRequestDto;
import com.example.manual.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

  private static final Logger log = LoggerFactory.getLogger(UserController.class);

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  // =============================================
  // 画面表示
  // =============================================

  // user-management表示
  @GetMapping
  public String showUserManagementPage(
      Principal principal, Model model) {
    log.info("start");
    UserFormDto formDto = userService.showUserManagementPage(principal);
    model.addAttribute("formDto", formDto);
    return "user-management";
  }

  // 更新モード表示
  @GetMapping("/{userId}/action")
  public String showUserUpdateMode(
      Principal principal,
      @PathVariable Long userId,
      RedirectAttributes message,
      Model model) {
    log.info("start");
    try {
      UserFormDto formDto = userService.showUserUpdateMode(principal, userId);
      model.addAttribute("formDto", formDto);
      message.addFlashAttribute("message", "ユーザーを取得しました。");
      message.addFlashAttribute("messageType", "success");
      return "user-management";
    } catch (Exception e) {
      message.addFlashAttribute("message", "処理中にエラーが発生しました。");
      message.addFlashAttribute("messageType", "error");
      return "user-management";
    }
  }

  // =============================================
  // DB処理
  // =============================================

  @PostMapping("/create")
  public String createUser(
      RedirectAttributes message,
      Principal principal,
      @Valid @ModelAttribute UserRequestDto requestDto,
      Model model) {
    log.info("start");
    try {
      if (userService.createUser(
          requestDto, principal)) {
        String duplicateMessage = "userIDは既に使われています。別のuserIDを入力してください。";
        model.addAttribute("duplicateMessage", duplicateMessage);
        UserFormDto formDto = userService.concertToUserFormDto(requestDto, principal);
        model.addAttribute("formDto", formDto);
        return "user-management";
      }

      message.addFlashAttribute("message", "新規ユーザーを作成しました。");
      message.addFlashAttribute("messageType", "success");
      return "redirect:/users";
    } catch (Exception e) {
      message.addFlashAttribute("message", "処理中にエラーが発生しました。");
      message.addFlashAttribute("messageType", "error");
      return "redirect:/users";
      // 操作者 対象ユーザー 実行日時
    }
  }

  @PostMapping("/{userId}/update")
  public String updateUser(
      RedirectAttributes message,
      Principal principal,
      @PathVariable Long id,
      @Valid @ModelAttribute UserRequestDto requestDto,
      Model model) {
    log.info("start");
    try {
      if (userService.updateUser(requestDto, principal, id)) {
        String duplicateMessage = "userIDは既に使われています。別のuserIDを入力してください。";
        model.addAttribute("duplicateMessage", duplicateMessage);
        UserFormDto formDto = userService.concertToUserFormDto(requestDto, principal);
        model.addAttribute("formDto", formDto);
        return "user-management";
      }
      message.addFlashAttribute("message", "処理に成功しました。");
      message.addFlashAttribute("messageType", "success");
      return "redirect:/users";
    } catch (Exception e) {
      message.addFlashAttribute("message", "処理中にエラーが発生しました。");
      message.addFlashAttribute("messageType", "error");
      return "redirect:/users";
      // 操作者 対象ユーザー 実行日時 loginId displayName Role
    }
  }

  @PostMapping("/{userId}/deactivate")
  public String deactivateUser(
      RedirectAttributes message,
      Principal principal,
      @PathVariable Long id,
      @Valid @ModelAttribute UserRequestDto requestDto) {
    log.info("start");
    try {
      // 操作者 対象ユーザー 実行日時
      userService.deactivateUser(principal, requestDto, id);
      message.addFlashAttribute("message", "処理に成功しました。");
      message.addFlashAttribute("messageType", "success");
      return "redirect:/users";
    } catch (Exception e) {
      message.addFlashAttribute("message", "処理中にエラーが発生しました。");
      message.addFlashAttribute("messageType", "error");
      return "redirect:/users";
    }
  }

  @PostMapping("/{userId}/activate")
  public String activateUser(
      RedirectAttributes message,
      @PathVariable Long id,
      Principal principal,
      @Valid @ModelAttribute UserRequestDto requestDto) {
    log.info("start");
    try {
      // adminのみ実行可
      // 操作者 対象ユーザー 実行日時
      userService.activateUser(principal, id);
      message.addFlashAttribute("message", "処理に成功しました。");
      message.addFlashAttribute("messageType", "success");
      return "redirect:/users";
    } catch (Exception e) {
      message.addFlashAttribute("message", "処理中にエラーが発生しました。");
      message.addFlashAttribute("messageType", "error");
      return "redirect:/users";
    }
  }

  @PostMapping("/{userId}/reset-password")
  public String resetPassword(
      RedirectAttributes message,
      Principal principal,
      @PathVariable Long id) {
    log.info("start");
    try {
      // アクティブのみ adminのみ実行可
      // 自分自身のリセット禁止 一時パスを通知で渡す
      // 初回ログインで強制パスワード変更要請
      // 監査ログを記録 操作者・対象・日時・理由を保存
      // 連続実行抑止: 連続リセットを制限
      // 操作タイプ＋理由をoperationhistoriesに残す
      // 操作者 対象ユーザー 実行日時 理由や変更内容
      // 採番idで対象ユーザー取得が安全
      // 監査 誰が誰をリセットしたかログに残す
      userService.resetPassword(principal, id);
      message.addFlashAttribute("message", "処理に成功しました。");
      message.addFlashAttribute("messageType", "success");
      return "RedirectAttributes:/users";
    } catch (Exception e) {
      message.addFlashAttribute("message", "処理中にエラーが発生しました。");
      message.addFlashAttribute("messageType", "error");
      return "RedirectAttributes:/users";
    }
  }

  @GetMapping("/{userId}/operation-histories")
  public String getOperationHistories(RedirectAttributes message) {
    log.info("start");
    try {
      // アクティブのみ adminのみ実行可
      // 操作者 実行日時
      message.addFlashAttribute("message", "処理に成功しました。");
      message.addFlashAttribute("messageType", "success");
      return "redirect:/users";
    } catch (Exception e) {
      message.addFlashAttribute("message", "処理中にエラーが発生しました。");
      message.addFlashAttribute("messageType", "error");
      return "redirect:/users";
    }
  }

}
