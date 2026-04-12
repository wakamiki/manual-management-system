package com.example.manual.service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.manual.dto.CategoryRequestDto;
import com.example.manual.dto.CategoryResponseDto;
import com.example.manual.entity.Category;
import com.example.manual.entity.User;
import com.example.manual.enums.UserRole;
import com.example.manual.exception.NotFoundException;
import com.example.manual.exception.UnauthorizedException;
import com.example.manual.repository.CategoryRepository;

@Service
public class CategoryService {

public final CategoryRepository categoryRepository;
public final UserService userService;

public CategoryService(CategoryRepository categoryRepository,UserService userService){
    this.categoryRepository = categoryRepository;
    this.userService = userService;
  }

public CategoryResponseDto createCategory(CategoryRequestDto requestDto,Principal principal) {
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
    Category savedCategory = categoryRepository.save(category);

    CategoryResponseDto responseDto = new CategoryResponseDto();
    responseDto.setId(savedCategory.getId());
    responseDto.setCategoryName(savedCategory.getCategoryName());
    responseDto.setDisplayOrder(savedCategory.getDisplayOrder());
    responseDto.setActive(savedCategory.isActive());
    return  responseDto;
  }

public CategoryResponseDto updateCategory(Long categoryId, CategoryRequestDto requestDto,Principal principal) {
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
    Category savedCategory = categoryRepository.save(category);
    
    CategoryResponseDto responseDto = new CategoryResponseDto();
    responseDto.setId(savedCategory.getId());
    responseDto.setCategoryName(savedCategory.getCategoryName());
    responseDto.setDisplayOrder(savedCategory.getDisplayOrder());
    responseDto.setActive(savedCategory.isActive());
    return  responseDto;
  }

public CategoryResponseDto deactivateCategory(Principal principal){
    if(!isAdmin(principal)){
      throw new  UnauthorizedException("権限が不足しています。");
    }
    Category category = new Category();
    
    category.markInactive();
    category.markUpdatedNow();   
    Category savedCategory = categoryRepository.save(category);

    CategoryResponseDto responseDto = new CategoryResponseDto();
    responseDto.setActive(savedCategory.isActive());
    responseDto.setUpdatedAt(savedCategory.getUpdatedAt());
    return responseDto;
  }

public CategoryResponseDto activateCategory(Principal principal){
    if(!isAdmin(principal)){
      throw new  UnauthorizedException("権限が不足しています。");
    }
    Category category = new Category();
    category.markActive();
    category.markUpdatedNow();
    Category savedCategory = categoryRepository.save(category);

    CategoryResponseDto responseDto = new CategoryResponseDto();
    responseDto.setActive(savedCategory.isActive());
    responseDto.setUpdatedAt(savedCategory.getUpdatedAt());
    return responseDto;
}


//取得系

public List<Category> getAllCategories() {
    return categoryRepository.findAllByOrderByCategoryNameAsc();
  }

public Category getCategoryById(Long categoryId) {
Optional<Category>categoryOpt = categoryRepository.findById(categoryId);
 if (categoryOpt.isEmpty()) {
      throw new RuntimeException("指定されたカテゴリーは存在しません");
    }
    return categoryOpt.get();
  }

//displayオーダー割り込み
private void shiftDownOrderNumbers(Integer start, Integer endInclusive) {
 // new を挿入するために範囲をずらす
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
  List<Category> targets = categoryRepository.findByDisplayOrderBetweenOrderByDisplayOrderAsc(start, end);
  for (Category target : targets) {
    target.setDisplayOrder(target.getDisplayOrder() + 1);
  }
  categoryRepository.saveAll(targets);
}

//displayオーダー割り込み
private void shiftUpOrderNumbers(Integer start, Integer end) {
 // new を挿入するために範囲をずらす
  if (start == null || end == null || end < start) {
    return;
  }
  List<Category> targets = categoryRepository.findByDisplayOrderBetweenOrderByDisplayOrderAsc(start, end);
  for (Category target : targets) {
    target.setDisplayOrder(target.getDisplayOrder() - 1);
  }
  categoryRepository.saveAll(targets);
}

  public void findAllActive(){
  
}

//権限判定

  //adminのみ
private boolean isAdmin(Principal principal){
  User user = userService.getUserByPrincipal(principal);
  if(user.getRole()!=UserRole.ADMIN){
    throw new UnauthorizedException("権限が不足しています。");
  }
    return true;
  }

//カテゴリー名同名チェック
private boolean isCategoryNameTaken(String categoryName){
  List<Category>categoryList = getAllCategories();
  for (Category category : categoryList) {
    if (categoryName.equals(category.getCategoryName())) {
      return true;
      //"分岐メッセージ:同名のカテゴリーがあります。そのまま登録しますか？";
  }
}
  return false;
}

//共通処理
private Category findCategoryOrThrow(Long categoryId) {
  Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
  if (categoryOpt.isEmpty()) {
    throw new RuntimeException("カテゴリが見つかりません。");
  }
  return categoryOpt.get();
}

}

