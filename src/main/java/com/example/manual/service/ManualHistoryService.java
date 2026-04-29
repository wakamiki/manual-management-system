package com.example.manual.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.manual.dto.ManualDetailHistoryDto;
import com.example.manual.dto.ManualHistoryDto;
import com.example.manual.entity.Manual;
import com.example.manual.entity.ManualHistory;
import com.example.manual.entity.User;
import com.example.manual.repository.ManualHistoryRepository;

@Service
public class ManualHistoryService {

  private static final Logger log = LoggerFactory.getLogger(ManualHistoryService.class);

  public final ManualHistoryRepository historyRepository;
  public final UserService userRepository;
  public final UserService userService;

  public ManualHistoryService(
      ManualHistoryRepository manualHistoryRepository,
      UserService userRepository,
      UserService userService) {

    this.historyRepository = manualHistoryRepository;
    this.userRepository = userRepository;
    this.userService = userService;
  }

  public ManualHistory createHistory(
      Manual manual,
      String changeNote,
      Principal principal) {

    ManualHistory history = new ManualHistory();
    history.setChangeNote(changeNote);
    history.markChangedNow();
    User changedByUser = userService.getUserByPrincipal(principal);
    history.setChangedByUser(changedByUser);
    history.setManual(manual);
    log.info("[{}][{}][PERSIST][START] action={} id={}");
    return historyRepository.save(history);
  }

  // ===========================================
  // 検索・取得
  // ===========================================

  // manualIDで紐づいた履歴を全て取得(更新履歴昇順)
  public List<ManualHistory> getManualIdHistory(Long manualId) {

    return historyRepository.findByManual_IdOrderByChangedAtDesc(
        manualId);
  }

  public List<ManualHistory> getAllHistories() {
    return historyRepository.findAllByOrderByChangedAtDesc();
  }

  // 一覧表示用 manualIDで紐づいた履歴を全て取得(更新履歴昇順)
  public List<ManualHistoryDto> getManualHistorySummaryDtoList(
      Long manualId) {

    List<ManualHistory> manualHistories = this.getManualIdHistory(manualId);
    List<ManualHistoryDto> historyDtoList = new ArrayList<>();
    for (ManualHistory history : manualHistories) {
      ManualHistoryDto historyDto = new ManualHistoryDto();
      historyDto.setChangeNote(history.getChangeNote());
      historyDto.setChangedAt(history.getChangedAt());
      historyDtoList.add(historyDto);
    }
    return historyDtoList;
  }

  // 詳細表示用
  public List<ManualDetailHistoryDto> getManualHistoryDetailDtoList(
      Long manualId) {

    List<ManualHistory> manualHistories = this.getManualIdHistory(manualId);
    log.info("[{}][FETCH]");
    List<ManualDetailHistoryDto> historyDetailDtoList = new ArrayList<>();
    for (ManualHistory history : manualHistories) {
      ManualDetailHistoryDto historyDetailDto = new ManualDetailHistoryDto();
      historyDetailDto.setChangeNote(history.getChangeNote());
      historyDetailDto.setChangedAt(history.getChangedAt());
      historyDetailDto.setChangedByUserName(
          history.getChangedByUser().getDisplayName());
      historyDetailDtoList.add(historyDetailDto);
    }
    return historyDetailDtoList;
  }

  // ===========================================
  // Dto詰め替え
  // ===========================================

  public List<ManualHistoryDto> toHistoriesDto(List<ManualHistory> histories) {

    List<ManualHistoryDto> historiesDto = new ArrayList<>();

    for (ManualHistory history : histories) {
      ManualHistoryDto historyDto = new ManualHistoryDto();
      historyDto.setChangedAt(history.getChangedAt());
      historyDto.setChangeNote(history.getChangeNote());
      historiesDto.add(historyDto);
    }
    return historiesDto;
  }

}
