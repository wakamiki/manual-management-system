package com.example.manual.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class ManualHistory {

  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @ManyToOne
  @JoinColumn(name = "manual_id")
  private Manual manual;

  @Setter
  @Column(nullable = false, length = 100)
  private String changeNote;

  @Column(nullable = false)
  private LocalDateTime changedAt;
  public void markChangedNow(){
    this.changedAt = LocalDateTime.now();
  }
}
