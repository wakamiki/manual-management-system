package com.example.manual.dto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.manual.enums.ManualStatus;

public class ManualResponseDto {

  public ManualResponseDto() {
  }

private Long manualId;
private String title="";
private String content="";
private UserResponseDto createdUserDto;
private ManualStatus status;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
private CategoryResponseDto categoryDto;
private List<ManualHistoryDto> histories = new ArrayList<>();



public Long getManualId() {
  return this.manualId;
}

public String getTitle() {
  return this.title;
}

public String getContent() {
  return this.content;
}

public ManualStatus getStatus() {
  return this.status;
}

public LocalDateTime getCreatedAt() {
  return this.createdAt;
}

public LocalDateTime getUpdatedAt() {
  return this.updatedAt;
}

public List<ManualHistoryDto> getHistories() {
  return this.histories;
}

public UserResponseDto getCreatedUserDto() {
  return this.createdUserDto;
}

public CategoryResponseDto getCategoryDto(){
  return this.categoryDto;
}


//setter

public void setManualId(long manualId) {
  this.manualId = manualId;
}

public void setTitle(String title) {
  this.title = title;
}

public void setContent(String content) {
  this.content = content;
}

public void setStatus(ManualStatus status) {
  this.status = status;
}

public void setCreatedAt(LocalDateTime createdAt) {
  this.createdAt = createdAt;
}

public void setUpdatedAt(LocalDateTime updatedAt) {
  this.updatedAt = updatedAt;
}

public void setHistories(List<ManualHistoryDto> histories) {
  this.histories = histories;
}

public void setCreatedUserDto(UserResponseDto createdUserDto) {
  this.createdUserDto = createdUserDto;
}

public void setCategoryDto(CategoryResponseDto categoryDto) {
  this.categoryDto = categoryDto;
}
}
