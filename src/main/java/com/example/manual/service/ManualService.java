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
  public ManualResponseDto submitManual(Long manualId) {
    Manual manual = findManualOrThrow(manualId);
    manualRepository.save(manual);

    ManualResponseDto responseDto = new ManualResponseDto();

    return responseDto;
  }
    

  

  //編集予定　新規作成（DRAFT）ユーザー関係未実装　履歴作成必須
  public void copyDraftManual(ManualCopyRequestDto requestDto) {

  }

    //編集予定 ユーザー関係未実装
  public void updateManual(Long manualId) {
    Manual manual = findManualOrThrow(manualId);
   
  }

    //編集予定 ユーザー関係未実装
  public void approveManual(Long manualId) {
    Manual manual = findManualOrThrow(manualId);

  }

    //編集予定 更新履歴必須 ユーザー関係未実装
  public void rollbackManual(Long manualId,ManualRequestDto requestDto) {
    Manual manual = findManualOrThrow(manualId);
    manual.rollbackToDraft();
    manualRepository.save(manual);
  }

    //編集予定　ユーザー関係未実装　更新履歴必須
  public void archiveManual(Long manualId) {
      Manual manual = findManualOrThrow(manualId);
    manualRepository.save(manual);
    }

    //編集予定 ユーザー関係未実装
//カテゴリーが同カテゴリーでアクティブ状態のときのみ復元可能の機能未実装
  public void restoreManual(Long manualId) {
      Manual manual = findManualOrThrow(manualId);
      manualRepository.save(manual);
    }

  public ManualDetailDto getManualDetail(Long manualId){
    Manual manual = findManualOrThrow(manualId);
    ManualDetailDto detailDto = new ManualDetailDto();
    detailDto.setManualId(manual.getId());
    detailDto.setCategoryName(manual.getCategory().getCategoryName());
    detailDto.setTitle(manual.getTitle());
    detailDto.setContent(manual.getContent());
    detailDto.setStatus(manual.getStatus());
    detailDto.setCreatedAt(manual.getCreatedAt());
    detailDto.setUpdatedAt(manual.getUpdatedAt());
    //ManualHistoryからマニュアルヒストリーをマニュアルIDで呼ぶ
    return detailDto;
  }

//履歴取得の窓口　manualIdを起点に履歴を返す
  public void getManualHistories(Long id){
    List<ManualHistory> getHistory = manualHistoryService.getManualIdHistory(id);

  }

  public ManualResponseDto getManualForEdit(Long manualId){
    Manual manual = findManualOrThrow(manualId);
    ManualResponseDto responseDto = new ManualResponseDto();
    responseDto.setManualId(manual.getId());
    responseDto.setTitle(manual.getTitle());
    responseDto.setContent(manual.getContent());
    responseDto.setCategoryName(manual.getCategory().getCategoryName());
    responseDto.setUpdatedAt(manual.getUpdatedAt());
    //displayNameとマニュアルヒストリー必要
    return responseDto;
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
