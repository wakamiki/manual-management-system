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

}
