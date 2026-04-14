package com.example.manual.dto;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.catalina.User;

import com.example.manual.enums.ManualStatus;

public class ManualResponseDto {

  public ManualResponseDto() {
  }

private Long manualId;
private String title;
private String content;
private String categoryName;
private String changeNote;
private String createdName;
private User changedByName;
private ManualStatus status;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
private LocalDateTime approvedAt;
private LocalDateTime changedAt;
private Long categoryId;
private List<ManualHistoryDto> histries;
private boolean isRolledBack = false;


public Long getManualId() {
  return this.manualId;
}

public Long getCategoryId(){
  return this.categoryId;
}

public String getTitle() {
  return this.title;
}

public String getContent() {
  return this.content;
}

public String getCategoryName() {
  return this.categoryName;
}

public String getChangeNote() {
  return this.changeNote;
}

public String getCreatedName() {
  return this.createdName;
}

public User getChangedByName() {
  return this.changedByName;
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

public LocalDateTime getApprovedAt() {
  return this.approvedAt;
}

public LocalDateTime getChangedAt() {

  return this.changedAt;
}

public List<ManualHistoryDto> getHistries() {
  return this.histries;
}

public boolean isRolledBack() {
  return this.isRolledBack;
}


//setter

public void setManualId(long manualId) {
  this.manualId = manualId;
}

public void setCategoryId(long categoryId){
  this.categoryId=categoryId;
}

public void setTitle(String title) {
  this.title = title;
}

public void setContent(String content) {
  this.content = content;
}

public void setCategoryName(String categoryName) {
  this.categoryName = categoryName;
}

public void setChangeNote(String changeNote) {
  this.changeNote = changeNote;
}

public void setCreatedName(String createdName) {
  this.createdName = createdName;
}

public void setChangedByName(User changedByName) {
  this.changedByName = changedByName;
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

public void setApprovedAt(LocalDateTime approvedAt) {
  this.approvedAt = approvedAt;
}

public void setChangedAt(LocalDateTime changedAt) {

  this.changedAt = changedAt;
}

public void setHistories(List<ManualHistoryDto> histories) {
  this.histries = histories;
}

public void setRolledBack(boolean isRolledBack) {
  this.isRolledBack = isRolledBack;
}

}
