package com.example.manual.controller;

import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        //ã‚«ãƒE‚´ãƒªå–å¾E
        //ãƒãƒ‹ãƒ¥ã‚¢ãƒ«å–å¾E
        return "index";
    }

}
  //#region ‚±‚Ì•ª‚¯•û‚ğ‚·‚é‚Æ•ª‚©‚è‚â‚·‚¢
  //#endregion
