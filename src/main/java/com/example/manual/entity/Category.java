package com.example.manual.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(nullable = false)
  private Integer displayOrder;

  @Setter
  @Column(nullable = false, length = 50)
  private String categoryName;

  @Column(nullable = false)
  private LocalDateTime createdAt;
  public void markCreatedNow() {
    this.createdAt = LocalDateTime.now();
  }

  @Column(nullable = false)
  private LocalDateTime updatedAt;
  public void markUpdatedNow() {
    this.updatedAt = LocalDateTime.now();
  }

  @Column(nullable = false)
  private boolean isActive;
  public void markActive() {
    this.isActive = true;
  }
  public void markInactive() {
    this.isActive = false;
  }

}
