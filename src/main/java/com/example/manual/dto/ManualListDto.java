package com.example.manual.dto;

import java.util.List;


public class ManualListDto {
  public ManualListDto() {

  }

 private List<ManualResponseDto> responseDtos;

 //getter
 public List<ManualResponseDto> getResponseDtos() {
   return this.responseDtos;
  }


  //setter
  public void setResponseDtos(
      List<ManualResponseDto> responseDtos) {
    this.responseDtos = responseDtos;
  }
}
