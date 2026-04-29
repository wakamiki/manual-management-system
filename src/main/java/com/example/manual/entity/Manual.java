package com.example.manual.entity;

import java.time.LocalDateTime;

import com.example.manual.enums.ManualStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "manuals")
public class Manual {

    public Manual() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "operated_by_user_id")
    private User createdByUser;
    @Column(nullable = false, length = 100)
    private String title;
    @Column(nullable = false, length = 10000)
    private String content;
    // 業務上の公開状態を表す
    @Enumerated(EnumType.STRING)
    private ManualStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime approvedAt;
    @Column(name = "is_rolled_back", nullable = false)
    private boolean isRolledback = false;

    // getter
    public Long getId() {
        return this.id;
    }

    public Category getCategory() {
        return this.category;
    }

    public User getCreatedByUser() {
        return this.createdByUser;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
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

    public boolean isRolledback() {
        return this.isRolledback;
    }

    // setter
    public void setCategory(Category category) {
        this.category = category;
    }

    public void setCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {

        this.content = content;
    }

    public void markUnreadRolledback() {
        this.isRolledback = false;
    }

    public void markReadRolledback() {
        this.isRolledback = true;
    }

    public void markStatusDRAFT() {
        this.status = ManualStatus.DRAFT;
        markUpdatedNow();
    }

    public void submitPENDING() {
        if (this.status != ManualStatus.APPROVED
                && this.status != ManualStatus.ARCHIVED) {
            this.status = ManualStatus.PENDING;
        } else {
            throw new IllegalStateException("必須項目(タイトル・本文)をすべて入力してください。");
        }
    }

    public void approve() {
        if (this.status == ManualStatus.PENDING) {
            this.status = ManualStatus.APPROVED;
            markApprovedNow();
            markUpdatedNow();
        } else {
            throw new IllegalStateException("申請状態のマニュアルのみ承認できます");
        }
    }

    public void rollbackToDraft() {
        if (this.status == ManualStatus.PENDING) {
            this.status = ManualStatus.DRAFT;
            markUpdatedNow();
        } else {
            throw new IllegalStateException("申請状態のマニュアルのみ差し戻しができます");
        }
    }

    public void archive() {
        if (this.status == ManualStatus.APPROVED
                || this.status == ManualStatus.PENDING
                || this.status == ManualStatus.DRAFT) {
            this.status = ManualStatus.ARCHIVED;
            markUpdatedNow();
        } else {
            throw new IllegalStateException("すでにアーカイブされているマニュアルは再度アーカイブできません。");
        }
    }

    public void restoreToApproved() {
        if (this.status == ManualStatus.ARCHIVED) {
            this.status = ManualStatus.APPROVED;
            // 復帰後は承認日時を保持するため、approvedAt は更新しない
        } else {
            throw new IllegalStateException("復帰できるのはステータスARCHIVEDのマニュアルだけです。");
        }
    }

    public void markCreatedNow() {
        this.createdAt = LocalDateTime.now();
    }

    public void markUpdatedNow() {
        this.updatedAt = LocalDateTime.now();
    }

    public void markApprovedNow() {
        this.approvedAt = LocalDateTime.now();
    }
}
