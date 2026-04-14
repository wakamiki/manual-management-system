package com.example.manual.dto;

import java.util.List;

public class UserListResponseDto {

  public UserListResponseDto() {

  }

  private List<UserResponseDto> userResponseDtoList;

  //getter
  public List<UserResponseDto> userResponseDtoList() {
    return this.userResponseDtoList;
  }

  //setter
  public  void userResponseDtoList(
      List<UserResponseDto> userResponseDtoList) {
    this.userResponseDtoList = userResponseDtoList;
  }
}
