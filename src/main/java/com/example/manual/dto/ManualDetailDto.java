package com.example.manual.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.manual.enums.ManualStatus;

public class ManualDetailDto {
  public ManualDetailDto() {

  }

  private Long manualId;
  private String title;
  private ManualStatus status;
  private CategoryResponseDto categoryDto;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime approvedAt;
  private UserResponseDto createUserDto;
  private String content;
  private List<ManualDetailHistoryDto> histories;
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

  public LocalDateTime getCreatedAt() {
    return this.createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return this.updatedAt;
  }

  public LocalDateTime getApprovedAt() {
    return this.approvedAt;
  }

  public String getContent() {
    return this.content;
  }

  public List<ManualDetailHistoryDto> getHistories() {
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

  public CategoryResponseDto getCategoryDto() {
    return categoryDto;
  }

  public UserResponseDto getCreateUserDto() {
    return createUserDto;
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

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public void setApprovedAt(LocalDateTime approvedAt) {
    this.approvedAt = approvedAt;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setHistories(List<ManualDetailHistoryDto> histories) {

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

  public void setCategoryDto(CategoryResponseDto categoryDto) {
    this.categoryDto = categoryDto;
  }

  public void setCreateUserDto(UserResponseDto createUserDto) {
    this.createUserDto = createUserDto;
  }
}
