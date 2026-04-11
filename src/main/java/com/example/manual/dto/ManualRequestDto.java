package com.example.manual.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ManualRequestDto {

  public ManualRequestDto() {
  }

@NotBlank
@Size(max=100)
private String title;
@Size(max=10000)
@NotBlank
private String content;
@NotNull
private Long categoryId;
@Size(max = 100)

//„Ç≤„ÉÅEÇø„Éº
private String changeNote;

  //#region getter
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
  //#endregion
  return this.changeNote;
}

//„Çª„ÉÅEÇø„Éº
  //#region setter
public void setTitle(String title) {
  this.title = title;
}

public void setContent(String content) {
  this.content = content;
}

public void setCategoryId(Long categoryId) {
  this.categoryId = categoryId;
}

public void setChangeNote(String cangeNote) {
  //#endregion
  this.changeNote = cangeNote;
}

}
