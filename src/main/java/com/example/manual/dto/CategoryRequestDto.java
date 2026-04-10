package com.example.manual.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CategoryRequestDto {

  public CategoryRequestDto() {

  }

@NotBlank
@Size(max=50)
private String categoryName;
@NotNull
private Integer displayOrder;

//ゲッター
public String getCategoryName() {
return this.categoryName;
}

public Integer getDisplayOrder() {
  return this.displayOrder;
}

// セッター
public void setCategoryName(String categoryName) {
  this.categoryName = categoryName;
}

public void setDisplayOrder(Integer displayOrder) {
  this.displayOrder = displayOrder;
}

}
