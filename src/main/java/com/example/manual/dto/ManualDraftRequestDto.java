package com.example.manual.dto;

import com.example.manual.enums.ManualStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ManualDraftRequestDto {
    public ManualDraftRequestDto() {

    }
    @Size(max=100)
    private String title;
    @Size(max=10000)
    private String content;
    @NotNull
    private Long categoryId;
    @Size(max=100)
    private String changeNote;
    private ManualStatus status;

    //ゲッター
    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public String getChangeNote() {
        return this.changeNote;
    }

    public ManualStatus getStatus() {
        return this.status;
    }

    //セッター
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setChangeNote(String changeNote) {
        this.changeNote = changeNote;
    }

    public void setStatus(ManualStatus status) {
        this.status = status;
    }
}
