package com.example.manual.dto;

import java.time.LocalDateTime;

import com.example.manual.enums.ManualStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class ManualListDto {

  private Long manualId;

  private String title;

  private ManualStatus status;

  private String categoryName;

  private LocalDateTime updatedAt;

  private String createdByName;

}
