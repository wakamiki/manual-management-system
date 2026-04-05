package com.example.manual.dto;
import java.time.LocalDateTime;

import com.example.manual.enums.ManualStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ManualResponseDto {

private Long id;

private String title;

private String content;

private ManualStatus status;

private String categoryName;

private String displayName;

private LocalDateTime createdAt;

private LocalDateTime updatedAt;

private LocalDateTime approvedAt;

}
