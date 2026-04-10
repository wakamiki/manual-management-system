package com.example.manual.service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.stereotype.Service;

import com.example.manual.dto.ManualCopyRequestDto;
import com.example.manual.dto.ManualDetailDto;
import com.example.manual.dto.ManualRequestDto;
import com.example.manual.dto.ManualResponseDto;
import com.example.manual.entity.Manual;
import com.example.manual.entity.ManualHistory;
import com.example.manual.entity.User;
import com.example.manual.enums.ManualStatus;
import com.example.manual.repository.ManualRepository;

@Service
public class ManualService {

  private final ManualRepository manualRepository;
  private final ManualHistoryService manualHistoryService;
  private final UserService userService;

  public ManualService(ManualRepository manualRepository
    ,ManualHistoryService manualHistoryService,UserService userService) {
    this.manualRepository = manualRepository;
    this.manualHistoryService = manualHistoryService;
    this.userService = userService;
  }

  public void createDraftManual(ManualRequestDto requestDto) {

  }

//#region 公開メソッド

public void submitManual(Long manualId) {//対応ボタン　マニュアル公開（編集なし）
  Manual manual = findManualOrThrow(manualId);
  manual.markUpdatedNow();
  manual.submitPENDING();
  manualRepository.save(manual);
}

//role必須　対応ボタン　承認（チェンジノートなし）
public void approveManual(Long manualId,Authentication authentication) {
  Manual manual = findManualOrThrow(manualId);
  manual.approve();
  manual.markUpdatedNow();
  manual.markApprovedNow();
  //TODO: ロールと作成者と別人か判定を入れる
  manualRepository.save(manual);
}
//#endregion

//#region 新規タブ画面遷移

    //対応ボタン　詳細を見る(新規タブ)
    public ManualDetailDto goToDetailPage(Long manualId) {
      Manual manual = findManualOrThrow(manualId);
      ManualDetailDto detailDto = new ManualDetailDto();
      detailDto.setManualId(manual.getId());
      detailDto.setCategoryName(manual.getCategory().getCategoryName());
      detailDto.setTitle(manual.getTitle());
      detailDto.setContent(manual.getContent());
      detailDto.setCreatedByName(manual.getUser().getDisplayName());
      detailDto.setStatus(manual.getStatus());
      detailDto.setCreatedAt(manual.getCreatedAt());
      detailDto.setUpdatedAt(manual.getUpdatedAt());
      detailDto.setHistories(getManualHistories(manualId));
      return detailDto;
    }

    //編集（新規タブ）
    public ManualResponseDto goToEditPage(Long manualId, Principal principal) {
      Manual manual = findManualOrThrow(manualId);
      ManualResponseDto responseDto = new ManualResponseDto();
      responseDto.setContent(manual.getContent());
      responseDto.setTitle(manual.getTitle());
      responseDto.setCategoryName(manual.getCategory().getCategoryName());
      return responseDto;
    }

    // 対応ボタン 複製（新規タブ）
    public ManualResponseDto goToCopyPage(Long manualId, Principal principal) {
      Manual manual = findManualOrThrow(manualId);
      ManualResponseDto responseDto = new ManualResponseDto();
      responseDto.setContent(manual.getContent());
      responseDto.setTitle(manual.getTitle());
      responseDto.setCategoryName(manual.getCategory().getCategoryName());
      return responseDto;
    }

    //対応ボタン　差し戻し（新規タブ）
    public ManualResponseDto goToRollbackPage(Long manualId, Authentication authentication) {
      // ロール判定
      Manual manual = findManualOrThrow(manualId);
      ManualResponseDto responseDto = new ManualResponseDto();
      responseDto.setContent(manual.getContent());
      responseDto.setTitle(manual.getTitle());
      responseDto.setCategoryName(manual.getCategory().getCategoryName());
      return responseDto;
    }
    

    //対応ボタン　アーカイブ（新規タブ） チェンジノート必須
    public ManualResponseDto goToArchivePage(Long manualId,Authentication authentication) {
      // ロール判定
      Manual manual = findManualOrThrow(manualId);
      ManualResponseDto responseDto = new ManualResponseDto();
      responseDto.setContent(manual.getContent());
      responseDto.setTitle(manual.getTitle());
      responseDto.setCategoryName(manual.getCategory().getCategoryName());
      return responseDto;
    }

//#endregion
//#region 検索・取得補助
//#endregion

//#region 状態確認
//#endregion
//#region 権限確認
//#endregion
//#region Entity作成・更新補助
//#endregion
//#region DTO変換
//#endregion

    private List<ManualHistory> getManualHistories(Long manualId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getManualHistories'");
  }

  // Responseにユーザー名とid未実装　権限チェック(ログイン中ユーザーか)
  public void createAndSubmitManual(ManualRequestDto requestDto) {

  }




    //編集予定　新規作成（DRAFT）ユーザー関係未実装　履歴作成必須
  public void copyDraftManual(ManualCopyRequestDto requestDto) {

  }

    //編集予定 詰めるデータを考える
  public ManualResponseDto updateManual(Long manualId,String loginId) {
    Manual manual = findManualOrThrow(manualId);
    User user = userService.getUserByloginId(loginId);
    //save
    Manual savedManual = manualRepository.save(manual);
    User savedUser = userService.userSaved(user);
    //Dto
    ManualResponseDto responseDto = new ManualResponseDto();
    responseDto.setDysplayName(savedUser.getDisplayName());
    manual.markUpdatedNow();
    return responseDto;
  }


    //編集予定 更新履歴必須 ユーザー関係未実装
  public void rollbackManual(Long manualId,ManualRequestDto requestDto) {
    Manual manual = findManualOrThrow(manualId);
    manual.rollbackToDraft();
    manualRepository.save(manual);
  }

    //編集予定 ユーザー関係未実装
//カテゴリーが同カテゴリーでアクティブ状態のときのみ復元可能の機能未実装
  public void restoreManual(Long manualId) {
      Manual manual = findManualOrThrow(manualId);
      manualRepository.save(manual);
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

  //#region 共通処理
  private Manual findManualOrThrow(Long id) {
    Optional<Manual> manualOpt = manualRepository.findById(id);
    if (manualOpt.isEmpty()) {
      throw new RuntimeException("指定されたマニュアルは存在しません");
    }
    return manualOpt.get();
 }
 //#endregion
}
