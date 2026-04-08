package com.example.manual.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.manual.dto.ManualResponseDto;
import com.example.manual.entity.Manual;
import com.example.manual.entity.ManualHistory;
import com.example.manual.repository.ManualHistoryRepository;

@Service
public class ManualHistoryService {

    public final ManualHistoryRepository manualHistoryRepository;

    public ManualHistoryService(ManualHistoryRepository manualHistoryRepository){
    this.manualHistoryRepository = manualHistoryRepository;
  }

//null可　ユーザー追加予定
public ManualResponseDto createHistory(Manual manual,String changeNote){
  ManualHistory history = new ManualHistory();
  history.setChangeNote(changeNote);
  history.setManual(manual);
  history.markChangedNow();
  manualHistoryRepository.save(history);
  ManualResponseDto responseDto = new ManualResponseDto();
  responseDto.setChangeNote(history.getChangeNote());
  responseDto.setChangedAt(history.getChangedAt());
  return responseDto;
}

//チェンジノート必須作成予定

//manualIDで紐づいた履歴を全て取得(更新履歴昇順)
public List<ManualHistory>getManualIdHistory(Long manualId){
  return manualHistoryRepository.findByManual_IdOrderByChangedAtDesc(manualId);
}

public List<ManualHistory> getAllHistories(){
  return manualHistoryRepository.findAllByOrderByChangedAtDesc();
}
}
