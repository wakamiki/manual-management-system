package com.example.manual.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.manual.dto.ManualDetailHistoryDto;
import com.example.manual.dto.ManualHistoryDto;
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

public ManualHistory createHistory(Manual manual,String changeNote,Principal principal){
  ManualHistory history = new ManualHistory();
  history.setChangeNote(changeNote);
  history.markChangedNow();
  User changedByUser = userService.getUserByPrincipal(principal);
  history.setChangedByUser(changedByUser);
  history.setManual(manual);
  return manualHistoryRepository.save(history);
}

//繝√ぉ繝ｳ繧ｸ繝弱・繝亥ｿ・井ｽ懈・莠亥ｮ・

//manualID縺ｧ邏舌▼縺・◆螻･豁ｴ繧貞・縺ｦ蜿門ｾ・譖ｴ譁ｰ螻･豁ｴ譏・・
public List<ManualHistory> getManualIdHistory(Long manualId) {
  return manualHistoryRepository.findByManual_IdOrderByChangedAtDesc(manualId);
}

public List<ManualHistory> getAllHistories(){
  return manualHistoryRepository.findAllByOrderByChangedAtDesc();
}

//荳隕ｧ陦ｨ遉ｺ逕ｨ
public List<ManualHistoryDto>getManualHistorySummaryDtoList(Long manualId){
  List<ManualHistory>manualHistories = this.getManualIdHistory(manualId);
  List<ManualHistoryDto>historyDtoList = new ArrayList<>();
  for (ManualHistory history :  manualHistories) {
    ManualHistoryDto historyDto = new ManualHistoryDto(); 
    historyDto.setChangeNote(history.getChangeNote());
    historyDto.setChangedAt(history.getChangedAt());
    historyDtoList.add(historyDto);
  }
  return historyDtoList;
}
//隧ｳ邏ｰ陦ｨ遉ｺ逕ｨ
public List<ManualDetailHistoryDto>getManualHistoryDetailDtoList(Long manualId){
  List<ManualHistory>manualHistories = this.getManualIdHistory(manualId);
  List<ManualDetailHistoryDto>historyDetailDtoList = new ArrayList<>();
  for (ManualHistory history :  manualHistories) {
    ManualDetailHistoryDto historyDetailDto = new ManualDetailHistoryDto(); 
    historyDetailDto.setChangeNote(history.getChangeNote());
    historyDetailDto.setChangedAt(history.getChangedAt());
    historyDetailDto.setChangedByUserName(history.getChangedByUser().getDisplayName());
    historyDetailDtoList.add(historyDetailDto);
  }
  return  historyDetailDtoList;
}
}
  //#region この分け方をすると分かりやすい
  //#endregion
