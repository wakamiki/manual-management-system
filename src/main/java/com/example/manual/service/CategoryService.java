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
import org.springframework.transaction.annotation.Transactional;

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
    if (!canShowCategoryManagement(playUser)) {
      throw new InvalidStateException("判定エラー");
    }
    Page<Category> categories = categoryRepository.findAllByOrderByDisplayOrderAsc(pageable);
    CategoryViewDto categoryDto = toCategoryViewDto(playUser, categories);

    return categoryDto;
  }

  public CategoryViewDto showCategoryUpdateMode(
      Principal principal, Long categoryId, Pageable pageable) {
    User playUser = userService.getUserByPrincipal(principal);
    if (!canShowCategoryUpdateMode(playUser)) {
      throw new InvalidStateException("判定エラー");
    }
    CategoryViewDto viewDto = showCategoryManagement(principal, pageable);
    viewDto.setMode(ViewMode.EDIT);

    return viewDto;
  }

  // ============================================
  // 登録・更新
  // ============================================

  @Transactional
  public CategoryFormDto createCategory(
      CategoryFormDto formDto,
      Principal principal) {

    User playUser = userService.getUserByPrincipal(principal);
    if (!canCreateCategory(formDto, playUser)) {
      throw new InvalidStateException("判定エラー");
    }
    formDto = handleDuplicateOnCreate(formDto, principal);
    if (formDto.getDuplicateStatus() == DuplicateStatus.ACTIVE_DUPLICATE) {
      return formDto;
    } else
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
    Category savedCategory = categoryRepository.save(category);
    log.debug("Category created: id={}, name={}, displayOrder={}",
        savedCategory.getId(), savedCategory.getCategoryName(), savedCategory.getDisplayOrder());
    return formDto;
  }

  @Transactional
  public CategoryFormDto updateCategory(
      CategoryFormDto formDto,
      Principal principal) {

    User playUser = userService.getUserByPrincipal(principal);
    if (!canUpdateCategory(formDto, playUser)) {
      throw new InvalidStateException("判定エラー");
    }
    formDto = handleDuplicateOnCreate(formDto, principal);
    if (formDto.getDuplicateStatus() == DuplicateStatus.ACTIVE_DUPLICATE) {
      return formDto;
    } else
    if (formDto.getDuplicateStatus() == DuplicateStatus.INACTIVE_DUPLICATE) {
      return formDto;
    }

    // 通常処理
    Category category = findCategoryOrThrow(formDto.getId());
    int currentOrder = category.getDisplayOrder();
    int targetOrder = formDto.getDisplayOrder();
    if (targetOrder < currentOrder) {
      shiftUpOrderNumbers(targetOrder, currentOrder - 1);
    } else if (targetOrder > currentOrder) {
      shiftDownOrderNumbers(currentOrder + 1, targetOrder);
    }

    category.setCategoryName(formDto.getCategoryName());
    category.setDisplayOrder(targetOrder);
    category.markUpdatedNow();
    Category savedCategory = categoryRepository.save(category);
    log.debug("Category updated: id={}, name={}, displayOrder={}",
        savedCategory.getId(), savedCategory.getCategoryName(), savedCategory.getDisplayOrder());
    return formDto;
  }

  public void deactivateCategory(Principal principal, Long categoryId) {

    if (!isAdmin(principal)) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    Category category = getCategoryById(categoryId);

    category.markInactive();
    category.markUpdatedNow();
    categoryRepository.save(category);

  }

  public void activateCategory(Principal principal, Long categoryId) {

    if (!isAdmin(principal)) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    Category category = getCategoryById(categoryId);
    category.markActive();
    category.markUpdatedNow();
    categoryRepository.save(category);

  }

  // ===============================================
  // 取得系
  // ===============================================

  public Page<Category> getAllCategories(Pageable pageable) {
    return categoryRepository.findAllByOrderByCategoryNameAsc(pageable);
  }

  public Category getCategoryById(Long categoryId) {

    Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
    if (categoryOpt.isEmpty()) {
      throw new NotFoundException("指定されたカテゴリーは存在しません");
    }
    return categoryOpt.get();
  }

  // index 検索欄のカテゴリーリスト(active)取得
  public List<CategoryResponseDto> getActiveCategoryDtos() {

    List<Category> activeCategories = categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
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

    if (start == null) {
      return;
    }
    Integer end = endInclusive;
    if (end == null) {
      Optional<Category> categoryOpt = categoryRepository.findTopByOrderByDisplayOrderDesc();
      if (categoryOpt.isEmpty()) {
        throw new NotFoundException("カテゴリ最大値が取得できませんでした。");
      }
      Integer categoryEnd = categoryOpt.get().getDisplayOrder();
      end = categoryEnd;
    }
    if (end < start) {
      return;
    }
    List<Category> targets = categoryRepository.findByDisplayOrderBetweenOrderByDisplayOrderAsc(
        start, end);

    for (Category target : targets) {
      target.setDisplayOrder(target.getDisplayOrder() - 1);
    }
    categoryRepository.saveAll(targets);
  }

  // displayオーダー割り込み
  private void shiftUpOrderNumbers(Integer start, Integer end) {
    // new を挿入するために範囲をずらす

    if (start == null) {
      return;
    }
    if (end == null) {
      Optional<Category> categoryOpt = categoryRepository.findTopByOrderByDisplayOrderDesc();
      if (categoryOpt.isEmpty()) {
        return;
      }
      end = categoryOpt.get().getDisplayOrder();
    }
    if (end < start) {
      return;
    }
    List<Category> targets = categoryRepository.findByDisplayOrderBetweenOrderByDisplayOrderAsc(
        start, end);

    for (Category target : targets) {
      target.setDisplayOrder(target.getDisplayOrder() + 1);
    }
    categoryRepository.saveAll(targets);
  }

  public Category findInactiveCategoryByName(CategoryFormDto formDto) {
    if (formDto.getId() == null) {
      // 新規作成時
      Optional<Category> categoryOpt = categoryRepository
          .findByCategoryNameAndIsActiveFalse(formDto.getCategoryName());
      if (categoryOpt.isEmpty()) {
        throw new InvalidStateException("取得カテゴリがありません。");
      }
      return categoryOpt.get();
    }
    // 変更時
    Optional<Category> categoryOpt = categoryRepository
        .findByCategoryNameAndIsActiveFalseAndIdNot(formDto.getCategoryName(), formDto.getId());
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
    if (formDto.getId() == null) {
      // 新規作成時
      return categoryRepository.existsByCategoryNameAndIsActiveTrue(formDto.getCategoryName());
    }
    // 変更時
    return categoryRepository.existsByCategoryNameAndIsActiveTrueAndIdNot(formDto.getCategoryName(), formDto.getId());
  }

  public boolean existsInactiveCategoryByName(CategoryFormDto formDto) {
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
    if (categoryOpt.isEmpty()) {
      throw new NotFoundException("カテゴリが見つかりません。");
    }
    return categoryOpt.get();
  }

  public String getActiveLabel(boolean isActive) {
    if (isActive) {

      return "使用中";
    }
    return "停止中";
  }

  private CategoryFormDto handleDuplicateOnCreate(CategoryFormDto formDto, Principal principal) {
    if (formDto.isConfirmed()) {
      // カテゴリー名重複了承 チェック回避処理
      Category category = findInactiveCategoryByName(formDto);
      log.debug("Duplicate confirmed: name={}, inactiveCategoryId={}",
          formDto.getCategoryName(), category.getId());
      manualArchiveService.archiveManualsByInactiveDuplicateCategory(category, principal);
      formDto.setDuplicateStatus(DuplicateStatus.NONE);
      return formDto;
    }

    if (existsActiveCategoryByName(formDto)) {
      // カテゴリー名重複チェックactive
      formDto.setDuplicateStatus(DuplicateStatus.ACTIVE_DUPLICATE);
      log.debug("Duplicate detected (active): name={}, mode={}",
          formDto.getCategoryName(), formDto.getId() == null ? "CREATE" : "UPDATE");
      return formDto;
    }

    if (existsInactiveCategoryByName(formDto)) {
      // カテゴリー名重複チェックinactive
      formDto.setDuplicateStatus(DuplicateStatus.INACTIVE_DUPLICATE);
      log.debug("Duplicate detected (inactive): name={}, mode={}",
          formDto.getCategoryName(), formDto.getId() == null ? "CREATE" : "UPDATE");
      return formDto;
    }
    log.debug("No duplicate detected: name={}, mode={}",
        formDto.getCategoryName(), formDto.getId() == null ? "CREATE" : "UPDATE");
    return formDto;
  }
}
