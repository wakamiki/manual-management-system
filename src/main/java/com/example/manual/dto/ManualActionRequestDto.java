package com.example.manual.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ManualActionRequestDto {
  public ManualActionRequestDto() {
 
  }
  @NotBlank
  @Size(max = 100)
  private String changeNote;

  //getter
  public String getChangeNote(){
    return this.changeNote;
  }

  //setter
  public void setChangeNote(String changeNote){
    this.changeNote = changeNote;
  }
}
