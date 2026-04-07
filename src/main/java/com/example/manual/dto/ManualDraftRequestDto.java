package com.example.manual.dto;

import com.example.manual.enums.ManualStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ManualDraftRequestDto {
    
    @Size(max=100)
    private String title;

    @Size(max=10000)
    private String content;

    @NotNull
    private Long categoruId;

    @Size(max=100)
    private String changeNote;

    private ManualStatus status;
}
