package com.example.manual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.manual.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{

  //全件取得（カテゴリ名昇順）
  List<Category> findAllByOrderByCategoryNameAsc();
  //表示順の最大値
  java.util.Optional<Category> findTopByOrderByDisplayOrderDesc();
  //表示順の範囲取得
  List<Category> findByDisplayOrderBetweenOrderByDisplayOrderAsc(Integer start, Integer end);
   //(使用中カテゴリ名昇順)
  List<Category> findByIsActiveTrueOrderByCategoryNameAsc();
   //(旧カテゴリ名昇順)
  List<Category> findByIsActiveFalseOrderByCategoryNameAsc();
}
