package com.example.manual.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
public class ManualRequestDto {

@NotBlank
@Size(max=100)
private String title;

@Size(max=10000)
@NotBlank
private String content;

@NotNull
private Long categoryId;

@Size(max=100)
private String changeNote;

public String getTitle() {
return this.title;
}

public String getContent() {
return this.content;
}

public Long getCategoryId() {
return this.categoryId;
}

public String getChangeNote() {
return this.changeNote;
}

}
