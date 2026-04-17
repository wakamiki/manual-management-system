package com.example.manual.dto;

import java.util.ArrayList;
import java.util.List;

import com.example.manual.enums.ManualStatus;

public class ManualSearchConditionDto {

  public ManualSearchConditionDto() {

  }

  private String keyword;
  private List<Long> categoryIds = new ArrayList<>();
  private List<ManualStatus> statuses = new ArrayList<>();

  //#region getter
  public String getKeyword() {
    return this.keyword;
  }

  public List<Long> getCategoryIds() {
    return this.categoryIds;
  }

  public List<ManualStatus> getStatuses() {
  //#endregion
    return this.statuses;
  }

  //#region setter
  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public void setCategoryIds(List<Long> categoryIds) {
    this.categoryIds = categoryIds;
  }

  public void setStatuses(List<ManualStatus> statuses) {
  //#endregion
    this.statuses = statuses;
  }
}
