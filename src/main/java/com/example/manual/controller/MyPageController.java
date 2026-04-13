package com.example.manual.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.manual.dto.MyPageDto;
import com.example.manual.service.MyPageService;

@RestController
@RequestMapping("/my-page")
public class MyPageController {

  private final MyPageService myPageService;

  public MyPageController(MyPageService myPageService){
    this.myPageService=myPageService;
  }

//取得系

  @GetMapping
  public MyPageDto showMyPage(Principal principal) {
  return myPageService.getMyPageData(principal);
  }
}
