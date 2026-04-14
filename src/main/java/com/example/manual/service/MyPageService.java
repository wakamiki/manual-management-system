package com.example.manual.service;

import java.security.Principal;
import java.util.List;

import com.example.manual.dto.ManualResponseDto;
import com.example.manual.dto.MyPageDto;
import com.example.manual.entity.User;
import com.example.manual.enums.UserRole;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.exception.UnauthorizedException;

public class MyPageService {

  private final UserService userService;
  private final ManualService manualService;

  public MyPageService(UserService userService, ManualService manualService) {

    this.userService = userService;
    this.manualService = manualService;
  }

  public MyPageDto getMyPageViewData(Principal principal) {
    User user = userService.getUserByPrincipal(principal);
    if (!canGetMyPageData(user)) {
      throw new InvalidStateException("判定エラー");
    }
    MyPageDto pageDto = new MyPageDto();
    pageDto.setCreatedManualList(getUserCreatedManual(user));
    pageDto.setPendingManualList(getPendingManual(user));
    pageDto.setRollbackManualList(getRollbackManual(user));
    pageDto.setRollbackCount(manualService.countMyRollBackManual(principal));
    pageDto.setPendingUnCreatedCount(manualService.countNotUserCreatedPendingManualList(principal));

    //数字数える
    return pageDto;
  }

  public List<ManualResponseDto> getRollbackManual(User user) {
    // 差し戻しマニュアルタブ
    List<ManualResponseDto> responseDto =
      manualService.createdRollbackManualList(user);
    return responseDto;
  }

  public List<ManualResponseDto>  getUserCreatedManual(User user) {
    // 自分作成マニュアルタブ
    List<ManualResponseDto> listDto =
        manualService.userCreatedManualList(user);
    return listDto;
  }

  public List<ManualResponseDto> getPendingManual(User user) {
    // 承認待ちマニュアルタブ
    if (!canGetPendingManual(user)) {
      throw new InvalidStateException("判定エラー");
    }
    List<ManualResponseDto> listDto =
      manualService.pendingManualList(user);
    return listDto;
  }


  //権限判定

  public boolean canGetMyPageData(User user) {
    //有効アカウント
    if(!user.isActive()){
      throw new UnauthorizedException("このアカウントは有効ではありません。");
    }
    return true;
  }

  public boolean canGetPendingManual(User user) {
    //admin/approver
    if(user.getRole()!=UserRole.APPROVER&&user.getRole()!=UserRole.ADMIN){
      throw new UnauthorizedException("承認権限がありません。");
    }
    return true;
  }
}
