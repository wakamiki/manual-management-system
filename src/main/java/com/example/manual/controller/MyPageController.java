package com.example.manual.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.manual.dto.MyPageDto;
import com.example.manual.service.MyPageService;

@Controller
@RequestMapping("/my-page")
public class MyPageController {

  private static final Logger log = LoggerFactory.getLogger(MyPageController.class);

  private final MyPageService myPageService;

  public MyPageController(MyPageService myPageService) {
    this.myPageService = myPageService;
  }

  // ================================================
  // 取得系
  // =================================================

  // myPage表示
  @GetMapping
  public String showMyPage(Principal principal, Model model) {
    log.info("start");
    MyPageDto myPageDto = myPageService.showMyPage(principal);
    model.addAttribute("myPageDto", myPageDto);
    return "my-page";

  }
}
