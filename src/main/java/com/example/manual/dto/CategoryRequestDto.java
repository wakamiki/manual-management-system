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
private Long id;

//getter
public Long getId(){
  return  this.id;
}

public String getCategoryName() {
return this.categoryName;
}

public Integer getDisplayOrder() {
  return this.displayOrder;
}

// setter
public void setCategoryName(String categoryName) {
  this.categoryName = categoryName;
}

public void setDisplayOrder(Integer displayOrder) {
  this.displayOrder = displayOrder;
}

public void setId(Long id){
  this.id=id;
}
}
