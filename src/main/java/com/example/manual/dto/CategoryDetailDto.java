package com.example.manual.dto;

import java.time.LocalDateTime;

public class CategoryDetailDto {
   public CategoryDetailDto() {

  }

private Long id;
private String categoryName;
private Integer displayOrder;
private boolean isActive;
private LocalDateTime updatedAt;

//getter
public Long getId() {
return this.id;
}

public String getCategoryName() {
return this.categoryName;
}

public Integer getDisplayOrder() {
return this.displayOrder;
}

public boolean isActive() {
  return this.isActive;
}

public LocalDateTime getUpdatedAt(){
  return this.updatedAt;
}

  // setter
public void setId(Long id) {
  this.id = id;
}

public void setCategoryName(String categoryName) {
  this.categoryName = categoryName;
}

public void setDisplayOrder(Integer displayOrder) {
  this.displayOrder = displayOrder;
}

public void setActive(boolean active) {
  this.isActive = active;
}

public void setUpdatedAt(LocalDateTime updatedAt){
  this.updatedAt=updatedAt;
}
}
