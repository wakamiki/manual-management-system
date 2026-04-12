package com.example.manual.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Category {

  public Category() {
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private Integer displayOrder;
  @Column(nullable = false, length = 50)
  private String categoryName;
  @Column(nullable = false)
  private LocalDateTime createdAt;
  @Column(nullable = false)
  private boolean isActive;
  @Column(nullable = false)
  private LocalDateTime updatedAt;


  public Long getId() {
    return this.id;
  }

  public Integer getDisplayOrder() {
    return this.displayOrder;
  }

  public String getCategoryName() {
    return this.categoryName;
  }

  public LocalDateTime getCreatedAt() {
    return this.createdAt;
  }

  public LocalDateTime getUpdatedAt() {

    return this.updatedAt;
  }

  public boolean isActive() {
    return this.isActive;
  }

  public void setDisplayOrder(Integer displayOrder) {
    this.displayOrder = displayOrder;
  }

  public void setCategoryName(String categoryName) {

    this.categoryName = categoryName;
  }

  public void markCreatedNow() {
    this.createdAt = LocalDateTime.now();
  }


  public void markUpdatedNow() {
    this.updatedAt = LocalDateTime.now();
  }

  public void markActive() {
    this.isActive = true;
  }

  public void markInactive() {
    this.isActive = false;
  }
}
