package com.example.manual.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CategoryFormDto {

  public CategoryFormDto() {

  }

  private Long id;
  @NotBlank(message = "categoryNameは必須です。")
  @Size(max = 50)
  private String categoryName;
  @NotNull(message = "displayOrderは必須です。")
  @Min(value = 1, message = "displayOrderは1以上で入力してください。")
  @Max(value = 1000, message = "displayOrderは1000以下で入力してください。")
  private Integer displayOrder;
  private boolean isActive;
  private boolean confirmed = false;

  // getter

  public Long getId() {
    return this.id;
  }

  public String getCategoryName() {
    return this.categoryName;
  }

  public Integer getDisplayOrder() {
    return this.displayOrder;
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  public boolean isActive() {
    return isActive;
  }

  // setter
  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public void setDisplayOrder(Integer displayOrder) {
    this.displayOrder = displayOrder;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setConfirmed(boolean confirmed) {
    this.confirmed = confirmed;
  }

  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }
}
