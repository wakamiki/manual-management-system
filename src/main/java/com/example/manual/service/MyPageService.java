package com.example.manual.service;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.manual.dto.ManualResponseDto;
import com.example.manual.dto.MyPageDto;
import com.example.manual.entity.User;
import com.example.manual.enums.UserRole;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.exception.UnauthorizedException;

@Service
public class MyPageService {

  private static final Logger log = LoggerFactory.getLogger(MyPageService.class);

  private final UserService userService;
  private final ManualService manualService;

  public MyPageService(UserService userService, ManualService manualService) {

    this.userService = userService;
    this.manualService = manualService;
  }

// ================================================
//画面表示
// ================================================

  // myPage表示
  public MyPageDto showMyPage(Principal principal) {
    log.info("start");
    User user = userService.getUserByPrincipal(principal);
    if (!canShowMyPage(user)) {
      throw new InvalidStateException("判定エラー");
    }
    MyPageDto pageDto = new MyPageDto();
    pageDto.setCreatedManualList(getUserCreatedManual(user));
    pageDto.setPendingManualList(getPendingManual(user));
    pageDto.setRollbackManualList(getRollbackManual(user));
    pageDto.setRollbackCount(manualService.countMyRollBackManual(principal));
    pageDto.setPendingUnCreatedCount(manualService.countNotUserCreatedPendingManualList(principal));

    return pageDto;
  }

  public List<ManualResponseDto> getRollbackManual(User user) {
    log.info("start");
    // 差し戻しマニュアルタブ
    List<ManualResponseDto> responseDto =
      manualService.createdRollbackManualList(user);
    return responseDto;
  }

  public List<ManualResponseDto> getUserCreatedManual(User user) {
    log.info("start");
    // 自分作成マニュアルタブ
    List<ManualResponseDto> listDto =
        manualService.userCreatedManualList(user);
    return listDto;
  }

  public List<ManualResponseDto> getPendingManual(User user) {
    log.info("start");
    // 承認待ちマニュアルタブ
    if (!canGetPendingManual(user)) {
      throw new InvalidStateException("判定エラー");
    }
    List<ManualResponseDto> listDto =
      manualService.pendingManualList(user);
    return listDto;
  }

//=================================================
//権限判定
// ================================================

public boolean canShowMyPage(User user) {
  log.info("start");
    //有効アカウント
    if(!user.isActive()){
      throw new UnauthorizedException("このアカウントは有効ではありません。");
    }
    return true;
  }

  public boolean canGetPendingManual(User user) {
    log.info("start");
    //admin/approver
    if(user.getRole()!=UserRole.APPROVER&&user.getRole()!=UserRole.ADMIN){
      throw new UnauthorizedException("承認権限がありません。");
    }
    return true;
  }
}
