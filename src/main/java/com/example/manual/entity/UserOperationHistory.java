package com.example.manual.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class UserOperationHistory {

    public UserOperationHistory() {

}

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
@ManyToOne
@JoinColumn(name = "target_user_id")
private User targetUser;
@Column(nullable = false)
private String operatedByUser;
@Column(nullable = false,length = 30)
private String operationType;
@Column(nullable = false,length = 100)
private String operationDetail;
@Column(nullable = false)
private LocalDateTime createdAt;

  //#region getter
public Long getId() {
    return this.id;
}

public User getTargetUser() {
    return this.targetUser;
}

public String getOperatedByUser() {
    return this.operatedByUser;
}

public String getOperationType() {
    return this.operationType;
}

public String getOperationDetail() {
    return this.operationDetail;
}

public LocalDateTime getCreatedAt() {

    return this.createdAt;
}
  //#endregion
  //#region setter
public void setTargetUser(User targetUser) {

    this.targetUser = targetUser;
}
  //#endregion
//メソッド
public void markCreatedNow() {
    this.createdAt = LocalDateTime.now();
}
}
