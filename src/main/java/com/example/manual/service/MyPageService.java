package com.example.manual.service;

import java.security.Principal;
import java.util.List;

import com.example.manual.dto.ManualListDto;
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

  public MyPageDto getMyPageData(Principal principal) {
    User user = userService.getUserByPrincipal(principal);
    if (!canGetMyPageData(user)) {
      throw new InvalidStateException("判定エラー");
    }
    MyPageDto pageDto = new MyPageDto();
    pageDto.setCreatedManualList(getUserCreatedManual(user));
    pageDto.setPendeingManualList(getPendingManual(user));
    pageDto.setRollbackManualList(getRollbackManual(user));
    
    pageDto.setPendingCount(pageDto.getPendingManualList().size());
    pageDto.setRollbackCount(pageDto.getRollbackManualList().size());
    return pageDto;
  }

  public List<ManualListDto> getRollbackManual(User user) {
    // 差し戻しマニュアルタブ
    List<ManualListDto>listDto =
      manualService.createdRollbackManualList(user);
    return listDto;
  }

  public List<ManualListDto>  getUserCreatedManual(User user) {
    // 自分作成マニュアルタブ
    List<ManualListDto> listDto =
        manualService.userCreatedManualList(user);
    return listDto;
  }

  public List<ManualListDto> getPendingManual(User user) {
    // 承認待ちマニュアルタブ
    if (!canGetPendingManual(user)) {
      throw new InvalidStateException("判定エラー");
    }
    List<ManualListDto> listDto =
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
