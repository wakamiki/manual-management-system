package com.example.manual.controller;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.manual.dto.UserDetailDto;
import com.example.manual.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

  private static final Logger log =
        LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
      this.userService = userService;
    }


//==============================================
// 取得系
// =============================================

    // user-management表示
    public String showUserManagementPege(
        Principal principal, Model model) {
      log.info("showUserManagementPege start");
      List<UserDetailDto> userResponseDto =
        userService.showUserManagementPege(principal);

      model.addAttribute("manualListDto", userResponseDto);
      return "user-management";
    }

  @GetMapping
  public void getAllUsers() {
    log.info("start");
    //ページ切り替え有
    //ユーザーID　displayネーム　ロール　isActive

  }

  @GetMapping("/{userId}")
  public void getUserById(Principal principal) {
    log.info("start");
    //ユーザーID　displayネーム　ロール　isActive
  }

  @PostMapping
  public void createUser() {
    log.info("start");
    //初期状態はアクティブ　loginID・displayNAME・ロール必須　adminのみ実行可　ログインID重複チェック
    //操作者　対象ユーザー　実行日時
  }

  @PutMapping("/{userId}")
  public void updateUser() {
    log.info("start");
    //氏名変更対応・ロール変更想定　ユーザーID変更可能
    //adminのみ実行可　ログインID重複チェック
    //DB DisplayName role
    //操作者　対象ユーザー　実行日時 loginId displayName Role
  }

  @PutMapping("/{userId}/deactivate")
  public void deactivateUser() {
    log.info("start");
    //adminのみ実行可
    //操作者　対象ユーザー　実行日時
  }

  @PutMapping("/{userId}/activate")
  public void activateUser() {
    log.info("start");
    //adminのみ実行可
    //操作者　対象ユーザー　実行日時
  }

  @PutMapping("/{userId}/reset-password")
  public void resetPassword() {
    log.info("start");
    //アクティブのみ　adminのみ実行可
    //自分自身のリセット禁止　一時パスを通知で渡す
    //初回ログインで強制パスワード変更要請
    //監査ログを記録　操作者・対象・日時・理由を保存
    //連続実行抑止: 連続リセットを制限
    //操作タイプ＋理由をoperationhistoriesに残す
    //操作者　対象ユーザー　実行日時 理由や変更内容
  }

  @GetMapping("/{userId}/operation-histories")
  public void getOperationHistories() {
    log.info("start");
    //アクティブのみ　adminのみ実行可
    //操作者　実行日時

  }
}
