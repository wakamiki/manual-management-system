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
import com.example.manual.enums.DuplicateStatus;
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
    log.info("[{}][START] args={}");
    CategoryViewDto viewDto = categoryService.showCategoryManagement(principal, pageable);
    CategoryFormDto formDto = new CategoryFormDto();
    model.addAttribute("viewDto", viewDto);
    model.addAttribute("formDto", formDto);
    log.info("[{}][END] result={}");
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
    log.info("[{}][START] args={}");
    CategoryFormDto formDto = categoryService.toFormDto(categoryId);
    model.addAttribute("formDto", formDto);
    CategoryViewDto viewDto = categoryService.showCategoryUpdateMode(principal, categoryId, pageable);
    model.addAttribute("viewDto", viewDto);
    message.addFlashAttribute("message", "カテゴリーを取得しました。");
    message.addFlashAttribute("messageType", "success");
    log.info("[{}][END] result={}");
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
    log.info("[{}][START] args={}");
    // @validエラー処理
    if (bindingResult.hasErrors()) {
      CategoryViewDto viewDto = categoryService.showCategoryManagement(principal, pageable);
      model.addAttribute("formDto", formDto);
      model.addAttribute("viewDto", viewDto);
      model.addAttribute("message", "必須項目が入力されていません。");
      model.addAttribute("messageType", "error");
      log.info("[{}][END] result={}");
      return "category-management";
    }

    CategoryFormDto resultDto = categoryService.createCategory(formDto, principal);
    // カテゴリー名重複チェック
    String url = handleDuplicateStatus(resultDto, principal, pageable, model);
    if (resultDto.getDuplicateStatus() != DuplicateStatus.NONE) {
      log.info("[{}][END] result={}");
      return url;
    }
    // 通常処理
    message.addFlashAttribute("message", "新しいカテゴリーを作成しました。");
    message.addFlashAttribute("messageType", "success");
    log.info("[{}][END] result={}");
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
    log.info("[{}][START] args={}");
    // @validエラー処理
    if (bindingResult.hasErrors()) {
      CategoryViewDto viewDto = categoryService.showCategoryManagement(principal, pageable);
      model.addAttribute("formDto", formDto);
      model.addAttribute("viewDto", viewDto);
      model.addAttribute("message", "必須項目が入力されていません。");
      model.addAttribute("messageType", "error");
      log.info("[{}][END] result={}");
      return "category-management";
    }
    CategoryFormDto resultDto = categoryService.updateCategory(formDto, principal);
    // カテゴリー名重複チェック
    String url = handleDuplicateStatus(resultDto, principal, pageable, model);
    if (resultDto.getDuplicateStatus() != DuplicateStatus.NONE) {
      log.info("[{}][END] result={}");
      return url;
    }
    // 通常処理
    message.addFlashAttribute(
        "message", "カテゴリーを更新しました。");
    message.addFlashAttribute("messageType", "success");
    log.info("[{}][END] result={}");
    return "redirect:/categories";
  }

  @PostMapping("/{categoryId}/deactivate")
  public String deactivateCategory(
      Principal principal,
      @PathVariable Long categoryId,
      RedirectAttributes message) {
    log.info("[{}][START] args={}");
    categoryService.deactivateCategory(principal, categoryId);
    message.addFlashAttribute(
        "message", "選択カテゴリーを使用停止にしました。");
    message.addFlashAttribute("messageType", "success");
    log.info("[{}][END] result={}");
    return "redirect:/categories";
  }

  @PostMapping("/{categoryId}/activate")
  public String activateCategory(
      Principal principal,
      @PathVariable Long categoryId,
      RedirectAttributes message) {
    log.info("[{}][START] args={}");
    categoryService.activateCategory(principal, categoryId);
    message.addFlashAttribute(
        "message", "選択カテゴリーを有効にしました。");
    message.addFlashAttribute("messageType", "success");
    log.info("[{}][END] result={}");
    return "redirect:/categories";
  }

  public void getAllCategories() {
    // 停止中もすべて取得 adminのみ実行可
    log.info("[{}][START] args={}");
  }

  public void getAllActiveCategories() {
    // adminのみ実行可
    log.info("[{}][START] args={}");
  }

  // ===================================================
  // 共通処理
  // ===================================================

  private String handleDuplicateStatus(
      CategoryFormDto resultDto,
      Principal principal,
      Pageable pageable,
      Model model) {
    if (resultDto.getDuplicateStatus() == DuplicateStatus.ACTIVE_DUPLICATE) {
      CategoryViewDto viewDto = categoryService.convertToCategoryViewDto(resultDto, principal, pageable);
      model.addAttribute("formDto", resultDto);
      model.addAttribute("viewDto", viewDto);
      model.addAttribute("message", "同名カテゴリの使用中カテゴリがあります。別の名前を使用してください。");
      model.addAttribute("messageType", "error");
      log.info("[{}][END] result={}");
      return "category-management";
    }
    if (resultDto.getDuplicateStatus() == DuplicateStatus.INACTIVE_DUPLICATE) {
      CategoryViewDto viewDto = categoryService.convertToCategoryViewDto(resultDto, principal, pageable);
      model.addAttribute("formDto", resultDto);
      model.addAttribute("viewDto", viewDto);
      model.addAttribute("duplicate", true);
      model.addAttribute("duplicateMessage", "入力されたカテゴリー名は既に存在しています。この名前で新規作成しますか？");
      log.info("[{}][END] result={}");
      return "category-management";
    }
    log.info("[{}][END] result={}");
    return "category-management";
  }

}
