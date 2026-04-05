package com.example.manual.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ManualCopyRequestDto {

    private String title;

    private String content;

    private Long categoryId;

    private String changeNote;

}
