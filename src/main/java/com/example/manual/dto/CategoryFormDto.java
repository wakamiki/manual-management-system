package com.example.manual.dto;

import java.util.List;

import com.example.manual.enums.ViewMode;

public class CategoryFormDto {
  public CategoryFormDto() {

  }

  private List<CategoryDetailDto> categoryListDto;
  private PagingDto PagingDto;
  private ViewMode mode;
  private UserResponseDto playUser;
  private CategoryDetailDto targetCategory;

  // getter
  public List<CategoryDetailDto> getCategoryListDto() {
    return categoryListDto;
  }

  public ViewMode getMode() {
    return mode;
  }

  public UserResponseDto getPlayUser() {
    return playUser;
  }

  public CategoryDetailDto getTargetCategory() {
    return targetCategory;
  }

  public PagingDto getPagingDto() {
    return PagingDto;
  }

  // setter
  public void setCategoryListDto(List<CategoryDetailDto> categoryListDto) {
    this.categoryListDto = categoryListDto;
  }

  public void setMode(ViewMode mode) {
    this.mode = mode;
  }

  public void setPlayUser(UserResponseDto playUser) {
    this.playUser = playUser;
  }

  public void setTargetCategory(CategoryDetailDto targetCategory) {
    this.targetCategory = targetCategory;
  }

  public void setPagingDto(PagingDto pagingDto) {
    PagingDto = pagingDto;
  }

}
