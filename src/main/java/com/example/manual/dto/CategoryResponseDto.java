package com.example.manual.dto;

public class CategoryResponseDto {

  public CategoryResponseDto() {

  }

private Long id;
private String categoryName;

//getter
public Long getId() {
return this.id;
}

public String getCategoryName() {
return this.categoryName;
}

  // setter
public void setId(Long id) {
  this.id = id;
}

public void setCategoryName(String categoryName) {
  this.categoryName = categoryName;
}

}
