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
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
public class Manual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Setter
    @ManyToOne
    @JoinColumn(name = "operated_by_user_id")
    private User TargetUser;

    @Setter
    @Column(nullable = false, length = 100)
    private String title;

    @Setter
    @Column(nullable = false, length = 10000)
    private String content;

    // 業務上の公開状態を表す
    @Enumerated(EnumType.STRING)
    private ManualStatus status;

    public void markStatusDRAFT() {
        this.status = ManualStatus.DRAFT;
        markUpdatedNow();
    }

    public void submitPENDING() {
        if (this.status!= ManualStatus.APPROVED
            &&this.status!=ManualStatus.ARCHIVED) {
            this.status = ManualStatus.PENDING;
        } else {
            throw new IllegalStateException("申請として保存するには必須項目をすべて入力してください。");
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
            || this.status == ManualStatus.PENDING) {
            this.status = ManualStatus.ARCHIVED;
            markUpdatedNow();
        } else {
            throw new IllegalStateException("公開されているマニュアルのみアーカイブできます");
        }
    }
    public void restoreToApproved() {
        if (this.status == ManualStatus.ARCHIVED) {
            this.status = ManualStatus.APPROVED;
            markUpdatedNow();
            // 復帰後は承認日時を保持するため、approvedAt は更新しない
        } else {
            throw new IllegalStateException("同カテゴリでアーカイブされたマニュアルのみ復帰できます");
        }
    }


    private LocalDateTime createdAt;
    public void markCreatedNow() {
        this.createdAt = LocalDateTime.now();
    }

    private LocalDateTime updatedAt;
         public void markUpdatedNow() {
        this.updatedAt = LocalDateTime.now();
    }

    // 承認済みになった日時を保持する。未承認の場合は null
    private LocalDateTime approvedAt;
         public void markApprovedNow() {
        this.approvedAt = LocalDateTime.now();
    }
}