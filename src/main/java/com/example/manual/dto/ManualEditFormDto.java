package com.example.manual.dto;

import com.example.manual.enums.FormMode;

import jakarta.validation.constraints.Size;

public class ManualEditFormDto {
  public ManualEditFormDto() {

  }
  @Size(max=100)
  private String title;
  @Size(max = 10000)
  private String content;
  private Long categoryId;
  private String categoryName;
  private Long manualId;
  @Size(max = 100)
  private String changeNote;
  private FormMode mode;

  //getter
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

  //setter
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

  public void markCopyMode(FormMode mode) {
    this.mode = mode.copy;
  }

  public void markEdit(FormMode mode) {
    this.mode = mode.edit;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }
}
