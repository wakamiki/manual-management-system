package com.example.manual.controller;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.manual.dto.CategoryDetailDto;
import com.example.manual.dto.CategoryRequestDto;
import com.example.manual.dto.CategoryResponseDto;
import com.example.manual.service.CategoryService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/categories")
public class CategoryController {

private static final Logger log =
        LoggerFactory.getLogger(CategoryController.class);

  public final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  // category-management表示
@GetMapping("/category-management")
public String showCategoryManagement(
    Principal principal, Model model) {
  log.info("start");
        List<CategoryDetailDto>responseDto =
        categoryService.showCategoryManagement(principal);
      model.addAttribute("responseDto",responseDto);
      return "category-management";
  }

  @PostMapping
  public String createCategory(
      @Valid CategoryRequestDto requestDto,
      Principal principal,
      RedirectAttributes redirectAttributes) {
    log.info("start");
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
    log.info("start");
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
    log.info("start");
        categoryService.deactivateCategory(principal);
    redirectAttributes.addFlashAttribute(
        "message", "選択カテゴリーを使用停止にしました。");

    return "redirect:/categories/{categoryId}/deactivate";
  }

  @PutMapping("/{categoryId}/activate")
  public String activateCategory(
      Principal principal,
      RedirectAttributes redirectAttributes) {
    log.info("start");
    categoryService.activateCategory(principal);
    redirectAttributes.addFlashAttribute(
        "message", "選択カテゴリーを有効にしました。");

    return "redirect:/categories/{categoryId}/activate";
  }



  public void getAllCategories() {
    //停止中もすべて取得 adminのみ実行可
    log.info("start");
  }


  public void getAllActiveCategories() {
    //adminのみ実行可
    log.info("start");
  }

}
