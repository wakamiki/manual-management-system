package com.example.manual.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
public class ManualRequestDto {

private String title;

private String content;

private Long categoryId;

private String changeNote;

}
