package com.example.manual.dto;

import java.util.List;

import com.example.manual.enums.ViewMode;

public class CategoryViewDto {
  public CategoryViewDto() {

  }

  private List<CategoryDetailDto> categoryListDto;
  private PagingDto PagingDto;
  private ViewMode mode;
  private UserResponseDto playUser;
  private boolean canGuest;

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

  public PagingDto getPagingDto() {
    return PagingDto;
  }

  public boolean isCanGuest() {
    return canGuest;
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

  public void setPagingDto(PagingDto pagingDto) {
    PagingDto = pagingDto;
  }

  public void setCanGuest(boolean canGuest) {
    this.canGuest = canGuest;
  }

}
