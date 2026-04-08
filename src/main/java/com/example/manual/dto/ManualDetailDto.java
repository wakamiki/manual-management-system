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

}
