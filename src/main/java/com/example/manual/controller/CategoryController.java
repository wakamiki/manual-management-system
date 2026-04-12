package com.example.manual.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
public class CategoryController {

  @GetMapping
  public void getAllCategories() {}

  @PostMapping
  public void createCategory() {}

  @PutMapping("/{categoryId}")
  public void updateCategory() {}

  @PutMapping("/{categoryId}/deactivate")
  public void deactivateCategory() {}

  @PutMapping("/{categoryId}/activate")
  public void activateCategory() {}
}
