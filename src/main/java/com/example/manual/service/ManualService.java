package com.example.manual.service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.manual.dto.CategoryResponseDto;
import com.example.manual.dto.IndexSummaryDto;
import com.example.manual.dto.ManualDetailDto;
import com.example.manual.dto.ManualDetailHistoryDto;
import com.example.manual.dto.ManualDraftRequestDto;
import com.example.manual.dto.ManualHistoryDto;
import com.example.manual.dto.ManualIndexDto;
import com.example.manual.dto.ManualRequestDto;
import com.example.manual.dto.ManualResponseDto;
import com.example.manual.dto.ManualSearchConditionDto;
import com.example.manual.dto.UserResponseDto;
import com.example.manual.entity.Category;
import com.example.manual.entity.Manual;
import com.example.manual.entity.User;
import com.example.manual.enums.ManualStatus;
import com.example.manual.enums.UserRole;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.exception.UnauthorizedException;
import com.example.manual.repository.ManualRepository;
import com.example.manual.repository.ManualSpecification;

@Service
public class ManualService {

  private static final Logger log = LoggerFactory.getLogger(ManualService.class);

  private final ManualRepository manualRepository;
  private final ManualHistoryService manualHistoryService;
  private final UserService userService;
  private final CategoryService categoryService;
  private final NotificationService notificationService;

  public ManualService(
      ManualRepository manualRepository,
      ManualHistoryService manualHistoryService,
      UserService userService,
      CategoryService categoryService,
      NotificationService notificationService) {

    this.manualRepository = manualRepository;
    this.manualHistoryService = manualHistoryService;
    this.userService = userService;
    this.categoryService = categoryService;
    this.notificationService = notificationService;
  }

  // ============================================
  // 画面表示
  // ============================================

  // index表示
  public ManualIndexDto showIndex(
      Principal principal,
      List<Manual> manuals) {
    log.info("start");
    // 標準表示項目取得
    ManualIndexDto listDto = buildIndexCommonData(principal);
    // 一覧リスト ページ毎に分ける必要有
    List<ManualResponseDto> ManualDtos = buildIndexWithManuals(manuals);

    listDto.setManuals(ManualDtos);

    return listDto;
  }

  // ============================================
  // DB保存処理
  // ============================================

  // 新規作成保存（DRAFT）
  public void saveDraftForCreate(
      ManualDraftRequestDto requestDto,
      Principal principal) {
    log.info("start");
    Manual manual = new Manual();
    Category category = categoryService.getCategoryById(requestDto.getCategoryId());

    User createUser = userService.getUserByPrincipal(principal);
    if (!canSaveDraftForCreate(createUser, manual)) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.setTitle(requestDto.getTitle());
    manual.setContent(requestDto.getContent());
    manual.markUpdatedNow();
    manual.markCreatedNow();
    manual.markStatusDRAFT();
    manual.setCategory(category);
    manual.setCreatedByUser(createUser);
    manual.markUnreadRolledback();
    manualRepository.save(manual);
  }

  // 新規作成マニュアル公開(PENDING)
  // public void createPendingManual(
  // ManualRequestDto requestDto,
  // Principal principal) {
  // log.info("start");
  // Manual manual = new Manual();
  // Category category =
  // categoryService.getCategoryById(requestDto.getCategoryId());
  // User createUser = userService.getUserByPrincipal(principal);
  // if (!canSubmitToPending(createUser, category)) {
  // throw new UnauthorizedException("判定エラー");
  // }
  // manual.setTitle(requestDto.getTitle());
  // manual.setContent(requestDto.getContent());
  // manual.markCreatedNow();
  // manual.markUpdatedNow();
  // manual.submitPENDING();
  // manual.setCategory(category);
  // manual.setCreatedByUser(createUser);
  // manual.markUnreadRolledback();
  // Manual savedManual = manualRepository.save(manual);
  // notificationService.createSubmitNotifications(principal, savedManual);
  // }

  // 複製編集保存(DRAFT)
  public void saveDraftForCopy(
      Long manualId,
      ManualRequestDto requestDto,
      Principal principal) {
    log.info("start");
    Manual manual = findManualOrThrow(manualId);
    Category category = categoryService.getCategoryById(requestDto.getCategoryId());

    User playUser = userService.getUserByPrincipal(principal);
    if (!canSaveDraftForCopy(
        playUser,
        category,
        requestDto.getChangeNote())) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.setTitle(requestDto.getTitle());
    manual.setContent(requestDto.getContent());
    manual.markUpdatedNow();
    manual.markCreatedNow();
    manual.markStatusDRAFT();
    manual.setCategory(category);
    manual.setCreatedByUser(playUser);
    manual.markUnreadRolledback();
    Manual savedManual = manualRepository.save(manual);
    manualHistoryService.createHistory(
        savedManual,
        requestDto.getChangeNote(),
        principal);
  }

  // 編集マニュアル公開(PENDING)
  public void editToPending(
      Long manualId,
      ManualRequestDto requestDto,
      Principal principal) {
    log.info("start");
    Manual manual = findManualOrThrow(manualId);
    Category category = categoryService.getCategoryById(requestDto.getCategoryId());

    User user = userService.getUserByPrincipal(principal);
    if (!canEditToPending(user, manual)) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.setTitle(requestDto.getTitle());
    manual.setContent(requestDto.getContent());
    manual.markCreatedNow();
    manual.markUpdatedNow();
    manual.submitPENDING();
    manual.setCategory(category);
    manual.setCreatedByUser(user);
    manual.markUnreadRolledback();
    Manual savedManual = manualRepository.save(manual);
    manualHistoryService.createHistory(
        savedManual,
        requestDto.getChangeNote(),
        principal);
    notificationService.deleteRollbackNotification(manualId, user);
  }

  // TODO: 通知実装予定
  // 承認（チェンジノート無）
  // public void approveManual(
  // Long manualId,
  // Principal principal) {
  // log.info("start");
  // Manual manual = findManualOrThrow(manualId);
  // User playUser = userService.getUserByPrincipal(principal);
  // if (!canApproveManual(
  // manual,
  // manual.getCategory(),
  // playUser)) {
  // throw new UnauthorizedException("判定エラー");
  // }

  // manual.approve();
  // manual.markUpdatedNow();
  // manual.markApprovedNow();
  // manualRepository.save(manual);
  // notificationService.deletePendingApprovalNotificationsByManualId(manualId);
  // }

  // 承認（チェンジノート有）
  public void approveManualWithComment(
      Long manualId,
      String changeNote,
      Principal principal) {
    log.info("start");
    Manual manual = findManualOrThrow(manualId);
    User playUser = userService.getUserByPrincipal(principal);
    if (!canApproveManual(manual, playUser)) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.approve();
    manual.markUpdatedNow();
    manual.markApprovedNow();
    Manual savedManual = manualRepository.save(manual);
    if (changeNote == null || changeNote.isBlank()) {
    } else {
      manualHistoryService.createHistory(
          savedManual,
          changeNote,
          principal);
    }
    notificationService.deletePendingApprovalNotificationsByManualId(manualId);
  }

  // 差し戻し
  // public void rollbackEditManual(
  // Long manualId,
  // ManualActionRequestDto requestDto,
  // Principal principal) {
  // log.info("start");
  // Manual manual = findManualOrThrow(manualId);
  // User playUser = userService.getUserByPrincipal(principal);
  // if (!canRollbackManual(manual, playUser, requestDto.getChangeNote())) {
  // throw new UnauthorizedException("判定エラー");
  // }
  // manual.markReadRolledback();
  // manual.markUpdatedNow();
  // manual.rollbackToDraft();
  // Manual savedManual = manualRepository.save(manual);
  // manualHistoryService.createHistory(
  // savedManual, requestDto.getChangeNote(), principal);
  // }

  // アーカイブ
  // public void archiveManual(
  // Long manualId,
  // ManualActionRequestDto actionRequestDto,
  // Principal principal) {
  // log.info("start");
  // Manual manual = findManualOrThrow(manualId);
  // User user = userService.getUserByPrincipal(principal);
  // Category category =
  // categoryService.getCategoryById(manual.getCategory().getId());

  // if (!canArchiveManual(
  // manual, user, actionRequestDto.getChangeNote())) {
  // throw new UnauthorizedException("判定エラー");
  // }
  // manual.archive();
  // manual.markUpdatedNow();
  // Manual savedManual = manualRepository.save(manual);
  // manualHistoryService.createHistory(
  // savedManual, actionRequestDto.getChangeNote(), principal);
  // }

  // 復帰
  // public void restoreManual(
  // Long manualId,
  // ManualActionRequestDto requestDto,
  // Principal principal) {
  // log.info("start");
  // Manual manual = findManualOrThrow(manualId);
  // User playUser = userService.getUserByPrincipal(principal);
  // if (!canRestoreManual(playUser, manual, requestDto.getChangeNote())) {
  // throw new UnauthorizedException("判定エラー");
  // }
  // manual.restoreToApproved();
  // manual.markUpdatedNow();
  // Manual savedManual = manualRepository.save(manual);
  // manualHistoryService.createHistory(
  // savedManual, requestDto.getChangeNote(), principal);
  // }

  // ============================================
  // 新規タブ画面遷移
  // ============================================

  // 新規作成(新規タブ)
  public List<CategoryResponseDto> goToNewCreatePage(Principal principal) {
    log.info("start");
    User playUser = userService.getUserByPrincipal(principal);
    if (!canGoToNewCreatePage(playUser)) {
      throw new InvalidStateException("判定エラー");
    }
    List<CategoryResponseDto> categoryDto = categoryService.getActiveCategoryDtos();
    return categoryDto;
  }

  // 編集（新規タブ）
  public ManualResponseDto goToEditPage(
      Long manualId,
      Principal principal) {
    log.info("start");
    Manual manual = findManualOrThrow(manualId);
    User playUser = userService.getUserByPrincipal(principal);

    if (!canGoToEditPage(
        playUser, manual)) {
      throw new UnauthorizedException("判定エラー");
    }
    return toManualFormInputDto(manual);
  }

  // 複製（新規タブ）
  public ManualResponseDto goToCopyPage(
      Long manualId,
      Principal principal) {
    log.info("start");
    Manual manual = findManualOrThrow(manualId);
    User playUser = userService.getUserByPrincipal(principal);
    if (!canGoToCopyPage(playUser)) {
      throw new UnauthorizedException("判定エラー");
    }
    return toManualFormInputDto(manual);
  }

  // マニュアル詳細画面（新規タブ）
  public ManualDetailDto goToDetailPage(
      Long manualId,
      Principal principal) {
    log.info("start");
    Manual manual = findManualOrThrow(manualId);
    User playUser = userService.getUserByPrincipal(principal);
    if (!canGoToDetailPage(playUser)) {
      throw new UnauthorizedException("判定エラー");
    }
    ManualDetailDto baseDetailDto = toManualDetailDto(manual);
    ManualDetailDto detailDto = buildDetailPermissions(manual, playUser, baseDetailDto);

    return detailDto;
  }

  // ============================
  // 取得・検索
  // ===========================

  // index 検索表示 取得

  public List<Manual> findManualsBySearch(
      ManualSearchConditionDto condition,
      Principal principal) {
    log.info("start");
    User targetUser = userService.getUserByPrincipal(principal);
    if (!canFindManualsBySearch(targetUser)) {
      throw new UnauthorizedException("判定エラー");
    }

    Specification<Manual> specification = (root, query, cb) -> cb.conjunction();

    Specification<Manual> keywordSpec = ManualSpecification.containsKeyword(condition.getKeyword());
    if (keywordSpec != null) {
      specification = specification.and(keywordSpec);
    }

    Specification<Manual> categorySpec = ManualSpecification.hasCategoryIds(condition.getCategoryIds());
    if (categorySpec != null) {
      specification = specification.and(categorySpec);
    }

    Specification<Manual> statusSpec = ManualSpecification.hasStatuses(condition.getStatuses());
    if (statusSpec != null) {
      specification = specification.and(statusSpec);
    }

    List<Manual> manualList = manualRepository.findAll(
        specification,
        Sort.by(Sort.Direction.DESC, "updatedAt"));

    return manualList;
  }

  // index quickView表示取得
  public IndexSummaryDto getIndexSummary(Principal principal) {
    log.info("start");
    // 通知欄２つの数字を取得
    int unreadRollbackCount = notificationService.unreadRollbackCount(principal);
    int unreadPendingCount = notificationService.unreadPendingCount(principal);
    // count 自分の作成マニュアル
    int countUserCreatedManual = countUserCreatedManual(principal);
    // count 申請中
    int countCreatedPendingManual = countCreatedPendingManual(principal);
    // count 最近更新（７日間）
    int countRecentWeeklyManual = countRecentWeeklyManuals();

    IndexSummaryDto summaryDto = new IndexSummaryDto();
    summaryDto.setCountUserCreatedManual(countUserCreatedManual);
    summaryDto.setCountCreatedPendingManual(countCreatedPendingManual);
    summaryDto.setCountRecentWeeklyManual(countRecentWeeklyManual);
    summaryDto.setUnreadRollbackCount(unreadRollbackCount);
    summaryDto.setUnreadPendingCount(unreadPendingCount);

    return summaryDto;
  }

  // status一覧を返す
  public List<ManualStatus> getManualStatuses() {
    log.info("start");
    List<ManualStatus> responseStatus = Arrays.asList(ManualStatus.values());

    return responseStatus;
  }

  // status一覧(Draft以外)を返す
  public List<ManualResponseDto> getDefaultStatuses() {
    log.info("start");
    List<ManualResponseDto> responseStatus = new ArrayList<>();
    ManualStatus status[] = ManualStatus.values();
    for (ManualStatus manualStatus : status) {
      ManualResponseDto defaultStatus = new ManualResponseDto();
      if (manualStatus == ManualStatus.DRAFT) {
      } else {
        defaultStatus.setStatus(manualStatus);
        defaultStatus.setStatusLabel(getStatusLabel(manualStatus));
        responseStatus.add(defaultStatus);
      }
    }
    return responseStatus;
  }

  // MyPege 差し戻し（自分作成）のデータを渡す。
  public List<Manual> findCreatedRollbackManuals(User user) {
    log.info("start");
    List<Manual> rollbackList = manualRepository.findByIsRolledbackTrueAndCreatedByUserOrderByUpdatedAtDesc(
        user);

    return rollbackList;
  }

  // MyPege PENDINGの全マニュアル（自分作成以外）のデータを渡す。
  public List<Manual> findPendingManuals(User user) {
    log.info("start");
    List<Manual> pendingManualList = manualRepository.findByCreatedByUserNotAndStatusOrderByUpdatedAtDesc(
        user, ManualStatus.PENDING);
    return pendingManualList;
  }

  // index 自分作成分のマニュアルデータ一覧を返す。
  public List<Manual> findMyCreatedManuals(Principal principal) {
    log.info("start");
    User targetUser = userService.getUserByPrincipal(principal);
    List<Manual> manualList = manualRepository.findByCreatedByUserOrderByCreatedAtDesc(
        targetUser);
    return manualList;
  }

  // index 自分作成PENDINGのデータ一覧を返す。
  public List<Manual> findMyPendingManuals(Principal principal) {
    log.info("start");
    User targetUser = userService.getUserByPrincipal(principal);
    List<Manual> manualList = manualRepository.findByCreatedByUserAndStatusOrderByUpdatedAtDesc(
        targetUser, ManualStatus.PENDING);
    return manualList;
  }

  // index 最近７日間の更新のデータ一覧を返す。
  public List<Manual> findRecentlyUpdatedManuals() {
    log.info("start");
    List<Manual> manualList = manualRepository.findByUpdatedAtAfterOrderByUpdatedAtDesc(
        LocalDateTime.now().minusDays(7));
    return manualList;
  }

  // index count自分作成PENDINGの数を返す。
  public int countCreatedPendingManual(Principal principal) {
    log.info("start");
    User targetUser = userService.getUserByPrincipal(principal);
    Long count = manualRepository.countByCreatedByUserAndStatus(
        targetUser, ManualStatus.PENDING);
    int targetCount = Math.toIntExact(count);
    return targetCount;
  }

  // index count最近7日間の更新の数を返す。
  public int countRecentWeeklyManuals() {
    log.info("start");
    Long count = manualRepository.countByUpdatedAtAfter(
        LocalDateTime.now().minusDays(7));
    int targetCount = Math.toIntExact(count);
    return targetCount;
  }

  // MyPage index通知 count自分以外作成PENDINGの数を返す。
  public int countNotUserCreatedPendingManualList(Principal principal) {
    log.info("start");
    User targetUser = userService.getUserByPrincipal(principal);
    Long count = manualRepository.countByCreatedByUserNotAndStatus(
        targetUser,
        ManualStatus.PENDING);
    int targetCount = Math.toIntExact(count);
    return targetCount;
  }

  // MyPage index通知 count作成者自分の差し戻しマニュアル数を返す。
  public int countMyRollbackManual(Principal principal) {
    log.info("start");
    User targetUser = userService.getUserByPrincipal(principal);
    Long count = manualRepository.countByIsRolledbackTrueAndCreatedByUser(
        targetUser);
    int targetCount = Math.toIntExact(count);
    return targetCount;
  }

  // index count自分作成分の数を返す。
  public int countUserCreatedManual(Principal principal) {
    log.info("start");
    User targetUser = userService.getUserByPrincipal(principal);
    Long count = manualRepository.countByCreatedByUser(targetUser);
    int targetCount = Math.toIntExact(count);
    return targetCount;
  }

  // ============================
  // Dto詰替
  // ============================

  private ManualResponseDto toManualFormInputDto(Manual manual) {
    log.info("start");
    ManualResponseDto responseDto = new ManualResponseDto();
    responseDto.setManualId(manual.getId());
    responseDto.setCreatedAt(manual.getCreatedAt());
    responseDto.setUpdatedAt(manual.getUpdatedAt());
    CategoryResponseDto categoryDto = categoryService.toCategoryDto(manual.getCategory());
    responseDto.setCategoryDto(categoryDto);
    UserResponseDto userResponseDto = userService.toCreatedUserDto(manual.getCreatedByUser());
    responseDto.setCreatedUserDto(userResponseDto);
    responseDto.setContent(manual.getContent());
    responseDto.setTitle(manual.getTitle());
    return responseDto;
  }

  public List<ManualResponseDto> buildIndexWithManuals(List<Manual> manualList) {
    List<ManualResponseDto> responseDtos = new ArrayList<>();
    for (Manual manual : manualList) {
      ManualResponseDto responseDto = new ManualResponseDto();
      responseDto.setManualId(manual.getId());
      responseDto.setTitle(manual.getTitle());
      responseDto.setContent(manual.getContent());
      UserResponseDto userDto = userService.toCreatedUserDto(manual.getCreatedByUser());
      responseDto.setCreatedUserDto(userDto);
      responseDto.setStatus(manual.getStatus());
      responseDto.setStatusLabel(getStatusLabel(manual.getStatus()));
      responseDto.setCreatedAt(manual.getCreatedAt());
      responseDto.setUpdatedAt(manual.getUpdatedAt());
      CategoryResponseDto categoryDto = categoryService.toCategoryDto(manual.getCategory());
      responseDto.setCategoryDto(categoryDto);
      List<ManualHistoryDto> histories = manualHistoryService.getManualHistorySummaryDtoList(manual.getId());
      responseDto.setHistories(histories);
      responseDtos.add(responseDto);
    }
    return responseDtos;
  }

  private ManualDetailDto toManualDetailDto(Manual manual) {
    ManualDetailDto detailDto = new ManualDetailDto();
    detailDto.setManualId(manual.getId());
    detailDto.setTitle(manual.getTitle());
    detailDto.setStatus(manual.getStatus());
    CategoryResponseDto categoryDto = categoryService.toCategoryDto(manual.getCategory());
    detailDto.setCategoryDto(categoryDto);
    detailDto.setCreatedAt(manual.getCreatedAt());
    detailDto.setUpdatedAt(manual.getUpdatedAt());
    detailDto.setApprovedAt(manual.getApprovedAt());
    UserResponseDto userDto = userService.toCreatedUserDto(manual.getCreatedByUser());
    detailDto.setCreateUserDto(userDto);
    detailDto.setContent(manual.getContent());
    List<ManualDetailHistoryDto> historiesDto = manualHistoryService.getManualHistoryDetailDtoList(manual.getId());
    detailDto.setHistories(historiesDto);
    return detailDto;
  }

  // ============================
  // ロール判定
  // ============================

  private boolean canGoToNewCreatePage(User playUser) {
    log.info("start");
    if (!isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    return true;
  }

  // private boolean canArchiveManual(
  // User playUser,
  // Manual manual,
  // Category category,
  // String changeNote) {
  // log.info("start");
  // if (!isApproverOrAdmin(playUser)) {
  // throw new UnauthorizedException("権限が不足しています。");
  // }
  // if (!isActive(playUser)) {
  // throw new UnauthorizedException("有効なユーザーではありません。");
  // }
  // if (!isStatusDraft(manual) &&
  // !isStatusPending(manual) &&
  // !isStatusApproved(manual)) {
  // throw new InvalidStateException("マニュアルのステータスが条件を満たしていません。");
  // }
  // if (!isCategoryActivate(manual)) {
  // throw new UnauthorizedException(
  // "指定カテゴリはアクティブではありません。");
  // }
  // if (!isChangeNote(changeNote)) {
  // throw new InvalidStateException("更新期歴は必須です。");
  // }
  // if (!isTitleAndContent(manual)) {
  // throw new NotFoundException("タイトル・コンテンツがありません。");
  // }
  // return true;
  // }

  // // TODO: 通知実装予定
  // private boolean canApproveManual(
  // Manual manual,
  // Category category,
  // User playUser) {
  // log.info("start");
  // if (!isApproverOrAdmin(playUser)) {
  // throw new UnauthorizedException("権限が不足しています。");
  // }
  // if (!isActive(playUser)) {
  // throw new UnauthorizedException("有効なユーザーではありません。");
  // }
  // if (!isStatusPending(manual)) {
  // throw new InvalidStateException(
  // "承認ができるのはステータス:PENDINGのマニュアルのみです。");
  // }
  // if (!isOwner(manual.getCreatedByUser(), playUser)) {
  // throw new InvalidStateException(
  // "自分が作成したマニュアルの承認をすることは出来ません。");
  // }
  // if (!isActive(playUser)) {
  // throw new InvalidStateException(
  // "有効でないカテゴリーでは承認することが出来ません。");
  // }
  // return true;
  // }

  // private boolean canRollbackManual(
  // Manual manual,
  // User playUser,
  // String changeNote) {
  // log.info("start");
  // if (!isApproverOrAdmin(playUser)) {
  // throw new UnauthorizedException("権限が不足しています。");
  // }
  // if (!isActive(playUser)) {
  // throw new UnauthorizedException("有効なユーザーではありません。");
  // }
  // if (!isStatusPending(manual)) {
  // throw new InvalidStateException(
  // "差し戻しができるのはステータス:PENDINGのマニュアルのみです。");
  // }
  // if (!isOwner(manual.getCreatedByUser(), playUser)) {
  // throw new InvalidStateException(
  // "自分が作成したマニュアルを差し戻しすることは出来ません。");
  // }
  // if (!isChangeNote(changeNote)) {
  // throw new InvalidStateException("更新期歴は必須です。");
  // }
  // return true;
  // }

  // private boolean canRestoreManual(
  // Manual manual,
  // User playUser,
  // String changeNote) {
  // log.info("start");
  // if (!isApproverOrAdmin(playUser)) {
  // throw new UnauthorizedException("権限が不足しています。");
  // }
  // if (!isActive(playUser)) {
  // throw new UnauthorizedException("有効なユーザーではありません。");
  // }
  // if (!isStatusArchived(manual)) {
  // throw new InvalidStateException(
  // "復帰ができるのはステータス:ARCHIVEDのマニュアルのみです。");
  // }
  // if (!isActive(playUser)) {
  // throw new InvalidStateException(
  // "使用中カテゴリでのみ復帰が出来ます。");
  // }
  // if (!isChangeNote(changeNote)) {
  // throw new InvalidStateException("更新履歴は必須です。");
  // }
  // return true;
  // }

  private boolean canSaveDraftForCreate(
      User playUser,
      Manual manual) {
    log.info("start");
    Category category = categoryService.getCategoryById(manual.getCategory().getId());

    if (!isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (!isCategoryActivate(category)) {
      throw new InvalidStateException(
          "使用中カテゴリでのみ復帰が出来ます。");
    }
    return true;
  }

  private boolean canSubmitToPending(
      User playUser,
      Manual manual) {
    log.info("start");
    Category category = categoryService.getCategoryById(manual.getCategory().getId());
    if (!isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (!isCategoryActivate(category)) {
      throw new InvalidStateException(
          "使用中カテゴリでのみ復帰が出来ます。");
    }
    return true;
  }

  private boolean canGoToEditPage(
      User playUser,
      Manual manual) {
    log.info("start");
    Category category = categoryService.getCategoryById(manual.getCategory().getId());
    if (!isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (!isCategoryActivate(category)) {
      throw new InvalidStateException(
          "使用中カテゴリでのみ復帰が出来ます。");
    }
    if (!isStatusPending(manual) && !isStatusDraft(manual)) {
      throw new InvalidStateException(
          "編集ができるのはステータス:DRAFT/PENDINGのマニュアルのみです。");
    }
    if (!isOwner(manual.getCreatedByUser(), playUser)) {
      throw new InvalidStateException(
          "編集ができるのは自分が作成したマニュアルだけです。");
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

  private boolean canEditToPending(
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

  private boolean canGoToDetailPage(User playUser) {
    log.info("start");
    // アクティブユーザー
    if (!isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    return true;
  }

  private boolean canGoToCopyPage(User playUser) {
    log.info("start");
    // アクティブユーザー
    if (!isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    return true;
  }

  private boolean canFindManualsBySearch(User playUser) {
    log.info("start");
    // アクティブユーザー
    if (!isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    return true;
  }

  // manualDetailフラグをセットして返す
  private ManualDetailDto buildDetailPermissions(Manual manual, User playUser, ManualDetailDto detailDto) {
    detailDto.setCanApprove(canApproveManual(manual, playUser));
    detailDto.setCanArchive(canArchiveManual(manual, playUser));
    detailDto.setCanCopy(canCopyManual(manual));
    detailDto.setCanEdit(canEditManual(manual, playUser));
    detailDto.setCanPending(canPendingManual(manual, playUser));
    detailDto.setCanRestore(canRestoreManual(playUser, manual));
    detailDto.setCanRollback(canRollbackManual(manual, playUser));

    return detailDto;
  }

  // ============================
  // ボタン表示非表示判定
  // ============================

  private boolean canRestoreManual(User playUser, Manual manual) {
    if (!isApproverOrAdmin(playUser)) {
      return false;
    }
    if (!isStatusArchived(manual)) {
      return false;
    }
    return true;
  }

  private boolean canCopyManual(Manual manual) {
    if (!manual.getCategory().isActive()) {
      return false;
    }
    if (!isStatusPending(manual) &&
        !isStatusApproved(manual) &&
        !isStatusArchived(manual)) {
      return false;
    }
    return true;
  }

  private boolean canApproveManual(Manual manual, User playUser) {
    if (!isApproverOrAdmin(playUser)) {
      return false;
    }
    // 作成者じゃない時のみOK
    if (isOwner(manual.getCreatedByUser(), playUser)) {
      return false;
    }
    if (!isStatusPending(manual)) {
      return false;
    }
    return true;
  }

  private boolean canRollbackManual(Manual manual, User playUser) {
    if (!isApproverOrAdmin(playUser)) {
      return false;
    }
    if (isOwner(manual.getCreatedByUser(), playUser)) {
      return false;
    }
    if (!isStatusPending(manual)) {
      return false;
    }
    return true;
  }

  private boolean PublishDtaftManual(Manual manual, User playUser) {
    if (!isStatusDraft(manual)) {
      return false;
    }
    if (!isOwner(manual.getCreatedByUser(), playUser)) {
      return false;
    }
    return true;
  }

  private boolean canEditManual(Manual manual, User playUser) {
    if (!isOwner(manual.getCreatedByUser(), playUser)) {
      return false;
    }
    if (!isStatusDraft(manual) && !isStatusPending(manual)) {
      return false;
    }
    return true;
  }

  private boolean canArchiveManual(Manual manual, User playUser) {
    if (!isApproverOrAdmin(playUser)) {
      return false;
    }
    if (!isStatusArchived(manual) && !isStatusDraft(manual)) {
      return false;
    }
    if (!isTitleAndContent(manual)) {
      return false;
    }
    return true;
  }

  private boolean canPendingManual(Manual manual, User playUser) {
    if (!isOwner(manual.getCreatedByUser(), playUser)) {
      return false;
    }
    if (!isStatusDraft(manual)) {
      return false;
    }
    if (!isTitleAndContent(manual)) {
      return false;
    }
    return true;
  }

  // ============================
  // 権限共通処理
  // ============================

  private boolean isActive(User playUser) {
    if (playUser.isActive()) {
      return true;
    }
    return false;
  }

  private boolean isUserActive(User playUser) {
    if (playUser.isActive() == true) {
      return true;
    }
    return false;
  }

  private boolean isOwner(User createdUser, User playUser) {
    if (Objects.equals(createdUser.getId(), playUser.getId())) {
      return true;
    }
    return false;
  }

  private boolean isApproverOrAdmin(User playUser) {
    if (playUser.getRole() == UserRole.ADMIN ||
        playUser.getRole() == UserRole.APPROVER) {
      return true;
    }
    return false;
  }

  private boolean isStatusDraft(Manual manual) {
    if (manual.getStatus() == ManualStatus.DRAFT) {
      return true;
    }
    return false;
  }

  private boolean isStatusPending(Manual manual) {
    if (manual.getStatus() == ManualStatus.PENDING) {
      return true;
    }
    return false;
  }

  private boolean isStatusApproved(Manual manual) {
    if (manual.getStatus() == ManualStatus.APPROVED) {
      return true;
    }
    return false;
  }

  private boolean isStatusArchived(Manual manual) {
    if (manual.getStatus() == ManualStatus.ARCHIVED) {
      return true;
    }
    return false;
  }

  private boolean isCategoryActivate(Category category) {
    if (category.isActive()) {
      return true;
    }
    return false;
  }

  private boolean isChangeNote(String changeNote) {
    if (changeNote == null || changeNote.isBlank()) {
      return false;
    }
    return true;
  }

  private boolean isTitleAndContent(Manual manual) {
    if (manual.getTitle() == null ||
        manual.getContent() == null ||
        manual.getTitle().isBlank() ||
        manual.getContent().isBlank()) {
      return false;
    }
    return true;
  }

  // ============================
  // 共通処理
  // ============================

  private Manual findManualOrThrow(Long id) {
    log.info("start");
    Optional<Manual> manualOpt = manualRepository.findById(id);
    if (manualOpt.isEmpty()) {
      throw new RuntimeException("");
    }
    return manualOpt.get();
  }

  // index表示に必要なコモンデータ（一覧表示以外）を集める。
  private ManualIndexDto buildIndexCommonData(Principal principal) {
    log.info("start");

    // カテゴリーリスト 検索欄用 active inactive
    List<CategoryResponseDto> activeCategoriesDto = categoryService.getActiveCategoryDtos();
    List<CategoryResponseDto> inactiveCategoriesDto = categoryService.getInactiveCategoryDtos();
    // ステータスリスト 検索欄用
    List<ManualResponseDto> defaultStatuses = getDefaultStatuses();
    // クイックビュー 通知
    IndexSummaryDto summaryDto = getIndexSummary(principal);
    // ログインユーザー情報 DisplayName
    UserResponseDto userDto = userService.toUserIndexViewDto(principal);

    ManualIndexDto listDto = new ManualIndexDto();
    listDto.setActiveCategories(activeCategoriesDto);
    listDto.setInactiveCategories(inactiveCategoriesDto);
    listDto.setDefaultStatuses(defaultStatuses);
    listDto.setSummaryDto(summaryDto);
    listDto.setUserDto(userDto);

    return listDto;
  }

  // statusを参照してstatusLabelを返す
  private String getStatusLabel(ManualStatus status) {
    switch (status) {
      case DRAFT:
        return "下書き";
      case PENDING:
        return "申請中";
      case APPROVED:
        return "承認済";
      case ARCHIVED:
        return "アーカイブ";
      default:
        throw new AssertionError("不明なステータスです。");
    }
  }

}
