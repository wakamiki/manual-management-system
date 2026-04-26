package com.example.manual.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.manual.dto.CategoryFormDto;
import com.example.manual.dto.CategoryViewDto;
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

  // =============================================
  // 画面表示
  // =============================================

  // category-management表示
  @GetMapping
  public String showCategoryManagement(
      Principal principal,
      @PageableDefault(size = 10) Pageable pageable,
      Model model) {
    log.info("start");
    CategoryViewDto viewDto = categoryService.showCategoryManagement(principal, pageable);
    CategoryFormDto formDto = new CategoryFormDto();
    model.addAttribute("viewDto", viewDto);
    model.addAttribute("formDto", formDto);
    return "category-management";
  }

  // 変更モード表示
  @GetMapping("/{categoryId}/action")
  public String showCategoryUpdateMode(
      Principal principal,
      @PathVariable Long categoryId,
      RedirectAttributes message,
      @PageableDefault(size = 10) Pageable pageable,
      Model model) {
    log.info("start");
    CategoryFormDto formDto = categoryService.toFormDto(categoryId);
    model.addAttribute("formDto", formDto);
    CategoryViewDto viewDto = categoryService.showCategoryUpdateMode(principal, categoryId, pageable);
    model.addAttribute("viewDto", viewDto);
    message.addFlashAttribute("message", "カテゴリーを取得しました。");
    message.addFlashAttribute("messageType", "success");
    return "category-management";
  }

  // =============================================
  // DB処理
  // =============================================

  @PostMapping("/create")
  public String createCategory(
      @Valid @ModelAttribute CategoryFormDto formDto,
      BindingResult bindingResult,
      Principal principal,
      RedirectAttributes message,
      Model model,
      Pageable pageable) {
    log.info("start");
    if (bindingResult.hasErrors()) {
      CategoryViewDto viewDto = categoryService.showCategoryManagement(principal, pageable);
      model.addAttribute("formDto", formDto);
      model.addAttribute("viewDto", viewDto);
      model.addAttribute("message", "必須項目が入力されていません。");
      model.addAttribute("messageType", "error");
      return "category-management";
    }
    // 重複チェック処理
    if (categoryService.createCategory(
        formDto, principal)) {
      boolean duplicate = true;
      String duplicateMessage = "入力されたカテゴリー名は既に存在しています。この名前で新規作成しますか？";
      model.addAttribute("duplicate", duplicate);
      model.addAttribute("duplicateMessage", duplicateMessage);
      CategoryViewDto viewDto = categoryService.convertToCategoryViewDto(formDto, principal, pageable);
      model.addAttribute("formDto", formDto);
      model.addAttribute("viewDto", viewDto);
      return "category-management";
    }
    // 既存処理
    message.addFlashAttribute(
        "message", "新しいカテゴリーを作成しました。");
    message.addFlashAttribute("messageType", "success");
    return "redirect:/categories";
  }

  @PostMapping("/{categoryId}/update")
  public String updateCategory(
      @Valid @ModelAttribute CategoryFormDto formDto,
      BindingResult bindingResult,
      Principal principal,
      RedirectAttributes message,
      Model model,
      @PageableDefault(size = 10) Pageable pageable) {
    log.info("start");
    if (bindingResult.hasErrors()) {
      CategoryViewDto viewDto = categoryService.showCategoryManagement(principal, pageable);
      model.addAttribute("formDto", formDto);
      model.addAttribute("viewDto", viewDto);
      model.addAttribute("message", "必須項目が入力されていません。");
      model.addAttribute("messageType", "error");
      return "category-management";
    }
    // カテゴリー名重複チェック
    if (formDto.isConfirmed()) {
      // カテゴリー名重複了承 回避処理
    } else if (categoryService.isCategoryNameTaken(formDto)) {
      boolean duplicate = true;
      String duplicateMessage = "入力されたカテゴリー名は既に存在しています。この名前で新規作成しますか？";
      model.addAttribute("duplicate", duplicate);
      model.addAttribute("duplicateMessage", duplicateMessage);
      CategoryViewDto viewDto = categoryService.convertToCategoryViewDto(formDto, principal, pageable);
      model.addAttribute("formDto", formDto);
      model.addAttribute("viewDto", viewDto);
      return "category-management";
    }
    // 既存処理
    categoryService.updateCategory(formDto, principal);
    message.addFlashAttribute(
        "message", "カテゴリーを更新しました。");
    message.addFlashAttribute("messageType", "success");
    return "redirect:/categories";
  }

  @PostMapping("/{categoryId}/deactivate")
  public String deactivateCategory(
      Principal principal,
      @PathVariable Long categoryId,
      RedirectAttributes message) {
    log.info("start");
    categoryService.deactivateCategory(principal, categoryId);
    message.addFlashAttribute(
        "message", "選択カテゴリーを使用停止にしました。");
    message.addFlashAttribute("messageType", "success");
    return "redirect:/categories";
  }

  @PostMapping("/{categoryId}/activate")
  public String activateCategory(
      Principal principal,
      @PathVariable Long categoryId,
      RedirectAttributes message) {
    log.info("start");
    categoryService.activateCategory(principal, categoryId);
    message.addFlashAttribute(
        "message", "選択カテゴリーを有効にしました。");
    message.addFlashAttribute("messageType", "success");
    return "redirect:/categories";
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
