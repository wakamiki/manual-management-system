package com.example.manual.entity;

import java.time.LocalDateTime;

import com.example.manual.enums.OperationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_operation_histories")
public class UserOperationHistory {

    public UserOperationHistory() {
}

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
@ManyToOne
@JoinColumn(name = "target_user_id")
private User targetUser;
@ManyToOne
@JoinColumn(name = "operated_by_user_id")
private User operatedByUser;
@Column(nullable = false, length = 30)
@Enumerated(EnumType.STRING)
private OperationType operationType;
@Column(nullable = false,length = 100)
private String operationDetail;
@Column(nullable = false)
private LocalDateTime createdAt;

  //getter
public Long getId() {
    return this.id;
}

public User getTargetUser() {
    return this.targetUser;
}

public User getOperatedByUser() {
    return this.operatedByUser;
}

public OperationType getOperationType() {
    return this.operationType;
}

public String getOperationDetail() {
    return this.operationDetail;
}

public LocalDateTime getCreatedAt() {

    return this.createdAt;
}

  //setter
  public void setTargetUser(User targetUser) {

      this.targetUser = targetUser;
  }

  public void setOperationDetail(String operationDetail) {
      this.operationDetail = operationDetail;
  }

  public void setOperatedByUser(User operatedByUser) {
      this.operatedByUser = operatedByUser;
  }

//メソッド
public void markCreatedNow() {
    this.createdAt = LocalDateTime.now();
}

public void markCreateUser() {
    this.operationType = OperationType.CREATE_USER;
}

public void markUpdateUser() {
    this.operationType = OperationType.UPDATE_USER;
}

public void markDeactiveteUser() {
    this.operationType = OperationType.DEACTIVATE_USER;
}

public void markActivateUser() {
    this.operationType = OperationType.ACTIVATE_USER;
}

public void markResetPassword() {
    this.operationType = OperationType.RESET_PASSWORD;
}

public void markChangePassword() {
    this.operationType = OperationType.CHANGE_PASSWORD;
}
}
