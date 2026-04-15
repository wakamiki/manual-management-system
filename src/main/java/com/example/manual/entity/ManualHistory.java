package com.example.manual.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "manualHistories")
public class ManualHistory {

  public ManualHistory() {
  }

  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "manual_id")
  private Manual manual;
  @Column(nullable = false, length = 100)
  private String changeNote;
  @Column(nullable = false)
  private LocalDateTime changedAt;
  @ManyToOne
  @JoinColumn(name = "change_user_id")
  private User changedByUser;

  //#region getter
  public Long getId() {
    return this.id;
  }
  public Manual getManual() {
    return this.manual;
  }
  public String getChangeNote() {
    return this.changeNote;
  }

  public LocalDateTime getChangedAt() {
    return this.changedAt;
  }

  public User getChangedByUser(){

    return  this.changedByUser;
  }

  //#endregion
  //#region setter
  public void setManual(Manual manual) {
    this.manual = manual;
  }

  public void setChangeNote(String changeNote) {
    this.changeNote = changeNote;
  }

  public void setChangedByUser(User changedByUser){

    this.changedByUser = changedByUser;
  }

  public void markChangedNow() {
    this.changedAt = LocalDateTime.now();
  }
  //#endregion
}
