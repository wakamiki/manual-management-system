package com.example.manual.service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import com.example.manual.dto.ManualEditFormDto;
import com.example.manual.dto.ManualHistoryDto;
import com.example.manual.dto.ManualIndexDto;
import com.example.manual.dto.ManualResponseDto;
import com.example.manual.dto.ManualSearchConditionDto;
import com.example.manual.dto.UserResponseDto;
import com.example.manual.entity.Category;
import com.example.manual.entity.Manual;
import com.example.manual.entity.User;
import com.example.manual.enums.ManualStatus;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.exception.UnauthorizedException;
import com.example.manual.repository.ManualRepository;
import com.example.manual.repository.ManualSpecification;

@Service
public class ManualQueryService {
  private static final Logger log = LoggerFactory.getLogger(ManualQueryService.class);

  private final ManualRepository manualRepository;
  private final ManualHistoryService historyService;
  private final UserService userService;
  private final CategoryService categoryService;
  private final NotificationService notificationService;
  private final ManualCommandService command;

  public ManualQueryService(
      ManualRepository manualRepository,
      ManualHistoryService manualHistoryService,
      UserService userService,
      CategoryService categoryService,
      NotificationService notificationService,
      ManualCommandService manualCommandService) {

    this.manualRepository = manualRepository;
    this.historyService = manualHistoryService;
    this.userService = userService;
    this.categoryService = categoryService;
    this.notificationService = notificationService;
    this.command = manualCommandService;
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
  public ManualEditFormDto goToEditPage(
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
  public ManualEditFormDto goToCopyPage(
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
  // ============================

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

  private ManualEditFormDto toManualFormInputDto(Manual manual) {
    log.info("start");
    ManualEditFormDto formDto = new ManualEditFormDto();
    formDto.setManualId(manual.getId());
    CategoryResponseDto categoryDto = categoryService.toCategoryDto(manual.getCategory());
    formDto.setCategoryName((categoryDto.getCategoryName()));
    formDto.setCategoryId(categoryDto.getId());
    formDto.setContent(manual.getContent());
    formDto.setTitle(manual.getTitle());
    return formDto;
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
      List<ManualHistoryDto> histories = historyService.getManualHistorySummaryDtoList(manual.getId());
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
    List<ManualDetailHistoryDto> historiesDto = historyService.getManualHistoryDetailDtoList(manual.getId());
    detailDto.setHistories(historiesDto);
    detailDto.setStatusLabel(getStatusLabel(manual.getStatus()));
    return detailDto;
  }

  // ============================
  // 共通処理
  // ============================

  public Manual findManualOrThrow(Long id) {
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

  // manualDetailフラグをセットして返す
  private ManualDetailDto buildDetailPermissions(Manual manual, User playUser, ManualDetailDto detailDto) {
    detailDto.setCanApprove(canApprove(manual, playUser));
    detailDto.setCanArchive(canArchive(manual, playUser));
    detailDto.setCanCopy(canCopy(manual));
    detailDto.setCanEdit(canEditManual(manual, playUser));
    detailDto.setCanPending(canPending(manual, playUser));
    detailDto.setCanRestore(canRestore(playUser, manual));
    detailDto.setCanRollback(canRollback(manual, playUser));
    detailDto.setCanGuest(command.isGuest(playUser));

    return detailDto;
  }

  // ============================
  // ボタン表示非表示判定
  // ============================

  private boolean canRestore(User playUser, Manual manual) {
    if (!command.isApproverOrAdmin(playUser)) {
      return false;
    }
    if (!command.isStatusArchived(manual)) {
      return false;
    }
    return true;
  }

  private boolean canCopy(Manual manual) {
    if (!manual.getCategory().isActive()) {
      return false;
    }
    if (!command.isStatusPending(manual) &&
        !command.isStatusApproved(manual) &&
        !command.isStatusArchived(manual)) {
      return false;
    }
    return true;
  }

  private boolean canApprove(Manual manual, User playUser) {
    if (!command.isApproverOrAdmin(playUser)) {
      return false;
    }
    // 作成者じゃない時のみOK
    if (command.isOwner(manual.getCreatedByUser(), playUser)) {
      return false;
    }
    if (!command.isStatusPending(manual)) {
      return false;
    }
    return true;
  }

  private boolean canRollback(Manual manual, User playUser) {
    if (!command.isApproverOrAdmin(playUser)) {
      return false;
    }
    if (command.isOwner(manual.getCreatedByUser(), playUser)) {
      return false;
    }
    if (!command.isStatusPending(manual)) {
      return false;
    }
    return true;
  }

  private boolean PublishDtaftManual(Manual manual, User playUser) {
    if (!command.isStatusDraft(manual)) {
      return false;
    }
    if (!command.isOwner(manual.getCreatedByUser(), playUser)) {
      return false;
    }
    return true;
  }

  private boolean canEditManual(Manual manual, User playUser) {
    if (!command.isOwner(manual.getCreatedByUser(), playUser)) {
      return false;
    }
    if (!command.isStatusDraft(manual) &&
        !command.isStatusPending(manual)) {
      return false;
    }
    return true;
  }

  private boolean canArchive(Manual manual, User playUser) {
    if (!command.isApproverOrAdmin(playUser)) {
      return false;
    }
    if (!command.isStatusArchived(manual) &&
        !command.isStatusDraft(manual)) {
      return false;
    }
    if (!command.isTitleAndContent(manual.getTitle(), manual.getContent())) {
      return false;
    }
    return true;
  }

  private boolean canPending(Manual manual, User playUser) {
    if (!command.isOwner(manual.getCreatedByUser(), playUser)) {
      return false;
    }
    if (!command.isStatusDraft(manual)) {
      return false;
    }
    if (!command.isTitleAndContent(manual.getTitle(), manual.getContent())) {
      return false;
    }
    return true;
  }

  // ============================
  // 権限判定
  // ============================

  private boolean canGoToDetailPage(User playUser) {
    log.info("start");
    // アクティブユーザー
    if (!command.isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    return true;
  }

  private boolean canGoToCopyPage(User playUser) {
    log.info("start");
    // アクティブユーザー
    if (!command.isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    return true;
  }

  private boolean canFindManualsBySearch(User playUser) {
    log.info("start");
    // アクティブユーザー
    if (!command.isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    return true;
  }

  private boolean canGoToEditPage(
      User playUser,
      Manual manual) {
    log.info("start");
    Category category = categoryService.getCategoryById(manual.getCategory().getId());
    if (!command.isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (!command.isCategoryActivate(category)) {
      throw new InvalidStateException(
          "使用中カテゴリでのみ復帰が出来ます。");
    }
    if (!command.isStatusPending(manual) &&
        !command.isStatusDraft(manual)) {
      throw new InvalidStateException(
          "編集ができるのはステータス:DRAFT/PENDINGのマニュアルのみです。");
    }
    if (!command.isOwner(manual.getCreatedByUser(), playUser)) {
      throw new InvalidStateException(
          "編集ができるのは自分が作成したマニュアルだけです。");
    }
    return true;
  }

  private boolean canGoToNewCreatePage(User playUser) {
    log.info("start");
    if (!command.isActive(playUser)) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    return true;
  }

}
