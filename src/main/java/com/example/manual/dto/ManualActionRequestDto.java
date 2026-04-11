package com.example.manual.dto;

public class ManualActionRequestDto {
  public ManualActionRequestDto() {
 
  }

  private String changeNote;

  //#region getter
  public String getChangeNote(){
    return this.changeNote;
  }

  //#endregion 
  //#region setter
  public void setChangeNote(String changeNote){
    this.changeNote = changeNote;
  }
  //#endregion  
}
