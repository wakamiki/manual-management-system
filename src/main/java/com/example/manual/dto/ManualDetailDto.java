package com.example.manual.dto;

import java.time.LocalDateTime;

import com.example.manual.enums.ManualStatus;
import com.example.manual.entity.ManualHistory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ManualDetailDto {

  private Long manualId;

  private String title;

  private ManualStatus status;

  private String categoryName;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  private LocalDateTime approvedAt;

  private String createdByName;
  
  private String content;

  private ManualHistory history;

  public Long getManualId() {
    return this.manualId;
  }

  public String getTitle() {
    return this.title;
  }

  public ManualStatus getStatus() {
    return this.status;
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

}
