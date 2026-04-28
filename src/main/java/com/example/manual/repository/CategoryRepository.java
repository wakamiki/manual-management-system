package com.example.manual.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.manual.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  // 全件取得（カテゴリ名昇順）
  Page<Category> findAllByOrderByCategoryNameAsc(Pageable pageable);

  // 全件取得(displayOrder昇順)
  Page<Category> findAllByOrderByDisplayOrderAsc(Pageable pageable);

  // active==trueのカテゴリ全件取得(displayOrder昇順)
  List<Category> findByIsActiveTrueOrderByDisplayOrderAsc();

  // active==falseのカテゴリ全件取得(displayOrder昇順)
  List<Category> findByIsActiveFalseOrderByDisplayOrderAsc();

  // 表示順の最大値
  java.util.Optional<Category> findTopByOrderByDisplayOrderDesc();

  // 表示順の範囲取得
  List<Category> findByDisplayOrderBetweenOrderByDisplayOrderAsc(Integer start, Integer end);

  // (使用中カテゴリ名昇順)
  List<Category> findByIsActiveTrueOrderByCategoryNameAsc();

  // (旧カテゴリ名昇順)
  List<Category> findByIsActiveFalseOrderByCategoryNameAsc();

  // =====================================
  // 同名カテゴリー関係
  // =====================================

  // アクティブカテゴリで同名カテゴリーがあるかチェック
  boolean existsByCategoryNameAndIsActiveTrue(String categoryName);

  // アクティブカテゴリで自分を除いた同名カテゴリーがあるかチェック
  boolean existsByCategoryNameAndIsActiveTrueAndIdNot(String categoryName, Long id);

  // 非アクティブカテゴリで同名カテゴリーがあるかチェック
  boolean existsByCategoryNameAndIsActiveFalse(String categoryName);

  // 非アクティブカテゴリで自分を除いた同名カテゴリーがあるかチェック
  boolean existsByCategoryNameAndIsActiveFalseAndIdNot(String categoryName, Long id);

  // 同名カテゴリーで停止中のカテゴリー情報をget
  Optional<Category> findByCategoryNameAndIsActiveFalse(String categoryName);

  // 同名カテゴリーで自分を除いた停止中のカテゴリー情報をget
  Optional<Category> findByCategoryNameAndIsActiveFalseAndIdNot(String categoryName, Long id);

  // 同名カテゴリーのカテゴリー情報をget
  List<Category> findByCategoryName(String categoryName);

}
