package com.example.manual.service;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.manual.dto.ManualActionRequestDto;
import com.example.manual.dto.ManualDraftDto;
import com.example.manual.dto.ManualEditFormDto;
import com.example.manual.entity.Category;
import com.example.manual.entity.Manual;
import com.example.manual.entity.User;
import com.example.manual.exception.UnauthorizedException;
import com.example.manual.repository.ManualRepository;

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

    Manual manual = new Manual();
    Category category = categoryService.getCategoryById(formDto.getCategoryId());
    User createUser = userService.getUserByPrincipal(principal);

    log.info("[{}][PERMISSION][START] rule={}");
    if (!permission.canSaveDraftForCreate(createUser, manual, category)) {
      throw new UnauthorizedException("判定エラー");
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    manual.setTitle(formDto.getTitle());
    manual.setContent(formDto.getContent());
    manual.markUpdatedNow();
    manual.markCreatedNow();
    manual.markStatusDRAFT();
    manual.setCategory(category);
    manual.setCreatedByUser(createUser);
    // Rollbackフラグ初期化
    manual.markUnreadRolledback();
    log.info("[{}][{}][PERSIST][START] action={} id={}");
    manualRepository.save(manual);
    log.info("[{}][{}][PERSIST][DONE] action={} id={}");
  }

  // 新規作成マニュアル公開(PENDING)
  public void createPendingManual(
      ManualEditFormDto formDto,
      Principal principal) {

    Manual manual = new Manual();
    Category category = categoryService.getCategoryById(formDto.getCategoryId());
    User createUser = userService.getUserByPrincipal(principal);
    log.info("[{}][PERMISSION][START] rule={}");
    if (!permission.canCreatePendingManual(createUser, category, formDto)) {
      throw new UnauthorizedException("判定エラー");
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    manual.setTitle(formDto.getTitle());
    manual.setContent(formDto.getContent());
    manual.markCreatedNow();
    manual.markUpdatedNow();
    manual.submitPENDING();
    manual.setCategory(category);
    manual.setCreatedByUser(createUser);
    // Rollbackフラグ初期化
    manual.markUnreadRolledback();
    log.info("[{}][{}][PERSIST][START] action={} id={}");
    Manual savedManual = manualRepository.save(manual);
    log.info("[{}][{}][PERSIST][DONE] action={} id={}");
    // 承認通知作成
    notificationService.createSubmitNotifications(principal, savedManual);
  }

  // 複製編集保存(DRAFT)
  public void saveDraftForCopy(
      Long manualId,
      ManualDraftDto formDto,
      Principal principal) {

    Manual manual = new Manual();
    Category category = categoryService.getCategoryById(formDto.getCategoryId());

    User playUser = userService.getUserByPrincipal(principal);
    log.info("[{}][PERMISSION][START] rule={}");
    if (!permission.canSaveDraftForCopy(playUser,
        category,
        formDto.getChangeNote())) {
      throw new UnauthorizedException("判定エラー");
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    manual.setTitle(formDto.getTitle());
    manual.setContent(formDto.getContent());
    manual.markUpdatedNow();
    manual.markCreatedNow();
    manual.markStatusDRAFT();
    manual.setCategory(category);
    manual.setCreatedByUser(playUser);
    // Rollbackフラグ初期化
    manual.markUnreadRolledback();
    log.info("[{}][{}][PERSIST][START] action={} id={}");
    Manual savedManual = manualRepository.save(manual);
    log.info("[{}][{}][PERSIST][DONE] action={} id={}");
    historyService.createHistory(savedManual,
        formDto.getChangeNote(),
        principal);
  }

  // 複製編集公開(Pending)
  public void savePendingForCopy(
      Long manualId,
      ManualEditFormDto formDto,
      Principal principal) {

    Manual manual = new Manual();
    Category category = categoryService.getCategoryById(formDto.getCategoryId());

    User playUser = userService.getUserByPrincipal(principal);
    log.info("[{}][PERMISSION][START] rule={}");
    if (!permission.canSavePendingForCopy(
        playUser,
        category,
        formDto.getChangeNote(),
        formDto.getTitle(),
        formDto.getContent())) {
      throw new UnauthorizedException("判定エラー");
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    manual.setTitle(formDto.getTitle());
    manual.setContent(formDto.getContent());
    manual.markUpdatedNow();
    manual.markCreatedNow();
    manual.submitPENDING();
    manual.setCategory(category);
    manual.setCreatedByUser(playUser);
    // Rollbackフラグ初期化
    manual.markUnreadRolledback();
    log.info("[{}][{}][PERSIST][START] action={} id={}");
    Manual savedManual = manualRepository.save(manual);
    log.info("[{}][{}][PERSIST][DONE] action={} id={}");
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

    Manual manual = query.findManualOrThrow(manualId);
    Category category = categoryService.getCategoryById(formDto.getCategoryId());

    User playUser = userService.getUserByPrincipal(principal);
    log.info("[{}][PERMISSION][START] rule={}");
    if (!permission.canEditToDraft(playUser, manual)) {
      throw new UnauthorizedException("判定エラー");
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    manual.setTitle(formDto.getTitle());
    manual.setContent(formDto.getContent());
    manual.markUpdatedNow();
    manual.markStatusDRAFT();
    manual.setCategory(category);
    log.info("[{}][{}][PERSIST][START] action={} id={}");
    Manual savedManual = manualRepository.save(manual);
    log.info("[{}][{}][PERSIST][DONE] action={} id={}");
    notificationService.deletePendingApprovalNotificationsByManualId(manualId);
    log.info("[{}][PERMISSION][START] rule={}");
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

    Manual manual = query.findManualOrThrow(manualId);
    Category category = categoryService.getCategoryById(formDto.getCategoryId());

    User playUser = userService.getUserByPrincipal(principal);
    log.info("[{}][PERMISSION][START] rule={}");
    if (!permission.canEditToPending(playUser, manual, formDto)) {
      throw new UnauthorizedException("判定エラー");
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    manual.setTitle(formDto.getTitle());
    manual.setContent(formDto.getContent());
    manual.markUpdatedNow();
    manual.submitPENDING();
    manual.setCategory(category);
    log.info("[{}][{}][PERSIST][START] action={} id={}");
    Manual savedManual = manualRepository.save(manual);
    log.info("[{}][PERMISSION][START] rule={}");
    if (!formDto.getChangeNote().isBlank() && formDto.getChangeNote() != null) {
      historyService.createHistory(savedManual,
          formDto.getChangeNote(),
          principal);
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    // 承認通知作成
    notificationService.deleteRollbackNotification(manualId, playUser);
    notificationService.createSubmitNotifications(principal, savedManual);
  }

  // ================================================
  // ワンボタンアクション
  // ================================================

  // 公開
  public void submitManual(
      Long manualId,
      Principal principal) {

    Manual manual = query.findManualOrThrow(manualId);
    User playUser = userService.getUserByPrincipal(principal);
    log.info("[{}][PERMISSION][START] rule={}");
    if (!permission.canPending(manual, playUser)) {
      throw new UnauthorizedException("判定エラー");
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    manual.submitPENDING();
    manual.markUpdatedNow();
    log.info("[{}][{}][PERSIST][START] action={} id={}");
    Manual savedManual = manualRepository.save(manual);
    log.info("[{}][{}][PERSIST][DONE] action={} id={}");
    notificationService.createSubmitNotifications(principal, savedManual);
  }

  // 承認
  public void approveManual(
      Long manualId,
      String changeNote,
      Principal principal) {

    Manual manual = query.findManualOrThrow(manualId);
    User playUser = userService.getUserByPrincipal(principal);
    Category category = manual.getCategory();
    log.info("[{}][PERMISSION][START] rule={}");
    if (!permission.canApproveManual(manual, category, playUser)) {
      throw new UnauthorizedException("判定エラー");
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    manual.approve();
    manual.markUpdatedNow();
    manual.markApprovedNow();
    log.info("[{}][{}][PERSIST][START] action={} id={}");
    Manual savedManual = manualRepository.save(manual);
    log.info("[{}][{}][PERSIST][DONE] action={} id={}");
    log.info("[{}][PERMISSION][START] rule={}");
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

    Manual manual = query.findManualOrThrow(manualId);
    User playUser = userService.getUserByPrincipal(principal);
    log.info("[{}][PERMISSION][START] rule={}");
    if (!permission.canRollbackManual(manual, playUser, requestDto.getChangeNote())) {
      throw new UnauthorizedException("判定エラー");
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    manual.markReadRolledback();
    manual.markUpdatedNow();
    manual.rollbackToDraft();
    log.info("[{}][{}][PERSIST][START] action={} id={}");
    Manual savedManual = manualRepository.save(manual);
    log.info("[{}][{}][PERSIST][DONE] action={} id={}");
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

    Manual manual = query.findManualOrThrow(manualId);
    User playUser = userService.getUserByPrincipal(principal);
    Category category = categoryService.getCategoryById(manual.getCategory().getId());

    log.info("[{}][PERMISSION][START] rule={}");
    if (!permission.canArchiveManual(
        playUser, manual, category, actionRequestDto.getChangeNote())) {
      throw new UnauthorizedException("判定エラー");
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    manual.archive();
    manual.markUpdatedNow();
    log.info("[{}][{}][PERSIST][START] action={} id={}");
    Manual savedManual = manualRepository.save(manual);
    log.info("[{}][{}][PERSIST][DONE] action={} id={}");
    historyService.createHistory(
        savedManual, actionRequestDto.getChangeNote(), principal);
    notificationService.deleteByManualIdNotification(manualId);
  }

  // 復帰
  public void restoreManual(
      Long manualId,
      ManualActionRequestDto requestDto,
      Principal principal) {

    Manual manual = query.findManualOrThrow(manualId);
    User playUser = userService.getUserByPrincipal(principal);
    log.info("[{}][PERMISSION][START] rule={}");
    if (!permission.canRestoreManual(manual, playUser, requestDto.getChangeNote())) {
      throw new UnauthorizedException("判定エラー");
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    manual.restoreToApproved();
    manual.markUpdatedNow();
    log.info("[{}][{}][PERSIST][START] action={} id={}");
    Manual savedManual = manualRepository.save(manual);
    log.info("[{}][{}][PERSIST][DONE] action={} id={}");
    historyService.createHistory(
        savedManual, requestDto.getChangeNote(), principal);
  }

}
