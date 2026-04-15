package com.example.manual.dto;

import java.util.List;

import com.example.manual.enums.ManualStatus;


public class ManualListDto {
  public ManualListDto() {

  }

  private List<ManualResponseDto> responseDtos;
  private List<ManualResponseDto> searchManuals;
  private ManualResponseDto quickView;
  private List<CategoryResponseDto> categoryDtos;
  private List<ManualStatus> manualStatuses;
 //getter
 public List<ManualResponseDto> getResponseDtos() {
   return this.responseDtos;
 }

 public List<ManualResponseDto> getSearchManuals() {
   return this.searchManuals;
 }

 public ManualResponseDto getQuickView() {
   return this.quickView;
 }

 public List<CategoryResponseDto> getCategoryDtos() {
   return this.categoryDtos;
 }

 public List<ManualStatus> getManualStatuses() {
   return this.manualStatuses;
 }



  //setter
  public void setResponseDtos(
      List<ManualResponseDto> responseDtos) {
    this.responseDtos = responseDtos;
  }

  public void setSearchManuals(
    List<ManualResponseDto>searchManuals
  ) {
    this.searchManuals = searchManuals;
  }

  public void setQuickView(
      ManualResponseDto quickView) {
    this.quickView = quickView;
  }

  public void setCategoryDtos(List<CategoryResponseDto> categoryDtos) {
    this.categoryDtos = categoryDtos;
  }

  public void setManualStatuses(List<ManualStatus> manualStatuses) {
    this.manualStatuses = manualStatuses;
  }
}
