package com.example.manual.service;

import java.security.Principal;

import com.example.manual.dto.ManualActionRequestDto;
import com.example.manual.dto.ManualDraftDto;
import com.example.manual.dto.ManualEditFormDto;
import com.example.manual.entity.Category;
import com.example.manual.entity.Manual;
import com.example.manual.entity.User;
import com.example.manual.exception.UnauthorizedException;
import com.example.manual.repository.ManualRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ManualCommandService {
  private static final Logger log = LoggerFactory.getLogger(ManualCommandService.class);

  private final ManualRepository manualRepository;
  private final ManualHistoryService historyService;
  private final UserService userService;
  private final CategoryService categoryService;
  private final NotificationService notificationService;
  private final ManualQueryService query;
  private final ManualPermissionService permission;

  public ManualCommandService(
      ManualRepository manualRepository,
      ManualHistoryService manualHistoryService,
      UserService userService,
      CategoryService categoryService,
      NotificationService notificationService,
      ManualQueryService query,
      ManualPermissionService permission) {

    this.manualRepository = manualRepository;
    this.historyService = manualHistoryService;
    this.userService = userService;
    this.categoryService = categoryService;
    this.notificationService = notificationService;
    this.query = query;
    this.permission = permission;
  }

  // ============================================
  // DB保存処理
  // ============================================

  // 新規作成保存（DRAFT）
  public void saveDraftForCreate(
      ManualDraftDto formDto,
      Principal principal) {
    log.info("start");
    Manual manual = new Manual();
    Category category = categoryService.getCategoryById(formDto.getCategoryId());
    User createUser = userService.getUserByPrincipal(principal);

    if (!permission.canSaveDraftForCreate(createUser, manual, category)) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.setTitle(formDto.getTitle());
    manual.setContent(formDto.getContent());
    manual.markUpdatedNow();
    manual.markCreatedNow();
    manual.markStatusDRAFT();
    manual.setCategory(category);
    manual.setCreatedByUser(createUser);
    // Rollbackフラグ初期化
    manual.markUnreadRolledback();
    manualRepository.save(manual);
  }

  // 新規作成マニュアル公開(PENDING)
  public void createPendingManual(
      ManualEditFormDto formDto,
      Principal principal) {
    log.info("start");
    Manual manual = new Manual();
    Category category = categoryService.getCategoryById(formDto.getCategoryId());
    User createUser = userService.getUserByPrincipal(principal);
    if (!permission.canCreatePendingManual(createUser, category, formDto)) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.setTitle(formDto.getTitle());
    manual.setContent(formDto.getContent());
    manual.markCreatedNow();
    manual.markUpdatedNow();
    manual.submitPENDING();
    manual.setCategory(category);
    manual.setCreatedByUser(createUser);
    // Rollbackフラグ初期化
    manual.markUnreadRolledback();
    Manual savedManual = manualRepository.save(manual);
    // 承認通知作成
    notificationService.createSubmitNotifications(principal, savedManual);
  }

  // 複製編集保存(DRAFT)
  public void saveDraftForCopy(
      Long manualId,
      ManualDraftDto formDto,
      Principal principal) {
    log.info("start");
    Manual manual = query.findManualOrThrow(manualId);
    Category category = categoryService.getCategoryById(formDto.getCategoryId());

    User playUser = userService.getUserByPrincipal(principal);
    if (!permission.canSaveDraftForCopy(playUser,
        category,
        formDto.getChangeNote())) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.setTitle(formDto.getTitle());
    manual.setContent(formDto.getContent());
    manual.markUpdatedNow();
    manual.markCreatedNow();
    manual.markStatusDRAFT();
    manual.setCategory(category);
    manual.setCreatedByUser(playUser);
    // Rollbackフラグ初期化
    manual.markUnreadRolledback();
    Manual savedManual = manualRepository.save(manual);
    historyService.createHistory(savedManual,
        formDto.getChangeNote(),
        principal);
  }

  // 複製編集公開(Pending)
  public void savePendingForCopy(
      Long manualId,
      ManualEditFormDto formDto,
      Principal principal) {
    log.info("start");
    Manual manual = query.findManualOrThrow(manualId);
    Category category = categoryService.getCategoryById(formDto.getCategoryId());

    User playUser = userService.getUserByPrincipal(principal);
    if (!permission.canSavePendingForCopy(
        playUser,
        category,
        formDto.getChangeNote(),
        formDto.getTitle(),
        formDto.getContent())) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.setTitle(formDto.getTitle());
    manual.setContent(formDto.getContent());
    manual.markUpdatedNow();
    manual.markCreatedNow();
    manual.submitPENDING();
    manual.setCategory(category);
    manual.setCreatedByUser(playUser);
    // Rollbackフラグ初期化
    manual.markUnreadRolledback();
    Manual savedManual = manualRepository.save(manual);
    historyService.createHistory(
        savedManual,
        formDto.getChangeNote(),
        principal);
    notificationService.createSubmitNotifications(principal, savedManual);
  }

  // 編集マニュアル下書き保存(Draft)
  public void editToDraft(
      Long manualId,
      ManualDraftDto formDto,
      Principal principal) {
    log.info("start");
    Manual manual = query.findManualOrThrow(manualId);
    Category category = categoryService.getCategoryById(formDto.getCategoryId());

    User playUser = userService.getUserByPrincipal(principal);
    if (!permission.canEditToDraft(playUser, manual)) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.setTitle(formDto.getTitle());
    manual.setContent(formDto.getContent());
    manual.markUpdatedNow();
    manual.markStatusDRAFT();
    manual.setCategory(category);
    Manual savedManual = manualRepository.save(manual);
    notificationService.deletePendingApprovalNotificationsByManualId(manualId);
    if (!formDto.getChangeNote().isBlank() && formDto.getChangeNote() != null) {
      historyService.createHistory(
          savedManual,
          formDto.getChangeNote(),
          principal);
    }
  }

  // 編集マニュアル公開(PENDING)
  public void editToPending(
      Long manualId,
      ManualEditFormDto formDto,
      Principal principal) {
    log.info("start");
    Manual manual = query.findManualOrThrow(manualId);
    Category category = categoryService.getCategoryById(formDto.getCategoryId());

    User playUser = userService.getUserByPrincipal(principal);
    if (!permission.canEditToPending(playUser, manual, formDto)) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.setTitle(formDto.getTitle());
    manual.setContent(formDto.getContent());
    manual.markUpdatedNow();
    manual.submitPENDING();
    manual.setCategory(category);
    Manual savedManual = manualRepository.save(manual);
    if (!formDto.getChangeNote().isBlank() && formDto.getChangeNote() != null) {
      historyService.createHistory(savedManual,
          formDto.getChangeNote(),
          principal);
    }
    // 承認通知作成
    notificationService.deleteRollbackNotification(manualId, playUser);
    notificationService.createSubmitNotifications(principal, savedManual);
  }

  // 公開
  public void submitManual(
      Long manualId,
      Principal principal) {
    log.info("start");
    Manual manual = query.findManualOrThrow(manualId);
    User playUser = userService.getUserByPrincipal(principal);
    if (!permission.canPending(manual, playUser)) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.submitPENDING();
    manual.markUpdatedNow();
    Manual savedManual = manualRepository.save(manual);
    notificationService.createSubmitNotifications(principal, savedManual);
  }

  // 承認
  public void approveManual(
      Long manualId,
      String changeNote,
      Principal principal) {
    log.info("start");
    Manual manual = query.findManualOrThrow(manualId);
    User playUser = userService.getUserByPrincipal(principal);
    Category category = manual.getCategory();
    if (!permission.canApproveManual(manual, category, playUser)) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.approve();
    manual.markUpdatedNow();
    manual.markApprovedNow();
    Manual savedManual = manualRepository.save(manual);
    if (changeNote != null && !changeNote.isBlank()) {
      historyService.createHistory(
          savedManual,
          changeNote,
          principal);
    }
    // 未承認通知削除
    notificationService.deletePendingApprovalNotificationsByManualId(manualId);
  }

  // 差し戻し
  public void rollbackEditManual(
      Long manualId,
      ManualActionRequestDto requestDto,
      Principal principal) {
    log.info("start");
    Manual manual = query.findManualOrThrow(manualId);
    User playUser = userService.getUserByPrincipal(principal);
    if (!permission.canRollbackManual(manual, playUser, requestDto.getChangeNote())) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.markReadRolledback();
    manual.markUpdatedNow();
    manual.rollbackToDraft();
    Manual savedManual = manualRepository.save(manual);
    historyService.createHistory(
        savedManual, requestDto.getChangeNote(), principal);
    // 差し戻し通知作成
    notificationService.createRollbackNotification(savedManual);
  }

  // アーカイブ
  public void archiveManual(
      Long manualId,
      ManualActionRequestDto actionRequestDto,
      Principal principal) {
    log.info("start");
    Manual manual = query.findManualOrThrow(manualId);
    User playUser = userService.getUserByPrincipal(principal);
    Category category = categoryService.getCategoryById(manual.getCategory().getId());

    if (!permission.canArchiveManual(
        playUser, manual, category, actionRequestDto.getChangeNote())) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.archive();
    manual.markUpdatedNow();
    Manual savedManual = manualRepository.save(manual);
    historyService.createHistory(
        savedManual, actionRequestDto.getChangeNote(), principal);
    notificationService.deleteByManualIdNotification(manualId);
  }

  // 復帰
  public void restoreManual(
      Long manualId,
      ManualActionRequestDto requestDto,
      Principal principal) {
    log.info("start");
    Manual manual = query.findManualOrThrow(manualId);
    User playUser = userService.getUserByPrincipal(principal);
    if (!permission.canRestoreManual(manual, playUser, requestDto.getChangeNote())) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.restoreToApproved();
    manual.markUpdatedNow();
    Manual savedManual = manualRepository.save(manual);
    historyService.createHistory(
        savedManual, requestDto.getChangeNote(), principal);
  }
}
