package com.example.manual.dto;

import java.time.LocalDateTime;

public class ManualHistoryDto {
    public ManualHistoryDto() {

  }
    
    private String changeNote;
    private LocalDateTime changedAt;


    //ゲッター
    public String getChangeNote(){
        return this.changeNote;
    }

    public LocalDateTime getChangedAt(){
        return this.changedAt;
    }

    //セッター
    public void setChangeNote(String changeNote){
        this.changeNote = changeNote;
    }

    public void setChangedAt(LocalDateTime changedAt){
        this.changedAt = changedAt;
    }

}
