package com.example.manual.dto;

import com.example.manual.enums.FormMode;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ManualDraftDto {
    public ManualDraftDto() {

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getManualId() {
        return manualId;
    }

    public void setManualId(Long manualId) {
        this.manualId = manualId;
    }

    public String getChangeNote() {
        return changeNote;
    }

    public void setChangeNote(String changeNote) {
        this.changeNote = changeNote;
    }

    public FormMode getMode() {
        return mode;
    }

    public void setMode(FormMode mode) {
        this.mode = mode;
    }

    public String getPendingSubmit() {
        return pendingSubmit;
    }

    public void setPendingSubmit(String pendingSubmit) {
        this.pendingSubmit = pendingSubmit;
    }

    public String getDraftSubmit() {
        return draftSubmit;
    }

    public void setDraftSubmit(String draftSubmit) {
        this.draftSubmit = draftSubmit;
    }

    public boolean isGuest() {
        return guest;
    }

    public void setGuest(boolean guest) {
        this.guest = guest;
    }

    public String getModeLabel() {
        return modeLabel;
    }

    public void setModeLabel(String modeLabel) {
        this.modeLabel = modeLabel;
    }
}
