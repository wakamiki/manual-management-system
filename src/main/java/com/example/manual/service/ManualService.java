package com.example.manual.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.manual.dto.ManualCopyRequestDto;
import com.example.manual.dto.ManualDetailDto;
import com.example.manual.dto.ManualRequestDto;
import com.example.manual.dto.ManualResponseDto;
import com.example.manual.entity.Manual;
import com.example.manual.entity.ManualHistory;
import com.example.manual.enums.ManualStatus;
import com.example.manual.repository.CategoryRepository;
import com.example.manual.repository.ManualRepository;

@Service
public class ManualService {

  private final ManualRepository manualRepository;
  private final CategoryRepository categoryRepository;
  private final ManualHistoryService manualHistoryService;
  private final CategoryService manualCategoryService;

  public ManualService(ManualRepository manualRepository
    ,CategoryRepository categoryRepository
    ,ManualHistoryService manualHistoryService
    ,CategoryService manualCategoryService) {
    this.manualRepository = manualRepository;
    this.categoryRepository = categoryRepository;
    this.manualHistoryService = manualHistoryService;
    this.manualCategoryService = manualCategoryService;
  }

  public void createDraftManual(ManualRequestDto requestDto) {
 
  }

  // Responseにユーザー名とid未実装　権限チェック(ログイン中ユーザーか)
  public void createAndSubmitManual(ManualRequestDto requestDto) {
   
  }
  //下書きから公開へ変更するだけの処理 対応ボタン不明　要確認
  public ManualResponseDto submitManual(Long id) {
    Manual manual = findManualOrThrow(id);
    manualRepository.save(manual);

    ManualResponseDto responseDto = new ManualResponseDto();

    return responseDto;
  }
    

  

  //編集予定　新規作成（DRAFT）ユーザー関係未実装　履歴作成必須
  public void copyDraftManual(ManualCopyRequestDto requestDto) {

  }

    //編集予定 ユーザー関係未実装
  public void updateManual(Long id) {
    Manual manual = findManualOrThrow(id);
   
  }

    //編集予定 ユーザー関係未実装
  public void approveManual(Long id) {
    Manual manual = findManualOrThrow(id);

  }

    //編集予定 更新履歴必須 ユーザー関係未実装
  public void rollbackManual(Long id,ManualRequestDto requestDto) {
    Manual manual = findManualOrThrow(id);
    manual.rollbackToDraft();
    manualRepository.save(manual);
  }

    //編集予定　ユーザー関係未実装　更新履歴必須
  public void archiveManual(Long id) {
      Manual manual = findManualOrThrow(id);
    manualRepository.save(manual);
    }

    //編集予定 ユーザー関係未実装
//カテゴリーが同カテゴリーでアクティブ状態のときのみ復元可能の機能未実装
  public void restoreManual(Long id) {
      Manual manual = findManualOrThrow(id);
      manualRepository.save(manual);
    }

  public ManualResponseDto getManualDetail(Long manualId){
    Manual manual = findManualOrThrow(manualId);
    ManualDetailDto detailDto = new ManualDetailDto();
    detailDto.set
  }

//履歴取得の窓口　manualIdを起点に履歴を返す
  public void getManualHistories(Long id){
    List<ManualHistory> getHistory = manualHistoryService.getManualIdHistory(id);

  }

    //マニュアルを直接返す形になっている編集予定
  public List<Manual> getAllManuals() {
      return manualRepository.findAllByOrderByUpdatedAtDesc();
    }

    // マニュアルを直接返す形になっている編集予定
  public List<Manual> searchByTitle(String keyword) {
    return manualRepository.findByTitleContainingOrderByUpdatedAtDesc(keyword);
  }


  public List<Manual> searchByStatus(ManualStatus status) {
    return manualRepository.findByStatusOrderByUpdatedAtDesc(status);
  }

  //小メソッド群
  private Manual findManualOrThrow(Long id) {
    Optional<Manual> manualOpt = manualRepository.findById(id);
    if (manualOpt.isEmpty()) {
      throw new RuntimeException("指定されたマニュアルは存在しません");
    }
    return manualOpt.get();
 }
}
