package com.example.manual.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.manual.entity.ManualHistory;
import com.example.manual.enums.ManualStatus;

public class ManualDetailDto {
  public ManualDetailDto() {

  }

  private Long manualId;
  private String title;
  private ManualStatus status;
  private Long categoryId;
  private String categoryName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime approvedAt;
  private String createdByName;
  private String content;
  private ManualHistory history;
  private List<ManualHistoryDto> histories;
  private boolean canEdit;
  private boolean canPending;
  private boolean canApprove;
  private boolean canRollback;
  private boolean canArchive;
  private boolean canRestore;
  private boolean canCopy;

  // getter
  public Long getManualId() {
    return this.manualId;
  }

  public String getTitle() {
    return this.title;
  }

  public ManualStatus getStatus() {
    return this.status;
  }

  public Long getCategoryId() {
    return this.categoryId;
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

  public List<ManualHistoryDto> getHistories() {
    return this.histories;
  }

  public boolean isCanEdit() {
    return this.canEdit;
  }

  public boolean isCanPending() {
    return this.canPending;
  }

  public boolean isCanApprove() {
    return this.canApprove;
  }

  public boolean isCanRollback() {
    return this.canRollback;
  }

  public boolean isCanRestore() {
    return this.canRestore;
  }

  public boolean isCanArchive() {
    return canArchive;
  }

  public boolean isCanCopy() {
    return canCopy;
  }

  // setter
  public void setManualId(Long manualId) {
    this.manualId = manualId;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setStatus(ManualStatus status) {
    this.status = status;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
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

  public void setHistories(List<ManualHistoryDto> histories) {

    this.histories = histories;
  }

  public void setCanCopy(boolean canCopy) {
    this.canCopy = canCopy;
  }

  public void setCanRestore(boolean canRestore) {
    this.canRestore = canRestore;
  }

  public void setCanRollback(boolean canRollback) {
    this.canRollback = canRollback;
  }

  public void setCanApprove(boolean canApprove) {
    this.canApprove = canApprove;
  }

  public void setCanPending(boolean canPending) {
    this.canPending = canPending;
  }

  public void setCanEdit(boolean canEdit) {
    this.canEdit = canEdit;
  }

  public void setCanArchive(boolean canArchive) {
    this.canArchive = canArchive;
  }
}
