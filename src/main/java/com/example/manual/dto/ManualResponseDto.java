package com.example.manual.dto;
import java.time.LocalDateTime;

import org.apache.catalina.User;

import com.example.manual.enums.ManualStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ManualResponseDto {

  private Long manualId;

private String title;

private String content;

private ManualStatus status;

private String categoryName;

private String changeNote;

private LocalDateTime changedAt;

private String displayName;

private User changedByName;

private LocalDateTime createdAt;

private LocalDateTime updatedAt;

private LocalDateTime approvedAt;

public Long getManualId() {
  return this.manualId;
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

public String getCategoryName() {
  return this.categoryName;
}

public String getChangeNote() {
  return this.changeNote;
}

public LocalDateTime getChangedAt() {
  return this.changedAt;
}

public String getDisplayName() {
  return this.displayName;
}

public User getChangedByName() {
  return this.changedByName;
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

}
