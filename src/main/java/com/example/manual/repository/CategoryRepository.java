package com.example.manual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.manual.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{

  //全件取得（カテゴリ名昇順）
  List<Category> findAllByOrderByCategoryNameAsc();
  //(使用中カテゴリ名昇順)
  List<Category> findByIsActiveTrueOrderByCategoryNameAsc();
  //(旧カテゴリ名昇順)
  List<Category> findByIsActiveFalseOrderByCategoryNameAsc();
}
