package com.example.manual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.manual.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{

  //蜈ｨ莉ｶ蜿門ｾ暦ｼ医き繝・ざ繝ｪ蜷肴・鬆・ｼ・
  List<Category> findAllByOrderByCategoryNameAsc();
  //(菴ｿ逕ｨ荳ｭ繧ｫ繝・ざ繝ｪ蜷肴・鬆・
  List<Category> findByIsActiveTrueOrderByCategoryNameAsc();
  //(譌ｧ繧ｫ繝・ざ繝ｪ蜷肴・鬆・
  List<Category> findByIsActiveFalseOrderByCategoryNameAsc();
}
  //#region この分け方をすると分かりやすい
  //#endregion
