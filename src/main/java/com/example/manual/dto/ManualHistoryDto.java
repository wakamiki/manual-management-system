package com.example.manual.dto;

import java.time.LocalDateTime;

public class ManualHistoryDto {
    public ManualHistoryDto() {

  }
    
    private String changeNote;
    private LocalDateTime changedAt;


    //„Ç≤„ÉÅEÇø„Éº
  //#region getter
    public String getChangeNote(){
        return this.changeNote;
    }

    public LocalDateTime getChangedAt(){
  //#endregion
        return this.changedAt;
    }

    //„Çª„ÉÅEÇø„Éº
  //#region setter
    public void setChangeNote(String changeNote){
        this.changeNote = changeNote;
    }

    public void setChangedAt(LocalDateTime changedAt){
  //#endregion
        this.changedAt = changedAt;
    }

}
