package com.example.manual.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.manual.entity.ManualHistory;

public interface ManualHistoryRepository extends JpaRepository<ManualHistory, Long> {

  //manualIDで履歴取得(更新履歴昇順)
  List<ManualHistory> findByManualIdOrderByChangedAtDesc(Long manualId);

  //一覧取得cangedAtの降順
  List<ManualHistory> findAllByOrderByChangedAtDesc();

  Optional<ManualHistory> findByManualId(Long id);
}