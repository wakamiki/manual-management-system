package com.example.manual.service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.example.manual.dto.PagingDto;
import com.example.manual.dto.UserResponseDto;
import com.example.manual.entity.Manual;
import com.example.manual.entity.User;
import com.example.manual.enums.FormMode;
import com.example.manual.enums.ManualStatus;
import com.example.manual.enums.UserRole;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.exception.UnauthorizedException;
import com.example.manual.repository.ManualRepository;
import com.example.manual.repository.ManualSpecification;

@Service
public class ManualQueryService {
  private static final Logger log = LoggerFactory.getLogger(ManualQueryService.class);

  private final ManualRepository manualRepository;
  private final UserService userService;
  private final CategoryService categoryService;
  private final ManualPermissionService permission;
  private final ManualHistoryService historyService;
  private final NotificationService notificationService;

  public ManualQueryService(
      ManualRepository manualRepository,
      UserService userService,
      CategoryService categoryService,
      ManualPermissionService permission,
      ManualHistoryService historyService,
      NotificationService notificationService) {

    this.manualRepository = manualRepository;
    this.userService = userService;
    this.categoryService = categoryService;
    this.permission = permission;
    this.historyService = historyService;
    this.notificationService = notificationService;
  }

  // ============================================
  // 画面表示
  // ============================================

  // index表示
  public ManualIndexDto showIndex(
      Principal principal,
      Page<Manual> manuals,
      ManualSearchConditionDto condition) {
    log.info("start");
    // 標準表示項目取得
    ManualIndexDto listDto = buildIndexCommonData(principal);
    // 一覧リスト ページ毎に分ける必要有
    PagingDto pagingDto = PagingDto.from(manuals);
    List<ManualResponseDto> ManualDtos = buildIndexWithManualsPage(manuals, userService.getUserByPrincipal(principal));

    listDto.setManuals(ManualDtos);
    listDto.setPagingDto(pagingDto);
    listDto.setCondition(condition);

    return listDto;
  }

  // ============================================
  // 新規タブ画面遷移
  // ============================================

  // 新規作成(新規タブ)
  public List<CategoryResponseDto> goToNewCreatePage(Principal principal) {
    log.info("start");
    User playUser = userService.getUserByPrincipal(principal);
    if (!permission.canGoToNewCreatePage(playUser)) {
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

    if (!permission.canGoToEditPage(
        playUser, manual)) {
      throw new UnauthorizedException("判定エラー");
    }
    FormMode mode = FormMode.edit;
    return toManualFormInputDto(manual, playUser, mode);
  }

  // 複製（新規タブ）
  public ManualEditFormDto goToCopyPage(
      Long manualId,
      Principal principal) {
    log.info("start");
    Manual manual = findManualOrThrow(manualId);
    User playUser = userService.getUserByPrincipal(principal);
    if (!permission.canGoToCopyPage(playUser)) {
      throw new UnauthorizedException("判定エラー");
    }
    FormMode mode = FormMode.copy;
    return toManualFormInputDto(manual, playUser, mode);
  }

  // マニュアル詳細画面（新規タブ）
  public ManualDetailDto goToDetailPage(
      Long manualId,
      Principal principal) {
    log.info("start");
    Manual manual = findManualOrThrow(manualId);
    User playUser = userService.getUserByPrincipal(principal);
    if (!permission.canGoToDetailPage(playUser)) {
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
  public Page<Manual> findManualsBySearch(
      ManualSearchConditionDto condition,
      Principal principal,
      Pageable pageable) {
    log.info("start");
    User targetUser = userService.getUserByPrincipal(principal);
    if (!permission.canFindManualsBySearch(targetUser)) {
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
    pageable = PageRequest.of(Math.max(pageable.getPageNumber(), 0), 10,
        Sort.by(Sort.Direction.DESC, "updatedAt"));
    Page<Manual> manualList = manualRepository.findAll(
        specification, pageable);

    return manualList;
  }

  // status一覧を返す
  public List<ManualStatus> getManualStatuses() {
    log.info("start");
    List<ManualStatus> responseStatus = Arrays.asList(ManualStatus.values());

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
  public Page<Manual> findMyCreatedManualsPage(Principal principal, Pageable pageable) {
    log.info("start");
    User targetUser = userService.getUserByPrincipal(principal);
    Page<Manual> manualList = manualRepository.findByCreatedByUserOrderByCreatedAtDesc(
        targetUser, pageable);
    return manualList;
  }

  public List<Manual> findMyCreatedManuals(Principal principal) {
    log.info("start");
    User targetUser = userService.getUserByPrincipal(principal);
    List<Manual> manualList = manualRepository.findByCreatedByUserOrderByCreatedAtDesc(
        targetUser);
    return manualList;
  }

  // index 自分作成PENDINGのデータ一覧を返す。
  public Page<Manual> findMyPendingManuals(Principal principal, Pageable pageable) {
    log.info("start");
    User targetUser = userService.getUserByPrincipal(principal);
    Page<Manual> manualList = manualRepository.findByCreatedByUserAndStatusOrderByUpdatedAtDesc(
        targetUser, ManualStatus.PENDING, pageable);
    return manualList;
  }

  // index 最近７日間の更新のデータ一覧を返す。
  public Page<Manual> findRecentlyUpdatedManuals(Pageable pageable) {
    log.info("start");
    Page<Manual> manualList = manualRepository.findByUpdatedAtAfterOrderByUpdatedAtDesc(
        LocalDateTime.now().minusDays(7), pageable);
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

  // ============================
  // Dto詰替
  // ============================

  public ManualEditFormDto toManualFormInputDto(Manual manual, User playUser,
      FormMode mode) {
    log.info("start");
    ManualEditFormDto formDto = new ManualEditFormDto();
    if (playUser.getRole() == UserRole.GUEST) {
      formDto.setGuest(true);
    }
    formDto.setManualId(manual.getId());
    CategoryResponseDto categoryDto = categoryService.toCategoryDto(manual.getCategory());
    formDto.setCategoryName((categoryDto.getCategoryName()));
    formDto.setCategoryId(categoryDto.getId());
    formDto.setContent(manual.getContent());
    formDto.setTitle(manual.getTitle());
    formDto.setMode(mode);
    if (mode == FormMode.copy) {
      formDto.setModeLabel("複製");
    } else if (mode == FormMode.edit) {
      formDto.setModeLabel("編集");
    }
    return formDto;
  }

  public List<ManualResponseDto> buildIndexWithManualsPage(Page<Manual> manualList, User playUser) {
    List<ManualResponseDto> responseDtos = new ArrayList<>();
    for (Manual manual : manualList) {
      ManualResponseDto responseDto = new ManualResponseDto();
      if (permission.canEditManual(manual, playUser)) {
        responseDto.setCanEdit(true);
      }
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

  public List<ManualResponseDto> buildIndexWithManualsPage(List<Manual> manualList, User playUser) {
    List<ManualResponseDto> responseDtos = new ArrayList<>();
    for (Manual manual : manualList) {
      ManualResponseDto responseDto = new ManualResponseDto();
      if (permission.canEditManual(manual, playUser)) {
        responseDto.setCanEdit(true);
      }
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

  public ManualDetailDto toManualDetailDto(Manual manual) {
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

  // manualDetailフラグをセットして返す
  public ManualDetailDto buildDetailPermissions(Manual manual, User playUser, ManualDetailDto detailDto) {
    detailDto.setCanApprove(permission.canApprove(manual, playUser));
    detailDto.setCanArchive(permission.canArchive(manual, playUser));
    detailDto.setCanCopy(permission.canCopy(manual));
    detailDto.setCanEdit(permission.canEditManual(manual, playUser));
    detailDto.setCanPending(permission.canPending(manual, playUser));
    detailDto.setCanRestore(permission.canRestore(playUser, manual));
    detailDto.setCanRollback(permission.canRollback(manual, playUser));
    detailDto.setCanGuest(permission.isGuest(playUser));

    return detailDto;
  }

  // index表示に必要なコモンデータ（一覧表示以外）を集める。
  public ManualIndexDto buildIndexCommonData(Principal principal) {
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
  public String getStatusLabel(ManualStatus status) {
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

}
