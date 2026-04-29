package com.example.manual.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.manual.dto.CategoryDetailDto;
import com.example.manual.dto.CategoryFormDto;
import com.example.manual.dto.CategoryResponseDto;
import com.example.manual.dto.CategoryViewDto;
import com.example.manual.dto.PagingDto;
import com.example.manual.dto.UserResponseDto;
import com.example.manual.entity.Category;
import com.example.manual.entity.User;
import com.example.manual.enums.DuplicateStatus;
import com.example.manual.enums.UserRole;
import com.example.manual.enums.ViewMode;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.exception.NotFoundException;
import com.example.manual.exception.UnauthorizedException;
import com.example.manual.repository.CategoryRepository;

@Service
public class CategoryService {

  private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

  public final CategoryRepository categoryRepository;
  public final UserService userService;
  public final UserPermissionService userPermissionService;
  public final ManualArchiveService manualArchiveService;

  public CategoryService(
      CategoryRepository categoryRepository,
      UserService userService,
      UserPermissionService userPermissionService,
      ManualArchiveService manualArchiveService) {

    this.categoryRepository = categoryRepository;
    this.userService = userService;
    this.userPermissionService = userPermissionService;
    this.manualArchiveService = manualArchiveService;
  }

  // =============================================
  // 画面表示
  // =============================================

  // category-management表示
  public CategoryViewDto showCategoryManagement(
      Principal principal, Pageable pageable) {

    User playUser = userService.getUserByPrincipal(principal);
    log.info("[{}][PERMISSION][START] rule={}");
    if (!canShowCategoryManagement(playUser)) {
      throw new InvalidStateException("判定エラー");
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    Page<Category> categories = categoryRepository.findAllByOrderByDisplayOrderAsc(pageable);
    CategoryViewDto categoryDto = toCategoryViewDto(playUser, categories);

    return categoryDto;
  }

  public CategoryViewDto showCategoryUpdateMode(
      Principal principal, Long categoryId, Pageable pageable) {
    User playUser = userService.getUserByPrincipal(principal);
    log.info("[{}][PERMISSION][START] rule={}");
    if (!canShowCategoryUpdateMode(playUser)) {
      throw new InvalidStateException("判定エラー");
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    CategoryViewDto viewDto = showCategoryManagement(principal, pageable);
    viewDto.setMode(ViewMode.EDIT);

    return viewDto;
  }

  // ============================================
  // 登録・更新
  // ============================================

  public CategoryFormDto createCategory(
      CategoryFormDto formDto,
      Principal principal) {

    User playUser = userService.getUserByPrincipal(principal);
    log.info("[{}][PERMISSION][START] rule={}");
    if (!canCreateCategory(formDto, playUser)) {
      throw new InvalidStateException("判定エラー");
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    formDto = handleDuplicateOnCreate(formDto, principal);
    log.info("[{}][PERMISSION][START] rule={}");
    if (formDto.getDuplicateStatus() == DuplicateStatus.ACTIVE_DUPLICATE) {
      return formDto;
    } else
      log.info("[{}][PERMISSION][START] rule={}");
    if (formDto.getDuplicateStatus() == DuplicateStatus.INACTIVE_DUPLICATE) {
      return formDto;
    }
    // 通常処理
    int targetOrder = formDto.getDisplayOrder();
    shiftUpOrderNumbers(targetOrder, null);
    Category category = new Category();
    category.setCategoryName(formDto.getCategoryName());
    category.setDisplayOrder(targetOrder);
    category.markCreatedNow();
    category.markUpdatedNow();
    category.markActive();
    log.info("[{}][{}][PERSIST][START] action={} id={}");
    categoryRepository.save(category);
    log.info("[{}][{}][PERSIST][DONE] action={} id={}");
    return formDto;
  }

  public CategoryFormDto updateCategory(
      CategoryFormDto formDto,
      Principal principal) {

    User playUser = userService.getUserByPrincipal(principal);
    log.info("[{}][PERMISSION][START] rule={}");
    if (!canUpdateCategory(formDto, playUser)) {
      throw new InvalidStateException("判定エラー");
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    formDto = handleDuplicateOnCreate(formDto, principal);
    log.info("[{}][PERMISSION][START] rule={}");
    if (formDto.getDuplicateStatus() == DuplicateStatus.ACTIVE_DUPLICATE) {
      return formDto;
    } else
      log.info("[{}][PERMISSION][START] rule={}");
    if (formDto.getDuplicateStatus() == DuplicateStatus.INACTIVE_DUPLICATE) {
      return formDto;
    }

    // 通常処理
    Category category = findCategoryOrThrow(formDto.getId());
    int currentOrder = category.getDisplayOrder();
    int targetOrder = formDto.getDisplayOrder();
    log.info("[{}][PERMISSION][START] rule={}");
    if (targetOrder < currentOrder) {
      shiftUpOrderNumbers(targetOrder, currentOrder - 1);
    } else if (targetOrder > currentOrder) {
      shiftDownOrderNumbers(currentOrder + 1, targetOrder);
    }
    log.info("[{}][PERMISSION][PASS] rule={}");

    category.setCategoryName(formDto.getCategoryName());
    category.setDisplayOrder(targetOrder);
    category.markUpdatedNow();
    log.info("[{}][{}][PERSIST][START] action={} id={}");
    categoryRepository.save(category);
    log.info("[{}][{}][PERSIST][DONE] action={} id={}");
    return formDto;
  }

  public void deactivateCategory(Principal principal, Long categoryId) {

    log.info("[{}][PERMISSION][START] rule={}");
    if (!isAdmin(principal)) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    Category category = getCategoryById(categoryId);

    category.markInactive();
    category.markUpdatedNow();
    log.info("[{}][{}][PERSIST][START] action={} id={}");
    categoryRepository.save(category);
    log.info("[{}][{}][PERSIST][DONE] action={} id={}");

  }

  public void activateCategory(Principal principal, Long categoryId) {

    log.info("[{}][PERMISSION][START] rule={}");
    if (!isAdmin(principal)) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    Category category = getCategoryById(categoryId);
    category.markActive();
    category.markUpdatedNow();
    log.info("[{}][{}][PERSIST][START] action={} id={}");
    categoryRepository.save(category);
    log.info("[{}][{}][PERSIST][DONE] action={} id={}");

  }

  // ===============================================
  // 取得系
  // ===============================================

  public Page<Category> getAllCategories(Pageable pageable) {
    log.info("[{}][FETCH]");
    return categoryRepository.findAllByOrderByCategoryNameAsc(pageable);
  }

  public Category getCategoryById(Long categoryId) {

    Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
    log.info("[{}][FETCH]");
    log.info("[{}][PERMISSION][START] rule={}");
    if (categoryOpt.isEmpty()) {
      throw new NotFoundException("指定されたカテゴリーは存在しません");
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    return categoryOpt.get();
  }

  // index 検索欄のカテゴリーリスト(active)取得
  public List<CategoryResponseDto> getActiveCategoryDtos() {

    List<Category> activeCategories = categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    log.info("[{}][FETCH]");
    List<CategoryResponseDto> activeCategoriesDto = new ArrayList<>();
    for (Category category : activeCategories) {
      CategoryResponseDto activeCategoryDto = new CategoryResponseDto();
      activeCategoryDto.setId(category.getId());
      activeCategoryDto.setCategoryName(category.getCategoryName());
      activeCategoriesDto.add(activeCategoryDto);
    }
    return activeCategoriesDto;
  }

  // index 検索欄のカテゴリーリスト(inactive)取得
  public List<CategoryResponseDto> getInactiveCategoryDtos() {

    List<Category> inactiveCategories = categoryRepository.findByIsActiveFalseOrderByDisplayOrderAsc();
    log.info("[{}][FETCH]");
    List<CategoryResponseDto> inactiveCategoriesDto = new ArrayList<>();
    for (Category category : inactiveCategories) {
      CategoryResponseDto inactiveCategoryDto = new CategoryResponseDto();
      inactiveCategoryDto.setId(category.getId());
      inactiveCategoryDto.setCategoryName(category.getCategoryName());
      inactiveCategoriesDto.add(inactiveCategoryDto);
    }
    return inactiveCategoriesDto;
  }

  // displayオーダー割り込み
  private void shiftDownOrderNumbers(Integer start, Integer endInclusive) {
    // new を挿入するために範囲をずらす

    log.info("[{}][PERMISSION][START] rule={}");
    if (start == null) {
      return;
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    Integer end = endInclusive;
    log.info("[{}][PERMISSION][START] rule={}");
    if (end == null) {
      Optional<Category> categoryOpt = categoryRepository.findTopByOrderByDisplayOrderDesc();
      log.info("[{}][PERMISSION][START] rule={}");
      if (categoryOpt.isEmpty()) {
        throw new NotFoundException("カテゴリ最大値が取得できませんでした。");
      }
      log.info("[{}][PERMISSION][PASS] rule={}");
      Integer categoryEnd = categoryOpt.get().getDisplayOrder();
      end = categoryEnd;
    }
    log.info("[{}][PERMISSION][START] rule={}");
    if (end < start) {
      return;
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    List<Category> targets = categoryRepository.findByDisplayOrderBetweenOrderByDisplayOrderAsc(
        start, end);

    for (Category target : targets) {
      target.setDisplayOrder(target.getDisplayOrder() - 1);
    }
    log.info("[{}][{}][PERSIST][START] action={} id={}");
    categoryRepository.saveAll(targets);
    log.info("[{}][{}][PERSIST][DONE] action={} id={}");
  }

  // displayオーダー割り込み
  private void shiftUpOrderNumbers(Integer start, Integer end) {
    // new を挿入するために範囲をずらす

    log.info("[{}][PERMISSION][START] rule={}");
    if (start == null) {
      return;
    }
    log.info("[{}][PERMISSION][START] rule={}");
    if (end == null) {
      Optional<Category> categoryOpt = categoryRepository.findTopByOrderByDisplayOrderDesc();
      log.info("[{}][PERMISSION][START] rule={}");
      if (categoryOpt.isEmpty()) {
        return;
      }
      end = categoryOpt.get().getDisplayOrder();
    }
    log.info("[{}][PERMISSION][START] rule={}");
    if (end < start) {
      return;
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    List<Category> targets = categoryRepository.findByDisplayOrderBetweenOrderByDisplayOrderAsc(
        start, end);

    for (Category target : targets) {
      target.setDisplayOrder(target.getDisplayOrder() + 1);
    }
    log.info("[{}][{}][PERSIST][START] action={} id={}");
    categoryRepository.saveAll(targets);
    log.info("[{}][{}][PERSIST][DONE] action={} id={}");
  }

  public Category findInactiveCategoryByName(CategoryFormDto formDto) {
    log.info("[{}][PERMISSION][START] rule={}");
    log.info("[{}][RULE] check={}", formDto.getId());
    if (formDto.getId() == null) {
      // 新規作成時
      Optional<Category> categoryOpt = categoryRepository
          .findByCategoryNameAndIsActiveFalse(formDto.getCategoryName());
      log.info("[{}][FETCH]");
      log.info("[{}][PERMISSION][START] rule={}");
      if (categoryOpt.isEmpty()) {
        throw new InvalidStateException("取得カテゴリがありません。");
      }
      return categoryOpt.get();
    }
    // 変更時
    Optional<Category> categoryOpt = categoryRepository
        .findByCategoryNameAndIsActiveFalseAndIdNot(formDto.getCategoryName(), formDto.getId());
    log.info("[{}][FETCH]");
    log.info("[{}][PERMISSION][START] rule={}");
    if (categoryOpt.isEmpty()) {
      throw new InvalidStateException("取得カテゴリがありません。");
    }
    return categoryOpt.get();
  }

  // ===============================================
  // Dto詰め替え
  // ===============================================
  public CategoryResponseDto toCategoryDto(Category category) {
    CategoryResponseDto categoryDto = new CategoryResponseDto();
    categoryDto.setId(category.getId());
    categoryDto.setCategoryName(category.getCategoryName());

    return categoryDto;
  }

  private CategoryViewDto toCategoryViewDto(User playUser, Page<Category> categories) {
    List<CategoryDetailDto> responseDtos = new ArrayList<>();
    PagingDto pagingDto = PagingDto.from(categories);
    for (Category category : categories) {
      CategoryDetailDto responseDto = new CategoryDetailDto();
      responseDto.setActive(category.isActive());
      responseDto.setCategoryName(category.getCategoryName());
      responseDto.setDisplayOrder(category.getDisplayOrder());
      responseDto.setId(category.getId());
      responseDto.setUpdatedAt(category.getUpdatedAt());
      responseDto.setActiveLabel(getActiveLabel(category.isActive()));
      responseDtos.add(responseDto);
    }

    CategoryViewDto categoryDto = new CategoryViewDto();
    categoryDto.setPagingDto(pagingDto);
    categoryDto.setCategoryListDto(responseDtos);
    categoryDto.setPlayUser(userService.toCreatedUserDto(playUser));
    categoryDto.setCanGuest(userPermissionService.isGuest(playUser));
    categoryDto.setMode(ViewMode.CREATE);

    return categoryDto;
  }

  public CategoryViewDto convertToCategoryViewDto(CategoryFormDto formDto, Principal principal,
      Pageable pageable) {
    CategoryViewDto viewDto = showCategoryManagement(principal, pageable);
    User playUser = userService.getUserByPrincipal(principal);
    UserResponseDto userDto = userService.toCreatedUserDto(playUser);
    viewDto.setPlayUser(userDto);
    log.info("[{}][PERMISSION][START] rule={}");
    if (formDto.getId() == null) {
      viewDto.setMode(ViewMode.CREATE);
    } else {
      viewDto.setMode(ViewMode.EDIT);
    }
    return viewDto;
  }

  public CategoryFormDto toFormDto(Long categoryId) {
    Category category = getCategoryById(categoryId);
    CategoryFormDto formDto = new CategoryFormDto();
    formDto.setId(category.getId());
    formDto.setDisplayOrder(category.getDisplayOrder());
    formDto.setCategoryName(category.getCategoryName());
    formDto.setActive(category.isActive());
    return formDto;
  }

  // ===============================================
  // 権限判定
  // ===============================================

  // adminのみ
  private boolean isAdmin(Principal principal) {

    User targetUser = userService.getUserByPrincipal(principal);
    if (targetUser.getRole() != UserRole.ADMIN) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    return true;
  }

  // カテゴリー名重複チェック
  public boolean existsActiveCategoryByName(CategoryFormDto formDto) {
    log.info("[{}][RULE] check={}", formDto.getId());
    if (formDto.getId() == null) {
      // 新規作成時
      return categoryRepository.existsByCategoryNameAndIsActiveTrue(formDto.getCategoryName());
    }
    // 変更時
    return categoryRepository.existsByCategoryNameAndIsActiveTrueAndIdNot(formDto.getCategoryName(), formDto.getId());
  }

  public boolean existsInactiveCategoryByName(CategoryFormDto formDto) {
    log.info("[{}][RULE] check={}", formDto.getId());
    if (formDto.getId() == null) {
      // 新規作成時
      return categoryRepository.existsByCategoryNameAndIsActiveFalse(formDto.getCategoryName());
    }
    // 変更時
    return categoryRepository.existsByCategoryNameAndIsActiveFalseAndIdNot(formDto.getCategoryName(), formDto.getId());
  }

  private boolean canShowCategoryManagement(User playUser) {

    if (playUser.getRole() != UserRole.ADMIN && playUser.getRole() != UserRole.GUEST) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    if (playUser.isActive() != true) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    return true;
  }

  private boolean canShowCategoryUpdateMode(User playUser) {

    if (playUser.getRole() != UserRole.ADMIN && playUser.getRole() != UserRole.GUEST) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    if (playUser.isActive() != true) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    return true;
  }

  private boolean canCreateCategory(CategoryFormDto formDto, User playUser) {
    if (playUser.getRole() != UserRole.ADMIN) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    if (playUser.isActive() != true) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (formDto.getCategoryName() == null ||
        formDto.getCategoryName().isBlank()) {
      throw new InvalidStateException("カテゴリー名は必須項目です。");
    }
    if (formDto.getDisplayOrder() == null) {
      throw new InvalidStateException("displayOrderは必須項目です。");
    }
    return true;
  }

  private boolean canUpdateCategory(CategoryFormDto formDto, User playUser) {
    if (playUser.getRole() != UserRole.ADMIN) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    if (playUser.isActive() != true) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (formDto.getCategoryName() == null ||
        formDto.getCategoryName().isBlank()) {
      throw new InvalidStateException("カテゴリー名は必須項目です。");
    }
    if (formDto.getDisplayOrder() == null) {
      throw new InvalidStateException("displayOrderは必須項目です。");
    }
    return true;
  }

  // ===============================================
  // 共通処理
  // ===============================================

  private Category findCategoryOrThrow(Long categoryId) {

    Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
    log.info("[{}][FETCH]");
    log.info("[{}][PERMISSION][START] rule={}");
    if (categoryOpt.isEmpty()) {
      throw new NotFoundException("カテゴリが見つかりません。");
    }
    log.info("[{}][PERMISSION][PASS] rule={}");
    return categoryOpt.get();
  }

  public String getActiveLabel(boolean isActive) {
    log.info("[{}][PERMISSION][START] rule={}");
    if (isActive) {

      return "使用中";
    }
    return "停止中";
  }

  private CategoryFormDto handleDuplicateOnCreate(CategoryFormDto formDto, Principal principal) {
    log.info("[{}][PERMISSION][START] rule={}");
    if (formDto.isConfirmed()) {
      // カテゴリー名重複了承 チェック回避処理
      Category category = findInactiveCategoryByName(formDto);
      manualArchiveService.archiveManualsByInactiveDuplicateCategory(category, principal);
      formDto.setDuplicateStatus(DuplicateStatus.NONE);
    } else
      log.info("[{}][PERMISSION][START] rule={}");
    if (existsActiveCategoryByName(formDto)) {
      // カテゴリー名重複チェックactive
      formDto.setDuplicateStatus(DuplicateStatus.ACTIVE_DUPLICATE);
      return formDto;
    } else
      log.info("[{}][PERMISSION][START] rule={}");
    if (existsInactiveCategoryByName(formDto)) {
      // カテゴリー名重複チェックinactive
      formDto.setDuplicateStatus(DuplicateStatus.INACTIVE_DUPLICATE);
      return formDto;
    }
    return formDto;
  }
}
