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

  public List<Category> getAllCategories() {
    return categoryRepository.findAllByOrderByCategoryNameAsc();
  }

  public Optional<Category> getCategoryById() {
    return categoryRepository.findById();
  }

  public Category createCategory(Category category) {
    category.markCreatedNow();
    category.markUpdatedNow();
    //作成時はアクティブ状態
    category.markActive();
    category.setCategoryName(category.getCategoryName());

    Category savedCategory = categoryRepository.save(category);
    return savedCategory;
  }

}
