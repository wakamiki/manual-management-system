package com.example.manual.dto;

import java.time.LocalDateTime;

public class ManualDetailHistoryDto {
        public ManualDetailHistoryDto() {

  }
    
    private String changeNote;
    private LocalDateTime changedAt;
    private String changedByUserName;


    //ゲッター
    public String getChangeNote(){
        return this.changeNote;
    }

    public LocalDateTime getChangedAt(){
        return this.changedAt;
    }

    public String getChangedByUserName(){
        return this.changedByUserName;
    }

    //セッター
    public void setChangeNote(String changeNote){
        this.changeNote = changeNote;
    }

    public void setChangedAt(LocalDateTime changedAt){
        this.changedAt = changedAt;
    }

    public void setChangedByUserName(String changedByUserName){
        this.changedByUserName = changedByUserName;
    }
}
