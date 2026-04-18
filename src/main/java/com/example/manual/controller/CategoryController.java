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
@GetMapping
public String showCategoryManagement(
    Principal principal, Model model,
  RedirectAttributes message) {
  log.info("start");
  try {
        List<CategoryDetailDto>responseDto =
        categoryService.showCategoryManagement(principal);
      model.addAttribute("responseDto",responseDto);
      return "category-management";
   } catch (Exception e) {
    message.addFlashAttribute("message", "画面取得に失敗しました。");
    message.addFlashAttribute("messageType", "error");
    return "redirect:/categories";
  }
  }

  @PostMapping("/categories/create")
  public String createCategory(
      @Valid CategoryRequestDto requestDto,
      Principal principal,
      RedirectAttributes message) {
    log.info("start");
    try {
    categoryService.createCategory(requestDto, principal);

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



  @PutMapping("/categories/{categoryId}")
  public String updateCategory(
      @Valid CategoryRequestDto requestDto,
      Principal principal,
      RedirectAttributes message) {
    log.info("start");
  try {
        categoryService.updateCategory(
          requestDto.getId(),
          requestDto,
          principal);
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


  @PutMapping("/categories/{categoryId}/deactivate")
  public String deactivateCategory(
      Principal principal,
      RedirectAttributes message) {
    log.info("start");
    try {
        categoryService.deactivateCategory(principal);
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

  @PutMapping("/categories/{categoryId}/activate")
  public String activateCategory(
      Principal principal,
      RedirectAttributes message) {
    log.info("start");
  try {
    categoryService.activateCategory(principal);
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
    //停止中もすべて取得 adminのみ実行可
    log.info("start");
  }


  public void getAllActiveCategories() {
    //adminのみ実行可
    log.info("start");
  }

}
