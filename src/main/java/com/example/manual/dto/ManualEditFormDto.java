package com.example.manual.dto;

import com.example.manual.enums.FormMode;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ManualEditFormDto {
  public ManualEditFormDto() {

  }

  @Size(max = 100)
  private String title;
  @Size(max = 10000)
  private String content;
  @NotNull(message = "カテゴリは必須です。")
  private Long categoryId;
  private String categoryName;
  private Long manualId;
  @Size(max = 100)
  private String changeNote;
  private FormMode mode;
  private String pendingSubmit;
  private String draftSubmit;
  private boolean guest = false;
  private String modeLabel;

  // getter
  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

  public Long getManualId() {
    return manualId;
  }

  public String getChangeNote() {
    return changeNote;
  }

  public FormMode getMode() {
    return mode;
  }

  public Long getCategoryId() {
    return categoryId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public String getDraftSubmit() {
    return draftSubmit;
  }

  public String getPendingSubmit() {
    return pendingSubmit;
  }

  public boolean getGuest() {
    return guest;
  }

  public String getModeLabel() {
    return modeLabel;
  }

  // setter
  public void setManualId(Long manualId) {
    this.manualId = manualId;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setChangeNote(String changeNote) {
    this.changeNote = changeNote;
  }

  public void setMode(FormMode mode) {
    this.mode = mode;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public void setPendingSubmit(String pendingSubmit) {
    this.pendingSubmit = pendingSubmit;
  }

  public void setDraftSubmit(String draftSubmit) {
    this.draftSubmit = draftSubmit;
  }

  public void setGuest(boolean guest) {
    this.guest = guest;
  }

  public void setModeLabel(String modeLabel) {
    this.modeLabel = modeLabel;
  }
}
