package com.example.manual.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.manual.entity.ManualHistory;
import com.example.manual.repository.ManualHistoryRepository;
import com.example.manual.repository.ManualRepository;
import com.example.manual.repository.UserRepository;

@Service
public class ManualHistoryService {

  private final ManualRepository manualRepository;
  public final ManualHistoryRepository manualHistoryRepository;
  public final UserRepository userRepository;

    public ManualHistoryService(ManualHistoryRepository manualHistoryRepository,UserRepository userRepository, ManualRepository manualRepository){
      this.manualHistoryRepository = manualHistoryRepository;
      this.userRepository = userRepository;
      this.manualRepository = manualRepository;
  }

//null可　ユーザー追加予定
public ManualHistory createHistory(Long manualId,String changeNote){
  ManualHistory history = new ManualHistory();
  history.setChangeNote(changeNote);
  history.markChangedNow();
  //ユーザーdisplayName
  return manualHistoryRepository.save(history);
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
