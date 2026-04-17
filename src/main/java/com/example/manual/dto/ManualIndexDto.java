package com.example.manual.dto;

import java.util.ArrayList;
import java.util.List;

import com.example.manual.enums.ManualStatus;


public class ManualIndexDto {
  public ManualIndexDto() {

  }

  private List<ManualResponseDto> searchManuals= new ArrayList<>();
  private List<CategoryResponseDto> activeCategories = new ArrayList<>();
  private List<CategoryResponseDto> inactiveCategories= new ArrayList<>();
  private List<ManualStatus> defaultStatuses= new ArrayList<>();
  private IndexSummaryDto summaryDto;
  private UserResponseDto userDto;

 //getter

 public List<ManualResponseDto> getSearchManuals() {
   return this.searchManuals;
 }

 public List<CategoryResponseDto> getActiveCategories() {
   return this.activeCategories;
 }

 public List<CategoryResponseDto> getInactiveCategories() {
   return this.inactiveCategories;
 }

 public List<ManualStatus> getDefaultStatuses() {
   return this.defaultStatuses;
 }

 public IndexSummaryDto getSummaryDto() {
   return this.summaryDto;
 }

 public UserResponseDto getUserDto() {
   return this.userDto;
 }



  //setter
  public void setSearchManuals(
    List<ManualResponseDto>searchManuals
  ) {
    this.searchManuals = searchManuals;
  }

  public void setActiveCategories(List<CategoryResponseDto> activeCategories) {
    this.activeCategories = activeCategories;
  }

  public void setInactiveCategories(List<CategoryResponseDto> inactiveCategories) {
    this.inactiveCategories = inactiveCategories;
  }

  public void setDefaultStatuses(List<ManualStatus> defaultStatuses) {
    this.defaultStatuses = defaultStatuses;
  }

  public void setSummaryDto(IndexSummaryDto summaryDto) {
    this.summaryDto = summaryDto;
  }

  public void setUserDto(UserResponseDto userDto) {
    this.userDto = userDto;
  }
}
