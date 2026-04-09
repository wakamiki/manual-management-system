package com.example.manual.dto;

import com.example.manual.enums.ManualStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ManualCopyRequestDto {

    @Size(max=100)
    @NotBlank
    private String title;

    @NotBlank
    @Size(max=10000)
    private String content;

    @NotNull
    private Long sourceManualId;

    @NotNull
    private Long categoryId;

    @Size(max=100)
    @NotBlank
    private String changeNote;

    @NotNull
    private ManualStatus status;

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public Long getSourceManualId() {
        return this.sourceManualId;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public String getChangeNote() {
        return this.changeNote;
    }

    public ManualStatus getStatus() {
        return this.status;
    }
}
