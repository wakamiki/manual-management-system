package com.example.manual.dto;
import java.time.LocalDateTime;

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
private String displayName;
private User changedByName;
private ManualStatus status;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
private LocalDateTime approvedAt;
private LocalDateTime changedAt;
  //#region getter
public Long getManualId() {
  return this.manualId;
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

public String getDisplayName() {
  return this.displayName;
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
  //#endregion
  //#region setter
public void setManualId(long manualId) {
  this.manualId = manualId;
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

public void setDysplayName(String displayName) {
  this.displayName = displayName;
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
  //#endregion
}
