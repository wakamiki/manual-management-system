package com.example.manual.service;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.manual.dto.ManualResponseDto;
import com.example.manual.dto.MyPageDto;
import com.example.manual.entity.Manual;
import com.example.manual.entity.User;
import com.example.manual.enums.UserRole;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.exception.UnauthorizedException;

@Service
public class MyPageService {

  private static final Logger log = LoggerFactory.getLogger(MyPageService.class);

  private final UserService userService;
  private final ManualQueryService query;
  private final ManualPermissionService permission;

  public MyPageService(UserService userService,
      ManualQueryService manualQueryService,
      ManualPermissionService permission) {

    this.userService = userService;
    this.query = manualQueryService;
    this.permission = permission;
  }

  // ================================================
  // 画面表示
  // ================================================

  // myPage表示
  public MyPageDto showMyPage(Principal principal) {
    log.info("start");
    User user = userService.getUserByPrincipal(principal);
    if (!canShowMyPage(user)) {
      throw new InvalidStateException("判定エラー");
    }
    MyPageDto pageDto = new MyPageDto();
    pageDto.setCreatedManualList(getUserCreatedManual(principal));
    pageDto.setPendingManualList(getPendingManual(user));
    pageDto.setRollbackManualList(getRollbackManual(user));
    pageDto.setRollbackCount(query.countMyRollbackManual(principal));
    pageDto.setPendingUnCreatedCount(query.countNotUserCreatedPendingManualList(principal));

    return pageDto;
  }

  public List<ManualResponseDto> getRollbackManual(User user) {
    log.info("start");
    // 差し戻しマニュアルタブ
    List<Manual> manuals = query.findCreatedRollbackManuals(user);

    List<ManualResponseDto> responseDtos = query.buildIndexWithManuals(manuals);
    return responseDtos;
  }

  public List<ManualResponseDto> getUserCreatedManual(Principal principal) {
    log.info("start");
    // 自分作成マニュアルタブ
    List<Manual> manuals = query.findMyCreatedManuals(principal);

    List<ManualResponseDto> responseDtos = query.buildIndexWithManuals(manuals);
    return responseDtos;
  }

  public List<ManualResponseDto> getPendingManual(User user) {
    log.info("start");
    // 承認待ちマニュアルタブ
    if (!canGetPendingManual(user)) {
      throw new InvalidStateException("判定エラー");
    }
    List<Manual> manuals = query.findPendingManuals(user);

    List<ManualResponseDto> responseDtos = query.buildIndexWithManuals(manuals);
    return responseDtos;
  }

  // =================================================
  // 権限判定
  // ================================================

  public boolean canShowMyPage(User user) {
    log.info("start");
    // 有効アカウント
    if (!user.isActive()) {
      throw new UnauthorizedException("このアカウントは有効ではありません。");
    }
    return true;
  }

  public boolean canGetPendingManual(User user) {
    log.info("start");
    // admin/approver
    if (user.getRole() != UserRole.APPROVER && user.getRole() != UserRole.ADMIN) {
      throw new UnauthorizedException("承認権限がありません。");
    }
    return true;
  }
}
