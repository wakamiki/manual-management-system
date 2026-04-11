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


@Entity
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
    private User user;
    @Column(nullable = false, length = 100)
    private String title;
    @Column(nullable = false, length = 10000)
    private String content;
    // 讌ｭ蜍吩ｸ翫・蜈ｬ髢狗憾諷九ｒ陦ｨ縺・    @Enumerated(EnumType.STRING)
    private ManualStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime approvedAt;

    //繧ｲ繝・ち繝ｼ
  //#region getter
    public Long getId() {
        return this.id;
    }

    public Category getCategory() {
        return this.category;
    }

    public User getUser() {
        return this.user;
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
  //#endregion
        return this.approvedAt;
    }

    //繧ｻ繝・ち繝ｼ
  //#region setter
    public void setCategory(Category category) {
        this.category = category;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
  //#endregion
        this.content = content;
    }

    //繝｡繧ｽ繝・ラ
    public void markStatusDRAFT() {
        this.status = ManualStatus.DRAFT;
        markUpdatedNow();
    }

    public void submitPENDING() {
        if (this.status != ManualStatus.APPROVED
                && this.status != ManualStatus.ARCHIVED) {
            this.status = ManualStatus.PENDING;
        } else {
            throw new IllegalStateException("蠢・磯・岼(繧ｿ繧､繝医Ν繝ｻ譛ｬ譁・繧偵☆縺ｹ縺ｦ蜈･蜉帙＠縺ｦ縺上□縺輔＞縲・);
        }
    }

    public void approve() {
        if (this.status == ManualStatus.PENDING) {
            this.status = ManualStatus.APPROVED;
            markApprovedNow();
            markUpdatedNow();
        } else {
            throw new IllegalStateException("逕ｳ隲狗憾諷九・繝槭ル繝･繧｢繝ｫ縺ｮ縺ｿ謇ｿ隱阪〒縺阪∪縺・);
        }
    }

    public void rollbackToDraft() {
        if (this.status == ManualStatus.PENDING) {
            this.status = ManualStatus.DRAFT;
            markUpdatedNow();
        } else {
            throw new IllegalStateException("逕ｳ隲狗憾諷九・繝槭ル繝･繧｢繝ｫ縺ｮ縺ｿ蟾ｮ縺玲綾縺励′縺ｧ縺阪∪縺・);
        }
    }

    public void archive() {
        if (this.status == ManualStatus.APPROVED
                || this.status == ManualStatus.PENDING) {
            this.status = ManualStatus.ARCHIVED;
            markUpdatedNow();
        } else {
            throw new IllegalStateException("蜈ｬ髢九＆繧後※縺・ｋ繝槭ル繝･繧｢繝ｫ縺ｮ縺ｿ繧｢繝ｼ繧ｫ繧､繝悶〒縺阪∪縺・);
        }
    }

    public void restoreToApproved() {
        if (this.status == ManualStatus.ARCHIVED) {
            this.status = ManualStatus.APPROVED;
            // 蠕ｩ蟶ｰ蠕後・謇ｿ隱肴律譎ゅｒ菫晄戟縺吶ｋ縺溘ａ縲∥pprovedAt 縺ｯ譖ｴ譁ｰ縺励↑縺・        } else {
            throw new IllegalStateException("蜷後き繝・ざ繝ｪ縺ｧ繧｢繝ｼ繧ｫ繧､繝悶＆繧後◆繝槭ル繝･繧｢繝ｫ縺ｮ縺ｿ蠕ｩ蟶ｰ縺ｧ縺阪∪縺・);
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
