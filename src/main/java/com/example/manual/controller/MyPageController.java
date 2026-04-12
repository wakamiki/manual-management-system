package com.example.manual.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/my-page")
public class MyPageController {

  @GetMapping
  public void getMyPage() {
  //必要な情報　差し戻し通知　未承認通知
  //通知マニュアル情報（更新日時、マニュアルID　マニュアルタイトル）
  }

    @PostMapping
  public void getUserCreatedManual(){
    //ステータス　マニュアルID　更新日時　マニュアルタイトル　
  }

  @GetMapping
  public void getPendingManual(){
    //自分が作成したマニュアルは承認できないので出てきて欲しくない。
    //マニュアルID　作成者　マニュアルタイトル　更新日時
  }
}
