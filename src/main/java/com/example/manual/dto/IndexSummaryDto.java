package com.example.manual.dto;

public class IndexSummaryDto {

  public IndexSummaryDto() {

  }

  private int countUserCreatedManual=0;
  private int countCreatedPendingManual=0;
  private int countRecentWeeklyManual=0;
  private int unreadRollbackCount=0;
  private int unreadPendingCount=0;


  //getter

  public int getCountUserCreatedManual() {
    return this.countUserCreatedManual;
  }

  public int getCountCreatedPendingManual() {
    return this.countCreatedPendingManual;
  }

  public int getCountRecentWeeklyManual() {
    return this.countRecentWeeklyManual;
  }

  public int getUnreadRollbackCount() {
    return this.unreadRollbackCount;
  }

  public int getUnreadPendingCount() {
    return this.unreadPendingCount;
  }
  //setter

  public void setCountUserCreatedManual(int countUserCreatedManual) {
    this.countUserCreatedManual = countUserCreatedManual;
  }

  public void setCountCreatedPendingManual(int countCreatedPendingManual) {
    this.countCreatedPendingManual = countCreatedPendingManual;
  }

  public void setCountRecentWeeklyManual(int countRecentWeeklyManual) {
    this.countRecentWeeklyManual = countRecentWeeklyManual;
  }

  public void setUnreadRollbackCount(int unreadRollbackCount) {
    this.unreadRollbackCount = unreadRollbackCount;
  }

  public void setUnreadPendingCount(int unreadPendingCount) {
    this.unreadPendingCount = unreadPendingCount;
  }
}
