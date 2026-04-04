package com.example.manual.controller;

import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        //カテゴリ取得
        //マニュアル取得
        return "index";
    }

}