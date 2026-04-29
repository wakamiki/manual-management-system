package com.example.manual.dto;

import java.util.ArrayList;
import java.util.List;

import com.example.manual.enums.ManualStatus;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ManualSearchConditionDto {

  public ManualSearchConditionDto() {

  }

  @Size(max = 500)
  private String keyword;
  private List<Long> categoryIds = new ArrayList<>();
  private List<ManualStatus> statuses = new ArrayList<>();

  // getter
  public String getKeyword() {
    return this.keyword;
  }

  public List<@Positive Long> getCategoryIds() {
    return this.categoryIds;
  }

  public List<ManualStatus> getStatuses() {
    return this.statuses;
  }

  // setter
  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public void setCategoryIds(List<Long> categoryIds) {
    this.categoryIds = categoryIds;
  }

  public void setStatuses(List<ManualStatus> statuses) {
    this.statuses = statuses;
  }
}
