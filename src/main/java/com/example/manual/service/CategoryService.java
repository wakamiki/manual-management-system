package com.example.manual.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.manual.dto.CategoryRequestDto;
import com.example.manual.dto.CategoryResponseDto;
import com.example.manual.entity.Category;
import com.example.manual.entity.User;
import com.example.manual.enums.UserRole;
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
  public List<CategoryResponseDto> showCategoryManagement(
      Principal principal) {
    log.info("start");
      if (!canShowCategoryManagement(principal)) {
        throw new InvalidStateException("判定エラー");
      }
  List<Category> categories =
      categoryRepository.findAllByOrderByDisplayOrderAsc();
  List<CategoryResponseDto>responseDtos =
      new ArrayList<>();
  for (Category category : categories) {
    CategoryResponseDto responseDto = new CategoryResponseDto();
    responseDto.setActive(category.isActive());
    responseDto.setCategoryName(category.getCategoryName());
    responseDto.setDisplayOrder(category.getDisplayOrder());
    responseDto.setId(category.getId());
    responseDto.setUpdatedAt(category.getUpdatedAt());
    responseDtos.add(responseDto);
  }
  return responseDtos;
}

// ============================================
// 登録・更新
// ============================================

  public void createCategory(
      CategoryRequestDto requestDto,
      Principal principal) {
    log.info("start");
    int targetOrder = requestDto.getDisplayOrder();
    shiftUpOrderNumbers(targetOrder, null);
    if(!isAdmin(principal)){
      throw new  UnauthorizedException("権限が不足しています。");
    }
    if (isCategoryNameTaken(requestDto.getCategoryName())) {
       //"分岐メッセージ:同名のカテゴリーがあります。そのまま登録しますか？";
       //TODO:分岐機構検討
    }
    Category category = new Category();
    category.setCategoryName(requestDto.getCategoryName());
    category.setDisplayOrder(targetOrder);
    category.markCreatedNow();
    category.markUpdatedNow();
    category.markActive();
    categoryRepository.save(category);

    //情報書き換え対応時に使用。
    // CategoryResponseDto responseDto = new CategoryResponseDto();
    // responseDto.setId(savedCategory.getId());
    // responseDto.setCategoryName(savedCategory.getCategoryName());
    // responseDto.setDisplayOrder(savedCategory.getDisplayOrder());
    // responseDto.setActive(savedCategory.isActive());
    // return  responseDto;
  }

  public void updateCategory(
      Long categoryId,
      CategoryRequestDto requestDto,
      Principal principal) {
    log.info("start");
    Category category = findCategoryOrThrow(categoryId);
    int currentOrder = category.getDisplayOrder();
    int targetOrder = requestDto.getDisplayOrder();
    if(!isAdmin(principal)){
      throw new  UnauthorizedException("権限が不足しています。");
    }
    if (isCategoryNameTaken(requestDto.getCategoryName())) {
       //"分岐メッセージ:同名のカテゴリーがあります。そのまま登録しますか？";
       //TODO:分岐機構検討
    }
    if (targetOrder < currentOrder) {
      shiftUpOrderNumbers(targetOrder, currentOrder - 1);
    } else if (targetOrder > currentOrder) {
      shiftDownOrderNumbers(currentOrder + 1, targetOrder);
    }

    category.setCategoryName(requestDto.getCategoryName());
    category.setDisplayOrder(targetOrder);
    category.markUpdatedNow();
    categoryRepository.save(category);
    // 情報書き換え対応時に使用。
    // CategoryResponseDto responseDto = new CategoryResponseDto();
    // responseDto.setId(savedCategory.getId());
    // responseDto.setCategoryName(savedCategory.getCategoryName());
    // responseDto.setDisplayOrder(savedCategory.getDisplayOrder());
    // responseDto.setActive(savedCategory.isActive());
  }

  public void deactivateCategory(Principal principal) {
    log.info("start");
  if (!isAdmin(principal)) {
    throw new UnauthorizedException("権限が不足しています。");
  }

    Category category = new Category();

    category.markInactive();
    category.markUpdatedNow();
    categoryRepository.save(category);
    // 情報書き換え対応時に使用。
    // CategoryResponseDto responseDto = new CategoryResponseDto();
    // responseDto.setActive(savedCategory.isActive());
    // responseDto.setUpdatedAt(savedCategory.getUpdatedAt());
  }

  public void activateCategory(Principal principal) {
    log.info("start");
  if(!isAdmin(principal)){
      throw new  UnauthorizedException("権限が不足しています。");
    }
    Category category = new Category();
    category.markActive();
    category.markUpdatedNow();
    categoryRepository.save(category);
    // 情報書き換え対応時に使用。
    // CategoryResponseDto responseDto = new CategoryResponseDto();
    // responseDto.setActive(savedCategory.isActive());
    // responseDto.setUpdatedAt(savedCategory.getUpdatedAt());
    // return responseDto;
}


//===============================================
//取得系
// ===============================================

public List<Category> getAllCategories() {
  log.info("start");
  return categoryRepository.findAllByOrderByCategoryNameAsc();
  }

  public Category getCategoryById(Long categoryId) {
    log.info("start");
  Optional<Category>categoryOpt = categoryRepository.findById(categoryId);
 if (categoryOpt.isEmpty()) {
      throw new RuntimeException("指定されたカテゴリーは存在しません");
    }
    return categoryOpt.get();
  }

  //index 検索欄のカテゴリーリスト取得
  public List<CategoryResponseDto> getCategoryDtos() {
    log.info("start");
  List<Category> categoryAll = categoryRepository.findAllByOrderByDisplayOrderAsc();
  List<CategoryResponseDto> responseDtos = new ArrayList<>();
  for (Category category : categoryAll) {
    CategoryResponseDto responseDto = new CategoryResponseDto();
    responseDto.setActive(category.isActive());
    responseDto.setId(category.getId());
    responseDto.setCategoryName(category.getCategoryName());
    responseDtos.add(responseDto);
  }
  return responseDtos;
}

//displayオーダー割り込み
private void shiftDownOrderNumbers(Integer start, Integer endInclusive) {
  // new を挿入するために範囲をずらす
  log.info("start");
 if (start == null) {
    return;
  }
  Integer end = endInclusive;
  if (end == null) {
    Optional<Category>categoryOpt = categoryRepository.findTopByOrderByDisplayOrderDesc();
    if (categoryOpt.isEmpty()) {
        throw new NotFoundException("カテゴリ最大値が取得できませんでした。");
      }
      Integer categoryEnd = categoryOpt.get().getDisplayOrder();
      end = categoryEnd;
  }
  if (end < start) {
    return;
  }
  List<Category> targets =
      categoryRepository.findByDisplayOrderBetweenOrderByDisplayOrderAsc(
          start, end);

  for (Category target : targets) {
    target.setDisplayOrder(target.getDisplayOrder() + 1);
  }
  categoryRepository.saveAll(targets);
}

//displayオーダー割り込み
private void shiftUpOrderNumbers(Integer start, Integer end) {
  // new を挿入するために範囲をずらす
  log.info("start");
  if (start == null || end == null || end < start) {
    return;
  }
  List<Category> targets = categoryRepository.findByDisplayOrderBetweenOrderByDisplayOrderAsc(
      start, end);

  for (Category target : targets) {
    target.setDisplayOrder(target.getDisplayOrder() - 1);
  }
  categoryRepository.saveAll(targets);
}


// ===============================================
//権限判定
// ===============================================

  //adminのみ
  private boolean isAdmin(Principal principal) {
    log.info("start");
  User targetUser = userService.getUserByPrincipal(principal);
  if(targetUser.getRole()!=UserRole.ADMIN){
    throw new UnauthorizedException("権限が不足しています。");
  }
    return true;
  }

//カテゴリー名同名チェック
private boolean isCategoryNameTaken(String categoryName) {
  log.info("start");
  List<Category> categoryList = getAllCategories();
  for (Category category : categoryList) {
    if (categoryName.equals(category.getCategoryName())) {
      return true;
      //"分岐メッセージ:同名のカテゴリーがあります。そのまま登録しますか？";
    }
  }
  return false;
}

private boolean canShowCategoryManagement(Principal principal) {
  log.info("start");
  User targetUser = userService.getUserByPrincipal(principal);
  if (targetUser.getRole() != UserRole.ADMIN) {
    throw new UnauthorizedException("権限が不足しています。");
  }
  if (targetUser.isActive() != true) {
    throw new UnauthorizedException("有効なユーザーではありません。");
  }
  return true;
}

// ===============================================
//共通処理
// ===============================================

private Category findCategoryOrThrow(Long categoryId) {
  log.info("start");
  Optional<Category> categoryOpt =
    categoryRepository.findById(categoryId);
  if (categoryOpt.isEmpty()) {
    throw new RuntimeException("カテゴリが見つかりません。");
  }
  return categoryOpt.get();
}

}
