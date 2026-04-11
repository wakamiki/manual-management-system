package com.example.manual.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.manual.dto.ManualActionRequestDto;
import com.example.manual.dto.ManualDetailDto;
import com.example.manual.dto.ManualListDto;
import com.example.manual.dto.ManualRequestDto;
import com.example.manual.dto.ManualResponseDto;
import com.example.manual.dto.ManualSearchConditionDto;
import com.example.manual.entity.Category;
import com.example.manual.entity.Manual;
import com.example.manual.entity.ManualHistory;
import com.example.manual.entity.User;
import com.example.manual.enums.ManualStatus;
import com.example.manual.enums.UserRole;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.exception.NotFoundException;
import com.example.manual.exception.UnauthorizedException;
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


  //新規作成下書き保存　対応ボタン　下書き保存
  public void saveDraftForCreate(Long manualId,ManualRequestDto requestDto,Principal principal){
    Manual manual = findManualOrThrow(manualId);
    Category category = categoryService.getCategoryById(requestDto.getCategoryId());
    User user = userService.getUserByPrincipal(principal);
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
    User user = userService.getUserByPrincipal(principal);
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
    User user = userService.getUserByPrincipal(principal);
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
    User user = userService.getUserByPrincipal(principal);
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
      return detailDto; //TODO: チェンジノート　リストにする
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

//#endregion
//#region action
//TODO: 通知実装予定
  //対応ボタン　承認（チェンジノート無）
  public void approveManual(Long manualId, Principal principal) {
    Manual manual = findManualOrThrow(manualId);
    if (!canApproveManual(manual, manual.getCategory(), principal)) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.approve();
    manual.markUpdatedNow();
    manual.markApprovedNow();
    manualRepository.save(manual);
  }

  //ifで更新履歴あり（空チェック、クリエイト）の分岐をする。//TODO: 通知実装予定
  //対応ボタン　承認（チェンジノート有）
  public void approveManualWithComment(Long manualId,String changeNote,Principal principal) {
    Manual manual = findManualOrThrow(manualId);
    if (!canApproveManual(manual, manual.getCategory(), principal)) {
      throw new UnauthorizedException("判定エラー");
    }
    manual.approve();
    manual.markUpdatedNow();
    manual.markApprovedNow();
    Manual savedManual = manualRepository.save(manual);
    if(changeNote==null||changeNote.isBlank()){
    }else{
    manualHistoryService.createHistory(savedManual,changeNote,principal);
    }
  }

  //対応ボタン　差し戻し（チェンジノート必須）　//TODO: 通知実装予定
  public void rollbackEditManual(Long manualId,String changeNote,Principal principal) {
  Manual manual = findManualOrThrow(manualId);
  if (!canRollbackManual(manual, principal, changeNote)) {
    throw new UnauthorizedException("判定エラー");
  }
  manual.markUpdatedNow();
  manual.rollbackToDraft();
  Manual savedManual = manualRepository.save(manual);
  manualHistoryService.createHistory(savedManual,changeNote,principal); 
  }

  //対応ボタン　アーカイブ
  public void archiveManual(Long manualId,ManualActionRequestDto actionRequestDto,Principal principal){
    Manual manual = findManualOrThrow(manualId);
    User user = userService.getUserByPrincipal(principal);
    Category category = categoryService.getCategoryById(manual.getCategory().getId());
    if(!canArchiveManual(user,manual,category,actionRequestDto.getChangeNote())){
      throw new UnauthorizedException("判定エラー");
    }
    manual.archive();
    manual.markUpdatedNow();
    Manual savedManual = manualRepository.save(manual);
    manualHistoryService.createHistory(savedManual,actionRequestDto.getChangeNote(),principal);
  }

  //対応ボタン　復帰
  public void restoreManual(Long manualId,String changeNote,Principal principal) {
  Manual manual = findManualOrThrow(manualId);
  if (!canRestoreManual(manual, principal, changeNote)) {
      throw new UnauthorizedException("判定エラー");
  }
  manual.restoreToApproved();
  manual.markUpdatedNow();
  Manual savedManual = manualRepository.save(manual);
  manualHistoryService.createHistory(savedManual,changeNote,principal);
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

private boolean canArchiveManual(User user, Manual manual,Category category,String changeNote){
//ロール権限　ログイン中　ステータスDRAFT/PENDING/APPROVED　アクティブカテゴリ　チェンジノート必須　タイトル・コンテンツ空白不可
if(user.getRole()!=UserRole.ADMIN&&user.getRole()!=UserRole.APPROVER){
  throw new UnauthorizedException("権限が不足しています。");
}
if (!user.isActive()) {
  throw new UnauthorizedException("有効なユーザーではありません。");
}
if(manual.getStatus()!=ManualStatus.DRAFT&&manual.getStatus()!=ManualStatus.PENDING&&manual.getStatus()!=ManualStatus.APPROVED){
  throw new InvalidStateException("マニュアルのステータスが条件を満たしていません。");
}
if(!category.isActive()){
  throw new UnauthorizedException("指定カテゴリはアクティブではありません。");
}
if (changeNote==null||changeNote.isBlank()) {
  throw new NotFoundException("更新履歴が見つかりません。");
}
if(manual.getTitle()==null||manual.getContent()==null||manual.getTitle().isBlank()||manual.getContent().isBlank()){
  throw new NotFoundException("タイトル・コンテンツがありません。");
}
return true;
}

//TODO: 通知実装予定
private boolean canApproveManual(Manual manual,Category category,Principal principal){
//APPROVER / ADMIN　isActive必須　PEDINGのみ 作成者本人不可　使用停止カテゴリ不可　
User user =userService.getUserByPrincipal(principal);
if(user.getRole()!=UserRole.ADMIN&&user.getRole()!=UserRole.APPROVER){
    throw new UnauthorizedException("権限が不足しています。");
}
if (!user.isActive()) {
    throw new UnauthorizedException("有効なユーザーではありません。");
}
if (manual.getStatus()!=ManualStatus.PENDING) {
    throw new InvalidStateException("承認ができるのはステータス:PENDINGのマニュアルのみです。");
}
if (Objects.equals(manual.getUser().getId(), user.getId())) {
    throw new InvalidStateException("自分が作成したマニュアルの承認をすることは出来ません。");
}
if (!category.isActive()) {
    throw new InvalidStateException("有効でないカテゴリーでは承認することが出来ません。");
}
return true;
}

private boolean canRollbackManual(Manual manual,Principal principal,String changeNote){
// APPROVER / ADMIN  isActive PENDINGのみ（PENDING→DRAFT）　作成者本人不可　チェンジノート必須
User user =userService.getUserByPrincipal(principal);
if(user.getRole()!=UserRole.ADMIN&&user.getRole()!=UserRole.APPROVER){
    throw new UnauthorizedException("権限が不足しています。");
}
if (!user.isActive()) {
    throw new UnauthorizedException("有効なユーザーではありません。");
}
if (manual.getStatus()!=ManualStatus.PENDING) {
    throw new InvalidStateException("承認ができるのはステータス:PENDINGのマニュアルのみです。");
}
if (Objects.equals(manual.getUser().getId(), user.getId())) {
    throw new InvalidStateException("自分が作成したマニュアルの承認をすることは出来ません。");
}
if (changeNote==null||changeNote.isBlank()) {
  throw new NotFoundException("更新履歴が見つかりません。");
}
return true;
}

private boolean canRestoreManual(Manual manual,Principal principal,String changeNote){
//APPROVER / ADMIN isActive ARCHIVEDのみ　アクティブカテゴリ　チェンジノート必須
  User user =userService.getUserByPrincipal(principal);
  if(user.getRole()!=UserRole.ADMIN&&user.getRole()!=UserRole.APPROVER){
    throw new UnauthorizedException("権限が不足しています。");
}
if (!user.isActive()) {
    throw new UnauthorizedException("有効なユーザーではありません。");
}
if (manual.getStatus()!=ManualStatus.ARCHIVED) {
    throw new InvalidStateException("復帰が行えるのはステータス:ARCHIVEDのマニュアルのみです。");
}
if (!manual.getCategory().isActive()) {
    throw new InvalidStateException("有効でないカテゴリーでは承認することが出来ません。");
}
if (changeNote==null||changeNote.isBlank()) {
  throw new NotFoundException("更新履歴が見つかりません。");
}
  return true;
}

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
