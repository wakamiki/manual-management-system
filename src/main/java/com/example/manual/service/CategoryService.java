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

  public CategoryService(
      CategoryRepository categoryRepository,
      UserService userService,
      UserPermissionService userPermissionService) {

    this.categoryRepository = categoryRepository;
    this.userService = userService;
    this.userPermissionService = userPermissionService;
  }

  // =============================================
  // 画面表示
  // =============================================

  // category-management表示
  public CategoryViewDto showCategoryManagement(
      Principal principal, Pageable pageable) {
    log.info("start");
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

  public boolean createCategory(
      CategoryFormDto formDto,
      Principal principal) {
    log.info("start");
    User playUser = userService.getUserByPrincipal(principal);
    if (!canCreateCategory(formDto, playUser)) {
      throw new InvalidStateException("判定エラー");
    }
    if (formDto.isConfirmed()) {
      // カテゴリー名重複了承 チェック回避処理
    } else if (isCategoryNameTaken(formDto)) {
      // カテゴリー名重複チェック
      return true;
    }

    int targetOrder = formDto.getDisplayOrder();
    shiftUpOrderNumbers(targetOrder, null);
    Category category = new Category();
    category.setCategoryName(formDto.getCategoryName());
    category.setDisplayOrder(targetOrder);
    category.markCreatedNow();
    category.markUpdatedNow();
    category.markActive();
    categoryRepository.save(category);
    return false;
  }

  public void updateCategory(
      CategoryFormDto formDto,
      Principal principal) {
    log.info("start");
    User playUser = userService.getUserByPrincipal(principal);
    if (!canUpdateCategory(formDto, playUser)) {
      throw new InvalidStateException("判定エラー");
    }
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
    categoryRepository.save(category);
  }

  public void deactivateCategory(Principal principal, Long categoryId) {
    log.info("start");
    if (!isAdmin(principal)) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    Category category = getCategoryById(categoryId);

    category.markInactive();
    category.markUpdatedNow();
    categoryRepository.save(category);

  }

  public void activateCategory(Principal principal, Long categoryId) {
    log.info("start");
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
    log.info("start");
    return categoryRepository.findAllByOrderByCategoryNameAsc(pageable);
  }

  public Category getCategoryById(Long categoryId) {
    log.info("start");
    Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
    if (categoryOpt.isEmpty()) {
      throw new NotFoundException("指定されたカテゴリーは存在しません");
    }
    return categoryOpt.get();
  }

  // index 検索欄のカテゴリーリスト(active)取得
  public List<CategoryResponseDto> getActiveCategoryDtos() {
    log.info("start");
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
    log.info("start");
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
    log.info("start");
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
    log.info("start");
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
    log.info("start");
    User targetUser = userService.getUserByPrincipal(principal);
    if (targetUser.getRole() != UserRole.ADMIN) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    return true;
  }

  // カテゴリー名重複チェック
  public boolean isCategoryNameTaken(CategoryFormDto formDto) {
    log.info("start");
    if (formDto.getId() == null) {
      return categoryRepository.existsByCategoryName(formDto.getCategoryName());
    }
    return categoryRepository.existsByCategoryNameAndIdNot(formDto.getCategoryName(), formDto.getId());
  }

  private boolean canShowCategoryManagement(User playUser) {
    log.info("start");
    if (playUser.getRole() != UserRole.ADMIN && playUser.getRole() != UserRole.GUEST) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    if (playUser.isActive() != true) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    return true;
  }

  private boolean canShowCategoryUpdateMode(User playUser) {
    log.info("start");
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
    log.info("start");
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

}
