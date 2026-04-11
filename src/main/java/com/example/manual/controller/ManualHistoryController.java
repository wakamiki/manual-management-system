package com.example.manual.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manuals")
public class ManualHistoryController {

  @GetMapping("/{manualId}/histories")
  public void getManualHistories() {}
}
