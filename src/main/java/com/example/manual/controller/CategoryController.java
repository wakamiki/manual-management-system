package com.example.manual.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.manual.dto.CategoryRequestDto;
import com.example.manual.dto.CategoryResponseDto;
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
  public CategoryResponseDto createCategory(@Valid CategoryRequestDto requestDto,Principal principal) {
    return categoryService.createCategory(requestDto, principal);
  }



  @PutMapping("/{categoryId}")
  public CategoryResponseDto updateCategory(@Valid CategoryRequestDto requestDto,Principal principal) {
    return categoryService.updateCategory(requestDto.getId(), requestDto, principal);
  }


  @PutMapping("/{categoryId}/deactivate")
  public CategoryResponseDto deactivateCategory(Principal principal) {
    return categoryService.deactivateCategory(principal);
  }

  @PutMapping("/{categoryId}/activate")
  public CategoryResponseDto activateCategory(Principal principal) {
    return categoryService.activateCategory(principal);
  }

  
  @GetMapping
  public void getAllCategories() {
    //停止中もすべて取得 adminのみ実行可
  }

    @GetMapping
  public void getAllActivCategories() {
    //adminのみ実行可
  }

}
