package com.example.manual.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.manual.dto.CategoryFormDto;
import com.example.manual.dto.CategoryRequestDto;
import com.example.manual.service.CategoryService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/categories")
public class CategoryController {

  private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

  public final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  // category-management表示
  @GetMapping
  public String showCategoryManagement(
      Principal principal, Model model) {
    log.info("start");
    CategoryFormDto categoryDto = categoryService.showCategoryManagement(principal);
    model.addAttribute("categoryDto", categoryDto);
    return "category-management";
  }

  @PostMapping("/create")
  public String createCategory(
      @Valid @ModelAttribute CategoryRequestDto requestDto,
      Principal principal,
      RedirectAttributes message,
      Model model) {
    log.info("start");
    try {
      if (categoryService.createCategory(
          requestDto, principal)) {
        boolean duplicate = true;
        String duplicateMessage = "入力されたカテゴリー名は既に存在しています。この名前で新規作成しますか？";
        model.addAttribute("duplicate", duplicate);
        model.addAttribute("duplicateMessage", duplicateMessage);
        CategoryFormDto formDto = categoryService.convertToCategoryFormDto(requestDto, principal);
        model.addAttribute("formDto", formDto);
        return "category-management";
      }

      message.addFlashAttribute(
          "message", "新しいカテゴリーを作成しました。");
      message.addFlashAttribute("messageType", "success");
      return "redirect:/categories";
    } catch (Exception e) {
      message.addFlashAttribute("message", "カテゴリー作成に失敗しました。");
      message.addFlashAttribute("messageType", "error");
      return "redirect:/categories";
    }
  }

  @PostMapping("/update")
  public String updateCategory(
      @Valid @ModelAttribute CategoryRequestDto requestDto,
      Principal principal,
      RedirectAttributes message,
      Model model) {
    log.info("start");
    try {
      if (categoryService.updateCategory(requestDto, principal)) {
        boolean duplicate = true;
        String duplicateMessage = "入力されたカテゴリー名は既に存在しています。この名前で新規作成しますか？";
        model.addAttribute("duplicate", duplicate);
        model.addAttribute("duplicateMessage", duplicateMessage);
        CategoryFormDto formDto = categoryService.convertToCategoryFormDto(requestDto, principal);
        model.addAttribute("formDto", formDto);
        return "category-management";
      }
      message.addFlashAttribute(
          "message", "カテゴリーを更新しました。");
      message.addFlashAttribute("messageType", "success");
      return "redirect:/categories";
    } catch (Exception e) {
      message.addFlashAttribute("message", "更新に失敗しました。");
      message.addFlashAttribute("messageType", "error");
      return "redirect:/categories";
    }

  }

  @PostMapping("/{categoryId}/deactivate")
  public String deactivateCategory(
      Principal principal,
      @PathVariable Long categoryId,
      RedirectAttributes message) {
    log.info("start");
    try {
      categoryService.deactivateCategory(principal, categoryId);
      message.addFlashAttribute(
          "message", "選択カテゴリーを使用停止にしました。");
      message.addFlashAttribute("messageType", "success");
      return "redirect:/categories";
    } catch (Exception e) {
      message.addFlashAttribute("message", "使用停止に失敗しました。");
      message.addFlashAttribute("messageType", "error");
      return "redirect:/categories";
    }
  }

  @PostMapping("/{categoryId}/activate")
  public String activateCategory(
      Principal principal,
      @PathVariable Long categoryId,
      RedirectAttributes message) {
    log.info("start");
    try {
      categoryService.activateCategory(principal, categoryId);
      message.addFlashAttribute(
          "message", "選択カテゴリーを有効にしました。");
      message.addFlashAttribute("messageType", "success");
      return "redirect:/categories";
    } catch (Exception e) {
      message.addFlashAttribute("message", "カテゴリー有効に失敗しました。");
      message.addFlashAttribute("messageType", "error");
      return "redirect:/categories";
    }
  }

  public void getAllCategories() {
    // 停止中もすべて取得 adminのみ実行可
    log.info("start");
  }

  public void getAllActiveCategories() {
    // adminのみ実行可
    log.info("start");
  }

}
