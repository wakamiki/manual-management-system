package com.example.manual.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.manual.dto.UserResponseDto;
import com.example.manual.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
  @GetMapping
  public void getAllUsers() {
    //ページ切り替え有
    //ユーザーID　displayネーム　ロール　isActive

  }

  @GetMapping("/{userId}")
  public void getUserById(Principal principal) {
    //ユーザーID　displayネーム　ロール　isActive
  }

  @PostMapping
  public void createUser() {
    //初期状態はアクティブ　loginID・displayNAME・ロール必須　adminのみ実行可　ログインID重複チェック
    //操作者　対象ユーザー　実行日時
  }

  @PutMapping("/{userId}")
  public void updateUser() {
    //氏名変更対応・ロール変更想定　ユーザーID変更可能
    //adminのみ実行可　ログインID重複チェック
    //DB DisplayName role
    //操作者　対象ユーザー　実行日時 loginId displayName Role
  }

  @PutMapping("/{userId}/deactivate")
  public void deactivateUser() {
    //adminのみ実行可
    //操作者　対象ユーザー　実行日時
  }

  @PutMapping("/{userId}/activate")
  public void activateUser() {
    //adminのみ実行可
    //操作者　対象ユーザー　実行日時
  }

  @PutMapping("/{userId}/reset-password")
  public void resetPassword() {
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
    //アクティブのみ　adminのみ実行可
    //操作者　実行日時

  }

//取得系

  //ユーザー管理画面取得
  public UserResponseDto showUserManegementUserManagementPege(
      Principal principal) {

  UserResponseDto userResponseDto = new UserResponseDto();

  return userResponseDto;
  }
}
