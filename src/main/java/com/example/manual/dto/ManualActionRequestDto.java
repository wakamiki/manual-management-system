package com.example.manual.dto;

public class ManualActionRequestDto {
  public ManualActionRequestDto() {
 
  }

  private String changeNote;

  //#region getter
  public String getChangeNote(){
  //#endregion
    return this.changeNote;
  }

  //#region setter
  public void setChangeNote(String changeNote){
  //#endregion
    this.changeNote = changeNote;
  }
}
