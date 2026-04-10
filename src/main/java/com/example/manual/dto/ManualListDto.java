package com.example.manual.dto;

import java.time.LocalDateTime;

import com.example.manual.enums.ManualStatus;

public class ManualListDto {
  public ManualListDto() {

  }
  private Long manualId;
  private String title;
  private ManualStatus status;
  private String categoryName;
  private LocalDateTime updatedAt;
  private String createdByName;

  //ゲッター
  public Long getManualId() {
    return this.manualId;
  }

  public String getTitle() {
    return this.title;
  }

  public ManualStatus getStatus() {
    return this.status;
  }

  public String getCategoryName() {
    return this.categoryName;
  }

  public LocalDateTime getUpdatedAt() {
    return this.updatedAt;
  }

  public String getCreatedByName() {
    return this.createdByName;
  }

  //セッター
  public void setManualId(Long manualId) {
     this.manualId = manualId;
  }

  public void setTitle(String title) {
     this.title = title;
  }

  public void setStatus(ManualStatus status) {
     this.status = status;
  }

  public void setCategoryName(String categoryName) {
     this.categoryName = categoryName;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
     this.updatedAt = updatedAt;
  }

  public void setCreatedByName(String createdByName) {
     this.createdByName = createdByName;
  }

}
