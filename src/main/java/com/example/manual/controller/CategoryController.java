package com.example.manual.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.manual.dto.CategoryRequestDto;
import com.example.manual.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categories")
public class CategoryController {

  public final CategoryService categoryService;

  public CategoryController(CategoryService categoryService){
    this.categoryService = categoryService;
  }

  @PostMapping
  public String createCategory(
      @Valid CategoryRequestDto requestDto,
      Principal principal,
      RedirectAttributes redirectAttributes) {
    categoryService.createCategory(requestDto, principal);
    redirectAttributes.addFlashAttribute(
        "message", "新しいカテゴリーを作成しました。");
      return "redirect:/categories";
  }



  @PutMapping("/{categoryId}")
  public String updateCategory(
      @Valid CategoryRequestDto requestDto,
      Principal principal,
      RedirectAttributes redirectAttributes) {
      categoryService.updateCategory(
          requestDto.getId(),
          requestDto,
          principal);
      redirectAttributes.addFlashAttribute(
          "message", "カテゴリーを更新しました。");

      return "redirect:/categories/{categoryId}";

  }


  @PutMapping("/{categoryId}/deactivate")
  public String deactivateCategory(
      Principal principal,
      RedirectAttributes redirectAttributes) {
    categoryService.deactivateCategory(principal);
    redirectAttributes.addFlashAttribute(
        "message", "選択カテゴリーを使用停止にしました。");

    return "redirect:/categories/{categoryId}/deactivate";
  }

  @PutMapping("/{categoryId}/activate")
  public String activateCategory(
      Principal principal,
      RedirectAttributes redirectAttributes) {

    categoryService.activateCategory(principal);
    redirectAttributes.addFlashAttribute(
        "message", "選択カテゴリーを有効にしました。");

    return "redirect:/categories/{categoryId}/activate";
  }


  @GetMapping
  public void getAllCategories() {
    //停止中もすべて取得 adminのみ実行可
  }

    @GetMapping
  public void getAllActiveCategories() {
    //adminのみ実行可
  }

}
