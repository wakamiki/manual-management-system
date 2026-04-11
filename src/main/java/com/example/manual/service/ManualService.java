package com.example.manual.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.manual.dto.ManualDetailDto;
import com.example.manual.dto.ManualListDto;
import com.example.manual.dto.ManualRequestDto;
import com.example.manual.dto.ManualResponseDto;
import com.example.manual.dto.ManualSearchConditionDto;
import com.example.manual.entity.Category;
import com.example.manual.entity.Manual;
import com.example.manual.entity.ManualHistory;
import com.example.manual.entity.User;
import com.example.manual.repository.ManualRepository;
import com.example.manual.repository.ManualSpecification;


@Service
public class ManualService {

  private final ManualRepository manualRepository;
  private final ManualHistoryService manualHistoryService;
  private final UserService userService;
  private final CategoryService categoryService;

  public ManualService(ManualRepository manualRepository
    ,ManualHistoryService manualHistoryService
    ,UserService userService,CategoryService categoryService
    ) {
    this.manualRepository = manualRepository;
    this.manualHistoryService = manualHistoryService;
    this.userService = userService;
    this.categoryService = categoryService;
  }
//#region 公開メソッド

//対応ボタン　マニュアル公開（編集なし）
  public void submitManual(Long manualId) {
    Manual manual = findManualOrThrow(manualId);
    manual.markUpdatedNow();
    manual.submitPENDING();
    manualRepository.save(manual);
}

//role必須　対応ボタン　承認（チェンジノート無）
  public void approveManual(Long manualId, Principal principal) {
    Manual manual = findManualOrThrow(manualId);
    manual.approve();
    manual.markUpdatedNow();
    manual.markApprovedNow();
  //TODO: ロールと作成者と別人か判定を入れる
    manualRepository.save(manual);
  }

  //role必須　対応ボタン　承認（チェンジノート有）
  public void approveEditManual(Long manualId,String changeNote,Principal principal) {
    Manual manual = findManualOrThrow(manualId);
    manual.approve();
    manual.markUpdatedNow();
    manual.markApprovedNow();
  //TODO: ロールと作成者と別人か判定を入れる
    Manual savedManual = manualRepository.save(manual);
    //manualHistoryService.createHistory(savedManual,changeNote,principal);
  }

  //新規作成下書き保存　対応ボタン　下書き保存
  public void saveDraftForCreate(Long manualId,ManualRequestDto requestDto,Principal principal){
    Manual manual = findManualOrThrow(manualId);
    Category category = categoryService.getCategoryById(requestDto.getCategoryId());
    User user = userService.getUserByloginId(principal.getName());
    manual.setTitle(requestDto.getTitle());
    manual.setContent(requestDto.getContent());
    manual.markUpdatedNow();
    manual.markCreatedNow();
    manual.markStatusDRAFT();
    manual.setCategory(category);
    manual.setUser(user);
    manualRepository.save(manual);
  }

    //（DRAFT）履歴作成必須 複製下書き保存　対応ボタン　下書き保存
  public void saveDraftForCopy(Long manualId,ManualRequestDto requestDto,Principal principal) {
    Manual manual = findManualOrThrow(manualId);
    Category category = categoryService.getCategoryById(requestDto.getCategoryId());
    User user = userService.getUserByloginId(principal.getName());
    manual.setTitle(requestDto.getTitle());
    manual.setContent(requestDto.getContent());
    manual.markUpdatedNow();
    manual.markCreatedNow();
    manual.markStatusDRAFT();
    manual.setCategory(category);
    manual.setUser(user);
    Manual savedManual = manualRepository.save(manual);
    manualHistoryService.createHistory(savedManual, requestDto.getChangeNote(),principal);
  }

  //新規作成承認待ち公開　対応ボタン　マニュアルを公開
  public void submitToPending(Long manualId,ManualRequestDto requestDto,Principal principal){
    Manual manual = findManualOrThrow(manualId);
    Category category = categoryService.getCategoryById(requestDto.getCategoryId());
    User user = userService.getUserByloginId(principal.getName());
    manual.setTitle(requestDto.getTitle());
    manual.setContent(requestDto.getContent());
    manual.markCreatedNow();
    manual.markUpdatedNow();
    manual.submitPENDING();
    manual.setCategory(category);
    manual.setUser(user);
    manualRepository.save(manual);
  }

  //下書き編集から承認待ち公開　対応ボタン　マニュアルを公開
  public void editToPending(Long manualId,ManualRequestDto requestDto,Principal principal){
    Manual manual = findManualOrThrow(manualId);
    Category category = categoryService.getCategoryById(requestDto.getCategoryId());
    User user = userService.getUserByloginId(principal.getName());
    manual.setTitle(requestDto.getTitle());
    manual.setContent(requestDto.getContent());
    manual.markCreatedNow();
    manual.markUpdatedNow();
    manual.submitPENDING();//遷移　ペンディング　ドラフト→ペンディング
    manual.setCategory(category);
    manual.setUser(user);
    Manual savedManual = manualRepository.save(manual);
    //チェンジノート必須ではない
    manualHistoryService.createHistory(savedManual, requestDto.getChangeNote(),principal);  
  }  

  //対応ボタン　差し戻し（チェンジノート必須）
  public void rollbackEditManual(Long manualId,String changeNote,Principal principal) {
  Manual manual = findManualOrThrow(manualId);
  manual.markUpdatedNow();
  manual.rollbackToDraft();
 
  Manual savedManual = manualRepository.save(manual);
    //manualHistoryService.createHistory(savedManual,changeNote,principal); 
    //TODO: ロール判定
  }

  //対応ボタン　アーカイブ（チェンジノート必須）
  public void archiveManual(Long manualId,ManualRequestDto requestDto,Principal principal){
    Manual manual = findManualOrThrow(manualId);
    manual.archive();
    manual.markUpdatedNow();

    Manual savedManual = manualRepository.save(manual);
    //manualHistoryService.createHistory(savedManual,changeNote,principal); 
   //TODO: ロール判定
   //懸念点　下書きからアーカイブの時にタイトル　コンテンツが白紙の可能性
  }

//#endregion
//#region 新規タブ画面遷移

    //対応ボタン　詳細を見る(新規タブ)
  public ManualDetailDto goToDetailPage(Long manualId) {
      Manual manual = findManualOrThrow(manualId);
      List<ManualHistory>history = manualHistoryService.getManualIdHistory(manualId);
      ManualDetailDto detailDto = new ManualDetailDto();
      detailDto.setManualId(manual.getId());
      detailDto.setCategoryName(manual.getCategory().getCategoryName());
      detailDto.setTitle(manual.getTitle());
      detailDto.setContent(manual.getContent());
      detailDto.setCreatedByName(manual.getUser().getDisplayName());
      detailDto.setStatus(manual.getStatus());
      detailDto.setCreatedAt(manual.getCreatedAt());
      detailDto.setUpdatedAt(manual.getUpdatedAt());
      detailDto.setHistories(history);
      return detailDto;
    }

    //対応ボタン　編集（新規タブ）
  public ManualResponseDto goToEditPage(Long manualId, Principal principal) {
  Manual manual = findManualOrThrow(manualId);
  return toManualFormInputDto(manual);
    }

    // 対応ボタン 複製（新規タブ）
  public ManualResponseDto goToCopyPage(Long manualId, Principal principal) {
      Manual manual = findManualOrThrow(manualId);
      return toManualFormInputDto(manual);
    }

    //対応ボタン　差し戻し（新規タブ）
  public ManualResponseDto goToRollbackPage(Long manualId, Principal principal) {
      //TODO: ロール判定
  Manual manual = findManualOrThrow(manualId);
  return toManualFormInputDto(manual);
  }

    //対応ボタン　アーカイブ（新規タブ） チェンジノート必須
  public ManualResponseDto goToArchivePage(Long manualId,Principal principal) {
      //TODO: ロール判定
  Manual manual = findManualOrThrow(manualId);
  return toManualFormInputDto(manual);
  }

  //対応ボタン　復帰
  public ManualResponseDto goToRestorePage(Long manualId,Principal principal) {
  Manual manual = findManualOrThrow(manualId);
  //TODO:同一カテゴリー　アクティブ判定
  //TODO: ロール判定
  return toManualFormInputDto(manual);
    }

//#endregion
//#region 検索・取得補助

public List<ManualListDto>searchManuals(ManualSearchConditionDto condition){
  Specification<Manual> specification = Specification
  .where(ManualSpecification.containsKeyword(condition.getKeyword()))
  .and(ManualSpecification.hasCategoryIds(condition.getCategoryIds()))
  .and(ManualSpecification.hasStatuses(condition.getStatuses()));

  List<Manual> manualList = manualRepository.findAll(
    specification,
    Sort.by(Sort.Direction.DESC, "updatedAt"));
    List<ManualListDto>manualDtoList = new ArrayList<>();
  for (Manual manual : manualList) {
    ManualListDto manualDto = new ManualListDto();
    manualDto.setTitle(manual.getTitle());
    manualDto.setContent(manual.getContent());
    manualDto.setCategoryName(manual.getCategory().getCategoryName());
    manualDto.setManualId(manual.getId());
    manualDto.setStatus(manual.getStatus());
    manualDto.setUpdatedAt(manual.getUpdatedAt());
    manualDto.setCreatedByName(manual.getUser().getDisplayName());
    manualDto.setHistries(
      manualHistoryService.getManualHistorySummaryDtoList(manual.getId()));
    manualDtoList.add(manualDto);
    }
  return manualDtoList;
}

//#endregion
//#region 状態確認
//#endregion
//#region 権限確認
//#endregion
//#region Entity作成・更新補助
//#endregion
//#region DTO変換
private ManualResponseDto toManualFormInputDto(Manual manual){
ManualResponseDto responseDto = new ManualResponseDto();
  responseDto.setManualId(manual.getId());
  responseDto.setCreatedAt(manual.getCreatedAt());
  responseDto.setUpdatedAt(manual.getUpdatedAt());
  responseDto.setDysplayName(manual.getUser().getDisplayName());
  responseDto.setContent(manual.getContent());
  responseDto.setTitle(manual.getTitle());
  responseDto.setCategoryName(manual.getCategory().getCategoryName());
  return responseDto;
}

private ManualListDto toManualListDto(Manual manual) {
  ManualListDto listDto = new ManualListDto();
  listDto.setManualId(manual.getId());
  listDto.setTitle(manual.getTitle());
  listDto.setContent(manual.getContent());
  listDto.setStatus(manual.getStatus());
  listDto.setCategoryName(manual.getCategory().getCategoryName());
  listDto.setUpdatedAt(manual.getUpdatedAt());
  listDto.setCreatedByName(manual.getUser().getDisplayName());
  return listDto;
}

//#endregion
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
