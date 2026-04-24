package com.example.manual.service;

import java.security.Principal;
import java.util.List;

import com.example.manual.dto.ManualResponseDto;
import com.example.manual.dto.MyPageDto;
import com.example.manual.entity.Manual;
import com.example.manual.entity.User;
import com.example.manual.enums.UserRole;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.exception.UnauthorizedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MyPageService {

  private static final Logger log = LoggerFactory.getLogger(MyPageService.class);

  private final UserService userService;
  private final ManualQueryService query;

  public MyPageService(UserService userService,
      ManualQueryService manualQueryService) {

    this.userService = userService;
    this.query = manualQueryService;
  }

  // ================================================
  // 画面表示
  // ================================================

  // myPage表示
  public MyPageDto showMyPage(Principal principal) {
  log.info("start");
  User playUser = userService.getUserByPrincipal(principal);
  if (!canShowMyPage(playUser)) {
  throw new InvalidStateException("判定エラー");
  }
  MyPageDto myPageDto = new MyPageDto();

  List<Manual> myCreatedManuals = query.findMyCreatedManuals(principal);
  List<ManualResponseDto> userCreatedDtos = query.buildIndexWithManualsPage(myCreatedManuals);
  myPageDto.setCreatedManualList(userCreatedDtos);

  List<Manual> createdRollbackManuals = query.findCreatedRollbackManuals(playUser);
  List<ManualResponseDto> rollbackDtos = query.buildIndexWithManualsPage(createdRollbackManuals);
  myPageDto.setRollbackManualList(rollbackDtos);

  if (playUser.getRole() == UserRole.APPROVER || playUser.getRole() == UserRole.ADMIN) {
    List<Manual> pendingManuals = query.findPendingManuals(playUser);
    List<ManualResponseDto> pendingDtos = query.buildIndexWithManualsPage(pendingManuals);
    myPageDto.setPendingManualList(pendingDtos);
    myPageDto.setPendingUnCreatedCount(query.countNotUserCreatedPendingManualList(principal));
  }

  myPageDto.setRollbackCount(query.countMyRollbackManual(principal));
  myPageDto.setUserDto(userService.toCreatedUserDto(playUser));

  return myPageDto;
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

}
