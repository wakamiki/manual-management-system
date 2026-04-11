package com.example.manual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.manual.entity.ManualHistory;

public interface ManualHistoryRepository extends JpaRepository<ManualHistory, Long> {

  //manualID縺ｧ螻･豁ｴ蜿門ｾ・譖ｴ譁ｰ螻･豁ｴ譏・・
  List<ManualHistory> findByManual_IdOrderByChangedAtDesc(Long manualId);

  //荳隕ｧ蜿門ｾ幼hangedAt縺ｮ髯埼・
  List<ManualHistory> findAllByOrderByChangedAtDesc();

}
  //#region この分け方をすると分かりやすい
  //#endregion
