package com.example.manual.service;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.manual.entity.Manual;
import com.example.manual.entity.ManualHistory;
import com.example.manual.entity.User;
import com.example.manual.repository.ManualHistoryRepository;
import com.example.manual.repository.UserRepository;

@Service
public class ManualHistoryService {

  public final ManualHistoryRepository manualHistoryRepository;
  public final UserRepository userRepository;
  public final UserService userService;

    public ManualHistoryService(ManualHistoryRepository manualHistoryRepository,UserRepository userRepository,UserService userService){
      this.manualHistoryRepository = manualHistoryRepository;
      this.userRepository = userRepository;
      this.userService = userService;
  }

//null可　ユーザー追加予定
public ManualHistory createHistory(Manual manual,String changeNote,Principal principal){
  ManualHistory history = new ManualHistory();
  history.setChangeNote(changeNote);
  history.markChangedNow();
  User changedByUser = userService.getUserByloginId(principal.getName());
  history.setChangedByUser(changedByUser);
  history.setManual(manual);
  return manualHistoryRepository.save(history);
}

//チェンジノート必須作成予定

//manualIDで紐づいた履歴を全て取得(更新履歴昇順)
public List<ManualHistory> getManualIdHistory(Long manualId) {
  return manualHistoryRepository.findByManual_IdOrderByChangedAtDesc(manualId);
}

public List<ManualHistory> getAllHistories(){
  return manualHistoryRepository.findAllByOrderByChangedAtDesc();
}
}
