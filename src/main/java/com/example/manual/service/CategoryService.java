package com.example.manual.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.manual.entity.Category;
import com.example.manual.repository.CategoryRepository;

@Service
public class CategoryService {

public final CategoryRepository categoryRepository;

public CategoryService(CategoryRepository categoryRepository){
    this.categoryRepository = categoryRepository;
  }

public Category createCategory(Category category) {
    category.markCreatedNow();
    category.markUpdatedNow();
    //菴懈・譎ゅ・繧｢繧ｯ繝・ぅ繝也憾諷・
    category.markActive();
    category.setCategoryName(category.getCategoryName());

    Category savedCategory = categoryRepository.save(category);
    return savedCategory;
  }

public void updateCategory() {
    
  }

public void DeactivateCategory(){

  }

public void activateCategory(){

}

public void findAllActive(){
  
}

public List<Category> getAllCategories() {
    return categoryRepository.findAllByOrderByCategoryNameAsc();
  }

public Category getCategoryById(Long categoryId) {
Optional<Category>categoryOpt = categoryRepository.findById(categoryId);
 if (categoryOpt.isEmpty()) {
      throw new RuntimeException("謖・ｮ壹＆繧後◆繧ｫ繝・ざ繝ｪ繝ｼ縺ｯ蟄伜惠縺励∪縺帙ｓ");
    }
    return categoryOpt.get();
  }

}
  //#region この分け方をすると分かりやすい
  //#endregion
