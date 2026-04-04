package com.example.manual.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import com.example.manual.entity.ManualHistory;
import com.example.manual.repository.ManualHistoryRepository;

@Service
public class ManualHistoryService {

    public final ManualHistoryRepository manualHistoryRepository;

    public ManualHistoryService(ManualHistoryRepository manualHistoryRepository){
    this.manualHistoryRepository = manualHistoryRepository;
  }
  
//manualIDで履歴取得(更新履歴昇順)
public List<ManualHistory>findByManualIdOrderByChangedAtDesc(Long manualId){
  return manualHistoryRepository.findByManualIdOrderByChangedAtDesc(manualId);
}

  //一覧取得cangedAtの降順
public List<ManualHistory> findAllByOrderByChangedAtDesc(ManualHistory cangedAt){
  return manualHistoryRepository.findAllByOrderByChangedAtDesc();
}

public Optional<ManualHistory> getManualHistoryById(Long id) {
  return manualHistoryRepository.findById(id);
}
}