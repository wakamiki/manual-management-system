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
import com.example.manual.dto.ManualActionRequestDto;
import com.example.manual.dto.ManualDetailDto;
import com.example.manual.dto.ManualHistoryDto;
import com.example.manual.dto.ManualIndexDto;
import com.example.manual.dto.ManualRequestDto;
import com.example.manual.dto.ManualResponseDto;
import com.example.manual.dto.ManualSearchConditionDto;
import com.example.manual.dto.UserResponseDto;
import com.example.manual.entity.Category;
import com.example.manual.entity.Manual;
import com.example.manual.entity.ManualHistory;
import com.example.manual.entity.User;
import com.example.manual.enums.ManualStatus;
import com.example.manual.enums.UserRole;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.exception.NotFoundException;
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



  //index表示
  public ManualIndexDto showIndex(
      Principal principal,
      ManualSearchConditionDto condition
  ) {
    log.info("start");
    //検索一覧リスト condition ページ毎に分ける必要有
    List<ManualResponseDto> searchManuals =
        searchManuals(condition,principal);
    //カテゴリーリスト 検索欄用 active inactive
    List<CategoryResponseDto> activeCategoriesDto =
        categoryService.getActiveCategoryDtos();
    List<CategoryResponseDto> inactiveCategoriesDto =
        categoryService.getInactiveCategoryDtos();
    //ステータスリスト　検索欄用
    List<ManualStatus> defaultStatuses =
        getDefaultStatuses();
    //クイックビュー　通知
    IndexSummaryDto summaryDto=
        getIndexSummary(principal);
    //ログインユーザー情報　DisplayName
    UserResponseDto userDto = userService.toUserIndexViewDto(principal);

    ManualIndexDto listDto = new ManualIndexDto();
    listDto.setSearchManuals(searchManuals);
    listDto.setActiveCategories(activeCategoriesDto);
    listDto.setInactiveCategories(inactiveCategoriesDto);
    listDto.setDefaultStatuses(defaultStatuses);
    listDto.setSummaryDto(summaryDto);
    listDto.setUserDto(userDto);

      return listDto;
  }

  // 対応ボタン 新規作成(新規タブ)
  public void goToNewCreatePage(Principal principal) {
    log.info("start");
    if(!canGoToNewCreatePage(principal)){
      throw new InvalidStateException("判定エラー");
    }
  }

  // 対応ボタン 新規作成
  public void submitManual(Long manualId) {
    log.info("start");
    Manual manual = findManualOrThrow(manualId);
    manual.markUpdatedNow();
    manual.submitPENDING();
    manual.markUnreadRolledback();
    manualRepository.save(manual);
  }

  // 対応ボタン 新規作成保存（DRAFT）
  public void saveDraftForCreate(
      Long manualId,
      ManualRequestDto requestDto,
      Principal principal) {
    log.info("start");
    Manual manual = findManualOrThrow(manualId);
    Category category = categoryService.getCategoryById(requestDto.getCategoryId());

    User user = userService.getUserByPrincipal(principal);
    if (!canSaveDraftForCreate(principal, category)) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.setTitle(requestDto.getTitle());
    manual.setContent(requestDto.getContent());
    manual.markUpdatedNow();
    manual.markCreatedNow();
    manual.markStatusDRAFT();
    manual.setCategory(category);
    manual.setCreatedByUser(user);
    manual.markUnreadRolledback();
    manualRepository.save(manual);
  }

  // 対応ボタン 新規作成マニュアル公開(PENDING)
  public void submitToPending(
      Long manualId,
      ManualRequestDto requestDto,
      Principal principal) {
    log.info("start");
    Manual manual = findManualOrThrow(manualId);
    Category category = categoryService.getCategoryById(requestDto.getCategoryId());

    User user = userService.getUserByPrincipal(principal);
    if (!canSubmitToPending(principal, category)) {
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
    notificationService.createSubmitNotifications(principal,savedManual);
  }

  // 対応ボタン 編集（新規タブ）
  public ManualResponseDto goToEditPage(
      Long manualId,
      Principal principal) {
    log.info("start");
    Manual manual = findManualOrThrow(manualId);

    if (!canGoToEditPage(
        principal,
        manual.getCategory(),
        manual.getStatus())) {
      throw new UnauthorizedException("判定エラー");
    }
    return toManualFormInputDto(manual);
  }

  // 対応ボタン 複製（新規タブ）
  public ManualResponseDto goToCopyPage(
      Long manualId,
      Principal principal) {
    log.info("start");
    Manual manual = findManualOrThrow(manualId);
    if (!canGoToCopyPage(principal)) {
      throw new UnauthorizedException("判定エラー");
    }
    return toManualFormInputDto(manual);
  }

  // 対応ボタン 複製編集保存(DRAFT)
  public void saveDraftForCopy(
      Long manualId,
      ManualRequestDto requestDto,
      Principal principal) {
    log.info("start");
    Manual manual = findManualOrThrow(manualId);
    Category category = categoryService.getCategoryById(requestDto.getCategoryId());

    User user = userService.getUserByPrincipal(principal);
    if (!canSaveDraftForCopy(
        principal,
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
    manual.setCreatedByUser(user);
    manual.markUnreadRolledback();
    Manual savedManual = manualRepository.save(manual);
    manualHistoryService.createHistory(
        savedManual,
        requestDto.getChangeNote(),
        principal);
  }

  // 対応ボタン 編集マニュアル公開(PENDING)
  public void editToPending(
      Long manualId,
      ManualRequestDto requestDto,
      Principal principal) {
    log.info("start");
    Manual manual = findManualOrThrow(manualId);
    Category category = categoryService.getCategoryById(requestDto.getCategoryId());

    User user = userService.getUserByPrincipal(principal);
    if (!canEditToPending(principal, category, manual)) {
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
    notificationService.deleteRollbackNotification(manualId,user);
  }

  // 対応ボタン マニュアル詳細画面（新規タブ）
  public ManualDetailDto goToDetailPage(
      Long manualId,
      Principal principal) {
    log.info("start");
    Manual manual = findManualOrThrow(manualId);
    List<ManualHistory> history = manualHistoryService.getManualIdHistory(manualId);

    ManualDetailDto detailDto = new ManualDetailDto();
    if (!canGoToDetailPage(principal)) {
      throw new UnauthorizedException("判定エラー");
    }
    detailDto.setManualId(manual.getId());
    detailDto.setCategoryName(manual.getCategory().getCategoryName());
    detailDto.setTitle(manual.getTitle());
    detailDto.setContent(manual.getContent());
    detailDto.setCreatedByName(manual.getCreatedByUser().getDisplayName());
    detailDto.setStatus(manual.getStatus());
    detailDto.setCreatedAt(manual.getCreatedAt());
    detailDto.setUpdatedAt(manual.getUpdatedAt());
    List<ManualHistoryDto> historiesDto =
      manualHistoryService.toHistoriesDto(history);
    detailDto.setHistories(historiesDto);
    return detailDto;
  }

  // TODO: 通知実装予定
  // 対応ボタン 承認（チェンジノート無）
  public void approveManual(
      Long manualId,
      Principal principal) {
    log.info("start");
        Manual manual = findManualOrThrow(manualId);
    if (!canApproveManual(
        manual,
        manual.getCategory(),
        principal)) {
      throw new UnauthorizedException("判定エラー");
    }

    manual.approve();
    manual.markUpdatedNow();
    manual.markApprovedNow();
    manualRepository.save(manual);
    notificationService.deletePendingApprovalNotificationsByManualId(manualId);
  }

  // 対応ボタン 承認（チェンジノート有）
  public void approveManualWithComment(
      Long manualId,
      String changeNote,
      Principal principal) {
    log.info("start");
    Manual manual = findManualOrThrow(manualId);
    if (!canApproveManual(manual, manual.getCategory(), principal)) {
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

  // 対応ボタン 差し戻し
  public void rollbackEditManual(
      Long manualId,
      ManualActionRequestDto requestDto,
      Principal principal) {
    log.info("start");
    Manual manual = findManualOrThrow(manualId);
    if (!canRollbackManual(manual, principal, requestDto.getChangeNote())) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.markReadRolledback();
    manual.markUpdatedNow();
    manual.rollbackToDraft();
    Manual savedManual = manualRepository.save(manual);
    manualHistoryService.createHistory(
        savedManual, requestDto.getChangeNote(), principal);
  }

  // 対応ボタン アーカイブ
  public void archiveManual(
      Long manualId,
      ManualActionRequestDto actionRequestDto,
      Principal principal) {
    log.info("start");
    Manual manual = findManualOrThrow(manualId);
    User user = userService.getUserByPrincipal(principal);
    Category category = categoryService.getCategoryById(manual.getCategory().getId());

    if (!canArchiveManual(
        user, manual, category, actionRequestDto.getChangeNote())) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.archive();
    manual.markUpdatedNow();
    Manual savedManual = manualRepository.save(manual);
    manualHistoryService.createHistory(
        savedManual, actionRequestDto.getChangeNote(), principal);
  }

  // 対応ボタン 復帰
  public void restoreManual(
      Long manualId,
      ManualActionRequestDto requestDto,
      Principal principal) {
    log.info("start");
    Manual manual = findManualOrThrow(manualId);
    if (!canRestoreManual(manual, principal, requestDto.getChangeNote())) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.restoreToApproved();
    manual.markUpdatedNow();
    Manual savedManual = manualRepository.save(manual);
    manualHistoryService.createHistory(
        savedManual, requestDto.getChangeNote(), principal);
  }

  //============================
  // 取得・検索
  // ===========================

  // index 検索表示 取得
  public List<ManualResponseDto> searchManuals(
      ManualSearchConditionDto condition,
      Principal principal) {
    log.info("start");
    if (!canSearchManuals(principal)) {
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

    List<ManualResponseDto> manualDtoList = new ArrayList<>();
    for (Manual manual : manualList) {
      ManualResponseDto manualDto = new ManualResponseDto();
      CategoryResponseDto categoryDto = 
          categoryService.toCategoryDto(manual.getCategory());
      manualDto.setCategoryDto(categoryDto);
      UserResponseDto userDto =
          userService.toCreatedUserDto(manual.getCreatedByUser());
      manualDto.setCreatedUserDto(userDto);
      manualDto.setTitle(manual.getTitle());
      manualDto.setContent(manual.getContent());
      manualDto.setManualId(manual.getId());
      manualDto.setStatus(manual.getStatus());
      manualDto.setUpdatedAt(manual.getUpdatedAt());
      manualDto.setHistories(
          manualHistoryService.getManualHistorySummaryDtoList(
              manual.getId()));
      manualDtoList.add(manualDto);
    }
    return manualDtoList;
  }

  // index quickView表示取得
  public IndexSummaryDto getIndexSummary(Principal principal) {
    log.info("start");
    //通知欄２つの数字を取得
    int unreadRollbackCount = notificationService.unreadRollbackCount(principal);
    int unreadPendingCount = notificationService.unreadPendingCount(principal);
    //count　自分の作成マニュアル
    int countUserCreatedManual = countUserCreatedManual(principal);
    //count　申請中
    int countCreatedPendingManual = countCreatedPendingManual(principal);
    //count　最近更新（７日間）
    int countRecentWeeklyManual = countRecentWeeklyManuals();

    IndexSummaryDto summaryDto = new IndexSummaryDto();
    summaryDto.setCountUserCreatedManual(countUserCreatedManual);
    summaryDto.setCountCreatedPendingManual(countCreatedPendingManual);
    summaryDto.setCountRecentWeeklyManual(countRecentWeeklyManual);
    summaryDto.setUnreadRollbackCount(unreadRollbackCount);
    summaryDto.setUnreadPendingCount(unreadPendingCount);

    return summaryDto;
  }

  //status一覧を返す
  public List<ManualStatus> getManualStatuses() {
    log.info("start");
    List<ManualStatus> responseStatus = Arrays.asList(ManualStatus.values());

    return responseStatus;
  }
  //status一覧(Draft以外)を返す
  public List<ManualStatus> getDefaultStatuses() {
    log.info("start");
    List<ManualStatus> responseStatus = new ArrayList<>();
    ManualStatus status[] = ManualStatus.values();
    for (ManualStatus manualStatus : status) {
      if (manualStatus==ManualStatus.DRAFT) {
        continue;
      }
      responseStatus.add(manualStatus);
    }
    return responseStatus;
  }

  //myPage 自分作成のデータを渡す
  public List<ManualResponseDto> userCreatedManualList(User user) {
    List<Manual> userCreatedManualList = manualRepository.findByCreatedByUserOrderByCreatedAtDesc(user);
    List<ManualResponseDto> userCreatedListDto = new ArrayList<>();
    for (Manual manual : userCreatedManualList) {
      ManualResponseDto manualDto = new ManualResponseDto();
      manualDto.setManualId(manual.getId());
      manualDto.setTitle(manual.getTitle());
      manualDto.setStatus(manual.getStatus());
      CategoryResponseDto categoryDto =
        categoryService.toCategoryDto(manual.getCategory());
      manualDto.setCategoryDto(categoryDto);
      manualDto.setUpdatedAt(manual.getUpdatedAt());
      UserResponseDto userDto =
        userService.toCreatedUserDto(manual.getCreatedByUser());
      manualDto.setCreatedUserDto(userDto);
      manualDto.setCreatedAt(manual.getCreatedAt());
      List<ManualHistory> histories =
        manualHistoryService.getManualIdHistory(manual.getId());
      List<ManualHistoryDto> historiesDto =
       manualHistoryService.toHistoriesDto(histories);
      manualDto.setHistories(historiesDto);
      userCreatedListDto.add(manualDto);
    }
    return userCreatedListDto;
  }

  //MyPege 差し戻し（自分作成）のデータを渡す。
  public List<ManualResponseDto> createdRollbackManualList(User user) {
    log.info("start");
    List<Manual> rollbackList = manualRepository.findByIsRolledbackTrueAndCreatedByUserOrderByUpdatedAtDesc(
        user);
    List<ManualResponseDto> rollbacklistDto = new ArrayList<>();
    for (Manual manual : rollbackList) {
      ManualResponseDto manualDto = new ManualResponseDto();
      manualDto.setManualId(manual.getId());
      manualDto.setTitle(manual.getTitle());
      manualDto.setStatus(manual.getStatus());
      CategoryResponseDto categoryDto =
        categoryService.toCategoryDto(manual.getCategory());
      manualDto.setCategoryDto(categoryDto);
      manualDto.setUpdatedAt(manual.getUpdatedAt());
      UserResponseDto userResponseDto =
        userService.toCreatedUserDto(manual.getCreatedByUser());
      manualDto.setCreatedUserDto(userResponseDto);
      rollbacklistDto.add(manualDto);
    }
    return rollbacklistDto;
  }

  //MyPege PENDINGの全マニュアル（自分作成以外）のデータを渡す。
  public List<ManualResponseDto> pendingManualList(User user) {
    log.info("start");
    List<Manual> pendingManualList = manualRepository.findByCreatedByUserNotAndStatusOrderByUpdatedAtDesc(
        user, ManualStatus.PENDING);
    List<ManualResponseDto> pendingListDto = new ArrayList<>();
    for (Manual manual : pendingManualList) {
      ManualResponseDto manualDto = new ManualResponseDto();
      manualDto.setManualId(manual.getId());
      manualDto.setTitle(manual.getTitle());
      manualDto.setStatus(manual.getStatus());
      CategoryResponseDto categoryDto =
        categoryService.toCategoryDto(manual.getCategory());
      manualDto.setCategoryDto(categoryDto);
      manualDto.setUpdatedAt(manual.getUpdatedAt());
      UserResponseDto userDto =
        userService.toCreatedUserDto(manual.getCreatedByUser());
      manualDto.setCreatedUserDto(userDto);
      pendingListDto.add(manualDto);
    }
    return pendingListDto;
  }

  //index 自分作成PENDINGのデータ一覧を返す。
  public List<ManualResponseDto> createdPendingManualList(Principal principal) {
    log.info("start");
    User targetUser = userService.getUserByPrincipal(principal);
    List<Manual> manualList = manualRepository.findByCreatedByUserAndStatusOrderByUpdatedAtDesc(
        targetUser, ManualStatus.PENDING);
    List<ManualResponseDto> manualDto = new ArrayList<>();
    for (Manual manual : manualList) {
      ManualResponseDto manualResponseDto = new ManualResponseDto();
      manualResponseDto.setManualId(manual.getId());
      manualResponseDto.setTitle(manual.getTitle());
      manualResponseDto.setContent(manual.getContent());
      manualResponseDto.setStatus(manual.getStatus());
      manualResponseDto.setUpdatedAt(manual.getUpdatedAt());
      UserResponseDto userResponseDto =
        userService.toCreatedUserDto(manual.getCreatedByUser());
      manualResponseDto.setCreatedUserDto(userResponseDto);
      CategoryResponseDto categoryDto =
        categoryService.toCategoryDto(manual.getCategory());
      manualResponseDto.setCategoryDto(categoryDto);
      List<ManualHistoryDto> historyDto = manualHistoryService.getManualHistorySummaryDtoList(manual.getId());
      manualResponseDto.setHistories(historyDto);
      manualDto.add(manualResponseDto);
    }
      return  manualDto;
  }

   //index 最近７日間の更新のデータ一覧を返す。
   public List<ManualResponseDto> getRecentWeeklyManuals() {
     log.info("start");
    List<Manual> manualList =
        manualRepository.findByUpdatedAtAfterOrderByUpdatedAtDesc(
            LocalDateTime.now().minusDays(7));
    List<ManualResponseDto> manualDto = new ArrayList<>();
    for (Manual manual : manualList) {
      ManualResponseDto manualResponseDto = new ManualResponseDto();
      manualResponseDto.setManualId(manual.getId());
      manualResponseDto.setTitle(manual.getTitle());
      manualResponseDto.setContent(manual.getContent());
      manualResponseDto.setStatus(manual.getStatus());
      manualResponseDto.setUpdatedAt(manual.getUpdatedAt());
      CategoryResponseDto categoryDto =
        categoryService.toCategoryDto(manual.getCategory());
      UserResponseDto userDto =
        userService.toCreatedUserDto(manual.getCreatedByUser());
      manualResponseDto.setCategoryDto(categoryDto);
      manualResponseDto.setCreatedUserDto(userDto);
      List<ManualHistoryDto> historyDto = manualHistoryService.getManualHistorySummaryDtoList(manual.getId());
      manualResponseDto.setHistories(historyDto);
      manualDto.add(manualResponseDto);
  }
      return  manualDto;
}

   //index 自分作成PENDINGの数を返す。
   public int countCreatedPendingManual(Principal principal) {
     log.info("start");
    User targetUser = userService.getUserByPrincipal(principal);
     Long count =
         manualRepository.countByCreatedByUserAndStatus(
          targetUser,ManualStatus.PENDING);
     int targetCount = Math.toIntExact(count);
    return targetCount;
   }

   //index 最近7日間の更新の数を返す。
   public int countRecentWeeklyManuals() {
     log.info("start");
    Long count =
      manualRepository.countByUpdatedAtAfter(
        LocalDateTime.now().minusDays(7));
    int targetCount = Math.toIntExact(count);
    return  targetCount;
   }

   //MyPage index通知 自分以外作成PENDINGの数を返す。
   public int countNotUserCreatedPendingManualList(Principal principal) {
     log.info("start");
    User targetUser = userService.getUserByPrincipal(principal);
    Long count = manualRepository.countByCreatedByUserNotAndStatus(
          targetUser,
          ManualStatus.PENDING);
    int targetCount = Math.toIntExact(count);
    return targetCount;
   }

   //MyPage index通知 作成者自分の差し戻しマニュアル数を返す。
   public int countMyRollbackManual(Principal principal) {
     log.info("start");
    User targetUser = userService.getUserByPrincipal(principal);
     Long count = manualRepository.countByIsRolledbackTrueAndCreatedByUser(
         targetUser);
     int targetCount = Math.toIntExact(count);
     return targetCount;
   }

   //index 自分作成分の数を返す。
   public int countUserCreatedManual(Principal principal) {
     log.info("start");
    User targetUser = userService.getUserByPrincipal(principal);
    Long count =
      manualRepository.countByCreatedByUser(targetUser);
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
    CategoryResponseDto categoryDto =
      categoryService.toCategoryDto(manual.getCategory());
    responseDto.setCategoryDto(categoryDto);
    UserResponseDto userResponseDto =
      userService.toCreatedUserDto(manual.getCreatedByUser());
    responseDto.setCreatedUserDto(userResponseDto);
    responseDto.setContent(manual.getContent());
    responseDto.setTitle(manual.getTitle());
    return responseDto;
  }

  // ============================
  // ロール判定
  // ============================

  private boolean canGoToNewCreatePage(Principal principal) {
    log.info("start");
    User user = userService.getUserByPrincipal(principal);
    if (!user.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    return true;
  }

  private boolean canArchiveManual(
      User user,
      Manual manual,
      Category category,
      String changeNote) {
    log.info("start");
    if (user.getRole() != UserRole.ADMIN &&
        user.getRole() != UserRole.APPROVER) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    if (!user.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (manual.getStatus() != ManualStatus.DRAFT &&
        manual.getStatus() != ManualStatus.PENDING &&
        manual.getStatus() != ManualStatus.APPROVED) {
      throw new InvalidStateException("マニュアルのステータスが条件を満たしていません。");
    }
    if (!category.isActive()) {
      throw new UnauthorizedException(
          "指定カテゴリはアクティブではありません。");
    }
    if (changeNote == null || changeNote.isBlank()) {
      throw new InvalidStateException("更新期歴は必須です。");
    }
    if (manual.getTitle() == null ||
        manual.getContent() == null ||
        manual.getTitle().isBlank() ||
        manual.getContent().isBlank()) {
      throw new NotFoundException("タイトル・コンテンツがありません。");
    }
    return true;
  }

  // TODO: 通知実装予定
  private boolean canApproveManual(
      Manual manual,
      Category category,
      Principal principal) {
    log.info("start");
    User user = userService.getUserByPrincipal(principal);
    if (user.getRole() != UserRole.ADMIN &&
        user.getRole() != UserRole.APPROVER) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    if (!user.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (manual.getStatus() != ManualStatus.PENDING) {
      throw new InvalidStateException(
          "承認ができるのはステータス:PENDINGのマニュアルのみです。");
    }
    if (Objects.equals(manual.getCreatedByUser().getId(), user.getId())) {
      throw new InvalidStateException(
          "自分が作成したマニュアルの承認をすることは出来ません。");
    }
    if (!category.isActive()) {
      throw new InvalidStateException(
          "有効でないカテゴリーでは承認することが出来ません。");
    }
    return true;
  }

  private boolean canRollbackManual(
      Manual manual,
      Principal principal,
      String changeNote) {
    log.info("start");
    User user = userService.getUserByPrincipal(principal);
    if (user.getRole() != UserRole.ADMIN &&
        user.getRole() != UserRole.APPROVER) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    if (!user.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (manual.getStatus() != ManualStatus.PENDING) {
      throw new InvalidStateException(
          "差し戻しができるのはステータス:PENDINGのマニュアルのみです。");
    }
    if (Objects.equals(manual.getCreatedByUser().getId(), user.getId())) {
      throw new InvalidStateException(
          "自分が作成したマニュアルを差し戻しすることは出来ません。");
    }
    if (changeNote == null || changeNote.isBlank()) {
      throw new InvalidStateException("更新期歴は必須です。");
    }
    return true;
  }

  private boolean canRestoreManual(
      Manual manual,
      Principal principal,
      String changeNote) {
    log.info("start");
    User user = userService.getUserByPrincipal(principal);
    if (user.getRole() != UserRole.ADMIN &&
        user.getRole() != UserRole.APPROVER) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    if (!user.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (manual.getStatus() != ManualStatus.ARCHIVED) {
      throw new InvalidStateException(
          "復帰ができるのはステータス:ARCHIVEDのマニュアルのみです。");
    }
    if (!manual.getCategory().isActive()) {
      throw new InvalidStateException(
          "使用中カテゴリでのみ復帰が出来ます。");
    }
    if (changeNote == null || changeNote.isBlank()) {
      throw new InvalidStateException("更新履歴は必須です。");
    }
    return true;
  }

  private boolean canSaveDraftForCreate(
      Principal principal,
      Category category) {
    log.info("start");
        User user = userService.getUserByPrincipal(principal);
    if (!user.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (!category.isActive()) {
      throw new InvalidStateException(
          "使用中カテゴリでのみ復帰が出来ます。");
    }
    return true;
  }

  private boolean canSubmitToPending(
      Principal principal,
      Category category) {
    log.info("start");
    User user = userService.getUserByPrincipal(principal);
    if (!user.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (!category.isActive()) {
      throw new InvalidStateException(
          "使用中カテゴリでのみ復帰が出来ます。");
    }
    return true;
  }

  private boolean canGoToEditPage(
      Principal principal,
      Category category,
      ManualStatus status) {
    log.info("start");
    User user = userService.getUserByPrincipal(principal);

    if (!user.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (!category.isActive()) {
      throw new InvalidStateException(
          "使用中カテゴリでのみ復帰が出来ます。");
    }
    if (status != ManualStatus.DRAFT && status != ManualStatus.PENDING) {
      throw new InvalidStateException(
          "編集ができるのはステータス:DRAFT/PENDINGのマニュアルのみです。");
    }
    return true;
  }

  private boolean canSaveDraftForCopy(
      Principal principal,
      Category category,
      String changeNote) {
    log.info("start");
    // アクティブユーザー アクティブカテゴリー チェンジノート必須
    User user = userService.getUserByPrincipal(principal);
    if (!user.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (!category.isActive()) {
      throw new InvalidStateException(
          "停止中カテゴリにマニュアルは作成できません。");
    }
    if (changeNote == null || changeNote.isBlank()) {
      throw new InvalidStateException("更新履歴は必須です。");
    }
    return true;
  }

  private boolean canEditToPending(
      Principal principal,
      Category category,
      Manual manual) {
    log.info("start");
    // アクティブユーザー ステータスドラフト・ペンディングのみ 作成者のみ編集可 アクティブカテゴリ
    User user = userService.getUserByPrincipal(principal);
    if (!user.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (!category.isActive()) {
      throw new InvalidStateException(
          "停止中カテゴリにマニュアルは作成できません。");
    }
    if (manual.getStatus() != ManualStatus.DRAFT &&
        manual.getStatus() != ManualStatus.PENDING) {
      throw new InvalidStateException(
          "編集ができるのはステータス:DRAFT/PENDINGのマニュアルのみです。");
    }
    if (!Objects.equals(manual.getCreatedByUser().getId(), user.getId())) {
      throw new InvalidStateException(
          "自分が作成したマニュアル以外を編集することはできません。");
    }
    return true;
  }

  private boolean canGoToDetailPage(Principal principal) {
    log.info("start");
    // アクティブユーザー
    User user = userService.getUserByPrincipal(principal);
    if (!user.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    return true;
  }

  private boolean canGoToCopyPage(Principal principal) {
    log.info("start");
    // アクティブユーザー
    User user = userService.getUserByPrincipal(principal);
    if (!user.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    return true;
  }

  private boolean canSearchManuals(Principal principal) {
    log.info("start");
    // アクティブユーザー
    User user = userService.getUserByPrincipal(principal);
    if (!user.isActive()) {
      throw new UnauthorizedException("有効なユーザーではありません。");
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
}
