package com.example.manual.controller;

import java.security.Principal;

public class MyPageController {

  //小メソッド群
      public String getLoginId(Principal principal) {
        String loginId = principal.getName();
        return loginId;
    }
}
