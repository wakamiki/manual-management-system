package com.example.manual.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.manual.dto.CategoryDetailDto;
import com.example.manual.dto.CategoryFormDto;
import com.example.manual.dto.CategoryRequestDto;
import com.example.manual.dto.CategoryResponseDto;
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

  public CategoryService(
      CategoryRepository categoryRepository,
      UserService userService) {

    this.categoryRepository = categoryRepository;
    this.userService = userService;
  }

  // category-management表示
  public CategoryFormDto showCategoryManagement(
      Principal principal) {
    log.info("start");
    User playUser = userService.getUserByPrincipal(principal);
    if (!canShowCategoryManagement(playUser)) {
      throw new InvalidStateException("判定エラー");
    }
    List<Category> categories = categoryRepository.findAllByOrderByDisplayOrderAsc();
    CategoryFormDto categoryDto = toCategoryFormDto(playUser, categories);

    return categoryDto;
  }

  // ============================================
  // 登録・更新
  // ============================================

  public boolean createCategory(
      CategoryRequestDto requestDto,
      Principal principal) {
    log.info("start");
    User playUser = userService.getUserByPrincipal(principal);
    if (!canCreateCategory(requestDto, playUser)) {
      throw new InvalidStateException("判定エラー");
    }
    if (requestDto.isConfirmed()) {
      // カテゴリー名重複了承 チェック回避処理
    } else if (isCategoryNameTaken(requestDto)) {
      // カテゴリー名重複チェック
      return true;
    }

    int targetOrder = requestDto.getDisplayOrder();
    shiftUpOrderNumbers(targetOrder, null);
    Category category = new Category();
    category.setCategoryName(requestDto.getCategoryName());
    category.setDisplayOrder(targetOrder);
    category.markCreatedNow();
    category.markUpdatedNow();
    category.markActive();
    categoryRepository.save(category);
    return false;
  }

  public boolean updateCategory(
      CategoryRequestDto requestDto,
      Principal principal) {
    log.info("start");
    User playUser = userService.getUserByPrincipal(principal);
    if (!canUpdateCategory(requestDto, playUser)) {
      throw new InvalidStateException("判定エラー");
    }
    if (requestDto.isConfirmed()) {
      // カテゴリー名重複了承 チェック回避処理
    } else if (isCategoryNameTaken(requestDto)) {
      // カテゴリー名重複チェック
      return true;
    }

    Category category = findCategoryOrThrow(requestDto.getId());
    int currentOrder = category.getDisplayOrder();
    int targetOrder = requestDto.getDisplayOrder();
    if (targetOrder < currentOrder) {
      shiftUpOrderNumbers(targetOrder, currentOrder - 1);
    } else if (targetOrder > currentOrder) {
      shiftDownOrderNumbers(currentOrder + 1, targetOrder);
    }

    category.setCategoryName(requestDto.getCategoryName());
    category.setDisplayOrder(targetOrder);
    category.markUpdatedNow();
    categoryRepository.save(category);
    return false;
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

  public List<Category> getAllCategories() {
    log.info("start");
    return categoryRepository.findAllByOrderByCategoryNameAsc();
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
      target.setDisplayOrder(target.getDisplayOrder() + 1);
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

  private CategoryFormDto toCategoryFormDto(User playUser, List<Category> categories) {
    List<CategoryDetailDto> responseDtos = new ArrayList<>();
    for (Category category : categories) {
      CategoryDetailDto responseDto = new CategoryDetailDto();
      responseDto.setActive(category.isActive());
      responseDto.setCategoryName(category.getCategoryName());
      responseDto.setDisplayOrder(category.getDisplayOrder());
      responseDto.setId(category.getId());
      responseDto.setUpdatedAt(category.getUpdatedAt());
      responseDtos.add(responseDto);
    }

    CategoryFormDto categoryDto = new CategoryFormDto();
    categoryDto.setCategoryListDto(responseDtos);
    categoryDto.setPlayUser(userService.toCreatedUserDto(playUser));
    categoryDto.setMode(ViewMode.CREATE);

    return categoryDto;
  }

  public CategoryFormDto convertToCategoryFormDto(CategoryRequestDto requestDto, Principal principal) {
    CategoryFormDto formDto = showCategoryManagement(principal);
    User playUser = userService.getUserByPrincipal(principal);
    UserResponseDto userDto = userService.toCreatedUserDto(playUser);
    CategoryDetailDto detailDto = new CategoryDetailDto();
    detailDto.setId(requestDto.getId());
    detailDto.setCategoryName(requestDto.getCategoryName());
    detailDto.setDisplayOrder(requestDto.getDisplayOrder());

    formDto.setPlayUser(userDto);
    formDto.setTargetCategory(detailDto);
    if (requestDto.getId() == null) {
      formDto.setMode(ViewMode.CREATE);
    } else {
      formDto.setMode(ViewMode.EDIT);
    }
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
  private boolean isCategoryNameTaken(CategoryRequestDto requestDto) {
    log.info("start");
    if (requestDto.getId() == null) {
      return categoryRepository.existsByCategoryName(requestDto.getCategoryName());
    }
    return categoryRepository.existsByCategoryNameAndIdNot(requestDto.getCategoryName(), requestDto.getId());
  }

  private boolean canShowCategoryManagement(User playUser) {
    log.info("start");
    if (playUser.getRole() != UserRole.ADMIN) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    if (playUser.isActive() != true) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    return true;
  }

  private boolean canCreateCategory(CategoryRequestDto requestDto, User playUser) {
    if (playUser.getRole() != UserRole.ADMIN) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    if (playUser.isActive() != true) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (requestDto.getCategoryName() == null ||
        requestDto.getCategoryName().isBlank()) {
      throw new InvalidStateException("カテゴリー名は必須項目です。");
    }
    if (requestDto.getDisplayOrder() == null) {
      throw new InvalidStateException("displayOrderは必須項目です。");
    }
    return true;
  }

  private boolean canUpdateCategory(CategoryRequestDto requestDto, User playUser) {
    if (playUser.getRole() != UserRole.ADMIN) {
      throw new UnauthorizedException("権限が不足しています。");
    }
    if (playUser.isActive() != true) {
      throw new UnauthorizedException("有効なユーザーではありません。");
    }
    if (requestDto.getCategoryName() == null ||
        requestDto.getCategoryName().isBlank()) {
      throw new InvalidStateException("カテゴリー名は必須項目です。");
    }
    if (requestDto.getDisplayOrder() == null) {
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

}
