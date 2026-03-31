package com.example.manual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.manual.entity.Manual;
import com.example.manual.entity.ManualStatus;

public interface ManualRepository extends JpaRepository<Manual, Long> {

  //一覧取得（更新が新しいもの順）
  List<Manual> findAllByOrderByUpdatedAtDesc();

  // 検索ワードが含まれる一覧取得（更新が新しいもの順）
  List<Manual> findByTitleContainingOrderByUpdatedAtDesc(String keyword);

  //status絞り込み検索機能
  List<Manual> findByStatusOrderByUpdatedAtDesc(ManualStatus status);
}
