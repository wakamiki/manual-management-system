package com.example.manual.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.manual.enums.ManualStatus;
import com.example.manual.entity.ManualHistory;

public class ManualDetailDto {
  public ManualDetailDto() {

  }

  private Long manualId;
  private String title;
  private ManualStatus status;
  private String categoryName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime approvedAt;
  private String createdByName;
  private String content;
  private ManualHistory history;
  private List<ManualHistory> histories;


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

  public LocalDateTime getCreatedAt() {
    return this.createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return this.updatedAt;
  }

  public LocalDateTime getApprovedAt() {
    return this.approvedAt;
  }

  public String getCreatedByName() {
    return this.createdByName;
  }

  public String getContent() {
    return this.content;
  }

  public ManualHistory getHistory() {
    return this.history;
  }

  public List<ManualHistory> getHistories() {
    return this.histories;
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

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public void setApprovedAt(LocalDateTime approvedAt) {
    this.approvedAt = approvedAt;
  }

  public void setCreatedByName(String createdByName) {
    this.createdByName = createdByName;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setHistory(ManualHistory history) {
    this.history = history;
  }

  public void setHistories(List<ManualHistory> histories) {
    this.histories = histories;
  }

}
