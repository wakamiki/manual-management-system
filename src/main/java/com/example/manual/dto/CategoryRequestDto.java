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

//„Ç≤„ÉÅEÇø„Éº
  //#region getter
public String getCategoryName() {
return this.categoryName;
}

public Integer getDisplayOrder() {
  //#endregion
  return this.displayOrder;
}

// „Çª„ÉÅEÇø„Éº
  //#region setter
public void setCategoryName(String categoryName) {
  this.categoryName = categoryName;
}

public void setDisplayOrder(Integer displayOrder) {
  //#endregion
  this.displayOrder = displayOrder;
}

}
