package com.example.manual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.manual.entity.ManualHistory;

public interface ManualHistoryRepository extends JpaRepository<ManualHistory, Long> {

  //manualIDで履歴取得(更新履歴昇順)
  List<ManualHistory> findByManual_IdOrderByChangedAtDesc(Long manualId);

 //一覧取得changedAtの降順
  List<ManualHistory> findAllByOrderByChangedAtDesc();

}
