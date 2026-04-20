package com.example.manual.service;

import java.security.Principal;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.manual.dto.ManualActionRequestDto;
import com.example.manual.dto.ManualEditFormDto;
import com.example.manual.entity.Category;
import com.example.manual.entity.Manual;
import com.example.manual.entity.User;
import com.example.manual.enums.ManualStatus;
import com.example.manual.enums.UserRole;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.exception.NotFoundException;
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

  public ManualCommandService(
      ManualRepository manualRepository,
      ManualHistoryService manualHistoryService,
      UserService userService,
      CategoryService categoryService,
      NotificationService notificationService,
      ManualQueryService manualQueryService) {

    this.manualRepository = manualRepository;
    this.historyService = manualHistoryService;
    this.userService = userService;
    this.categoryService = categoryService;
    this.notificationService = notificationService;
    this.query = manualQueryService;
  }

  // ============================================
  // DB保存処理
  // ============================================

  // 新規作成保存（DRAFT）
  public void saveDraftForCreate(
      ManualEditFormDto formDto,
      Principal principal) {
    log.info("start");
    Manual manual = new Manual();
    Category category = categoryService.getCategoryById(formDto.getCategoryId());
    User createUser = userService.getUserByPrincipal(principal);

    if (!canSaveDraftForCreate(createUser, manual, category)) {
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
    if (!canCreatePendingManual(createUser, category, formDto)) {
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
      ManualEditFormDto formDto,
      Principal principal) {
    log.info("start");
    Manual manual = query.findManualOrThrow(manualId);
    Category category = categoryService.getCategoryById(formDto.getCategoryId());

    User playUser = userService.getUserByPrincipal(principal);
    if (!canSaveDraftForCopy(playUser,
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
    if (!canSavePendingForCopy(
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

  // 編集マニュアル公開(Draft)
  public void editToDraft(
      Long manualId,
      ManualEditFormDto formDto,
      Principal principal) {
    log.info("start");
    Manual manual = query.findManualOrThrow(manualId);
    Category category = categoryService.getCategoryById(formDto.getCategoryId());

    User playUser = userService.getUserByPrincipal(principal);
    if (!canEditToDraft(playUser, manual)) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.setTitle(formDto.getTitle());
    manual.setContent(formDto.getContent());
    manual.markUpdatedNow();
    manual.markStatusDRAFT();
    manual.setCategory(category);
    Manual savedManual = manualRepository.save(manual);
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
    if (!canEditToPending(playUser, manual, formDto)) {
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

  // 承認
  public void approveManual(
      Long manualId,
      String changeNote,
      Principal principal) {
    log.info("start");
    Manual manual = query.findManualOrThrow(manualId);
    User playUser = userService.getUserByPrincipal(principal);
    Category category = categoryService.getCategoryById(manual.getId());
    if (!canApproveManual(manual, category, playUser)) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.approve();
    manual.markUpdatedNow();
    manual.markApprovedNow();
    Manual savedManual = manualRepository.save(manual);
    if (changeNote != null && !changeNote.isBlank()) {
    } else {
      historyService.createHistory(
          savedManual,
          changeNote,
          principal);
    }
    // 承認通知削除
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
    if (!canRollbackManual(manual, playUser, requestDto.getChangeNote())) {
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

    if (!canArchiveManual(
        playUser, manual, category, actionRequestDto.getChangeNote())) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.archive();
    manual.markUpdatedNow();
    Manual savedManual = manualRepository.save(manual);
    historyService.createHistory(
        savedManual, actionRequestDto.getChangeNote(), principal);
  }

  // 復帰
  public void restoreManual(
      Long manualId,
      ManualActionRequestDto requestDto,
      Principal principal) {
    log.info("start");
    Manual manual = query.findManualOrThrow(manualId);
    User playUser = userService.getUserByPrincipal(principal);
    if (!canRestoreManual(manual, playUser, requestDto.getChangeNote())) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.restoreToApproved();
    manual.markUpdatedNow();
    Manual savedManual = manualRepository.save(manual);
    historyService.createHistory(
        savedManual, requestDto.getChangeNote(), principal);
  }

  // ============================
  // 権限判定
  // ============================

  private boolean canArchiveManual(
      User playUser,
      Manual manual,
      Category category,
      String changeNote) {
    log.info("start");
    if (!isApproverOrAdmin(playUser)) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    if (!isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (!isStatusDraft(manual) &&
        !isStatusPending(manual) &&
        !isStatusApproved(manual)) {
      throw new InvalidStateException("マニュアルのステータスが条件を満たしていません。");
    }
    if (!isCategoryActivate(category)) {
      throw new UnauthorizedException(
          "指定カテゴリはアクティブではありません。");
    }
    if (!isChangeNote(changeNote)) {
      throw new InvalidStateException("更新期歴は必須です。");
    }
    if (!isTitleAndContent(manual.getTitle(), manual.getContent())) {
      throw new NotFoundException("タイトル・コンテンツがありません。");
    }
    return true;
  }

  // TODO: 通知実装予定
  private boolean canApproveManual(
      Manual manual,
      Category category,
      User playUser) {
    log.info("start");
    if (!isApproverOrAdmin(playUser)) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    if (!isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (!isStatusPending(manual)) {
      throw new InvalidStateException(
          "承認ができるのはステータス:PENDINGのマニュアルのみです。");
    }
    if (!isOwner(manual.getCreatedByUser(), playUser)) {
      throw new InvalidStateException(
          "自分が作成したマニュアルの承認をすることは出来ません。");
    }
    if (!isActive(playUser)) {
      throw new InvalidStateException(
          "有効でないカテゴリーでは承認することが出来ません。");
    }
    return true;
  }

  private boolean canRollbackManual(
      Manual manual,
      User playUser,
      String changeNote) {
    log.info("start");
    if (!isApproverOrAdmin(playUser)) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    if (!isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (!isStatusPending(manual)) {
      throw new InvalidStateException(
          "差し戻しができるのはステータス:PENDINGのマニュアルのみです。");
    }
    if (!isOwner(manual.getCreatedByUser(), playUser)) {
      throw new InvalidStateException(
          "自分が作成したマニュアルを差し戻しすることは出来ません。");
    }
    if (!isChangeNote(changeNote)) {
      throw new InvalidStateException("更新期歴は必須です。");
    }
    return true;
  }

  private boolean canRestoreManual(
      Manual manual,
      User playUser,
      String changeNote) {
    log.info("start");
    if (!isApproverOrAdmin(playUser)) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    if (!isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (!isStatusArchived(manual)) {
      throw new InvalidStateException(
          "復帰ができるのはステータス:ARCHIVEDのマニュアルのみです。");
    }
    if (!isActive(playUser)) {
      throw new InvalidStateException(
          "使用中カテゴリでのみ復帰が出来ます。");
    }
    if (!isChangeNote(changeNote)) {
      throw new InvalidStateException("更新履歴は必須です。");
    }
    return true;
  }

  private boolean canSaveDraftForCreate(
      User playUser,
      Manual manual,
      Category category) {
    log.info("start");

    if (!isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (!isCategoryActivate(category)) {
      throw new InvalidStateException(
          "使用中カテゴリでのみ復帰が出来ます。");
    }
    return true;
  }

  private boolean canCreatePendingManual(
      User playUser,
      Category category,
      ManualEditFormDto formDto) {
    log.info("start");
    if (!isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (!isCategoryActivate(category)) {
      throw new InvalidStateException(
          "使用停止中のカテゴリーが選択されています。");
    }
    if (!isTitleAndContent(formDto.getTitle(), formDto.getContent())) {

    }
    return true;
  }

  private boolean canSaveDraftForCopy(
      User playUser,
      Category category,
      String changeNote) {
    log.info("start");
    // アクティブユーザー アクティブカテゴリー チェンジノート必須
    if (!isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (!isCategoryActivate(category)) {
      throw new InvalidStateException(
          "停止中カテゴリにマニュアルは作成できません。");
    }
    if (!isChangeNote(changeNote)) {
      throw new InvalidStateException("更新履歴は必須です。");
    }
    return true;
  }

  private boolean canSavePendingForCopy(
      User playUser,
      Category category,
      String changeNote,
      String title, String content) {
    log.info("start");
    // アクティブユーザー アクティブカテゴリー チェンジノート必須 タイトル・コンテンツ必須
    if (!isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (!isCategoryActivate(category)) {
      throw new InvalidStateException(
          "停止中カテゴリにマニュアルは作成できません。");
    }
    if (!isChangeNote(changeNote)) {
      throw new InvalidStateException("更新履歴は必須です。");
    }
    if (!isTitleAndContent(title, content)) {
      throw new InvalidStateException("タイトル・本文は必須です。");
    }
    return true;
  }

  private boolean canEditToPending(
      User playUser,
      Manual manual, ManualEditFormDto formDto) {
    log.info("start");
    // アクティブユーザー ステータスドラフト・ペンディングのみ 作成者のみ編集可 アクティブカテゴリ
    Category category = categoryService.getCategoryById(manual.getCategory().getId());
    if (!isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (!isCategoryActivate(category)) {
      throw new InvalidStateException(
          "停止中カテゴリにマニュアルは作成できません。");
    }
    if (!isStatusPending(manual) && !isStatusDraft(manual)) {
      throw new InvalidStateException(
          "編集ができるのはステータス:DRAFT/PENDINGのマニュアルのみです。");
    }
    if (!isOwner(manual.getCreatedByUser(), playUser)) {
      throw new InvalidStateException(
          "自分が作成したマニュアル以外を編集することはできません。");
    }
    if (!isTitleAndContent(formDto.getTitle(), formDto.getContent())) {
      throw new InvalidStateException(
          "タイトルと本文は必須項目です。");
    }
    return true;
  }

  private boolean canEditToDraft(
      User playUser,
      Manual manual) {
    log.info("start");
    // アクティブユーザー ステータスドラフト・ペンディングのみ 作成者のみ編集可 アクティブカテゴリ
    Category category = categoryService.getCategoryById(manual.getCategory().getId());
    if (!isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (!isCategoryActivate(category)) {
      throw new InvalidStateException(
          "停止中カテゴリにマニュアルは作成できません。");
    }
    if (!isStatusPending(manual) && !isStatusDraft(manual)) {
      throw new InvalidStateException(
          "編集ができるのはステータス:DRAFT/PENDINGのマニュアルのみです。");
    }
    if (!isOwner(manual.getCreatedByUser(), playUser)) {
      throw new InvalidStateException(
          "自分が作成したマニュアル以外を編集することはできません。");
    }
    return true;
  }

  // ============================
  // 権限共通処理
  // ============================

  public boolean isActive(User playUser) {
    if (playUser.isActive()) {
      return true;
    }
    return false;
  }

  public boolean isUserActive(User playUser) {
    if (playUser.isActive() == true) {
      return true;
    }
    return false;
  }

  public boolean isOwner(User createdUser, User playUser) {
    if (Objects.equals(createdUser.getId(), playUser.getId())) {
      return true;
    }
    return false;
  }

  public boolean isApproverOrAdmin(User playUser) {
    if (playUser.getRole() == UserRole.ADMIN ||
        playUser.getRole() == UserRole.APPROVER) {
      return true;
    }
    return false;
  }

  public boolean isStatusDraft(Manual manual) {
    if (manual.getStatus() == ManualStatus.DRAFT) {
      return true;
    }
    return false;
  }

  public boolean isStatusPending(Manual manual) {
    if (manual.getStatus() == ManualStatus.PENDING) {
      return true;
    }
    return false;
  }

  public boolean isStatusApproved(Manual manual) {
    if (manual.getStatus() == ManualStatus.APPROVED) {
      return true;
    }
    return false;
  }

  public boolean isStatusArchived(Manual manual) {
    if (manual.getStatus() == ManualStatus.ARCHIVED) {
      return true;
    }
    return false;
  }

  public boolean isCategoryActivate(Category category) {
    if (category.isActive()) {
      return true;
    }
    return false;
  }

  public boolean isChangeNote(String changeNote) {
    if (changeNote == null || changeNote.isBlank()) {
      return false;
    }
    return true;
  }

  public boolean isTitleAndContent(String title, String content) {
    if (title == null ||
        content == null ||
        title.isBlank() ||
        content.isBlank()) {
      return false;
    }
    return true;
  }

  public boolean isGuest(User playUser) {
    if (playUser.getRole() == UserRole.GUEST) {
      return true;
    }
    return false;
  }

}
