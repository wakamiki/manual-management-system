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

  public void submitManual(Long manualId) {
    Manual manual = findManualOrThrow(manualId);
    manual.markUpdatedNow();
    manual.submitPENDING();
    manualRepository.save(manual);
}


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

  public void editToPending(Long manualId,ManualRequestDto requestDto,Principal principal){
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
    Manual savedManual = manualRepository.save(manual);
    manualHistoryService.createHistory(savedManual, requestDto.getChangeNote(),principal);  
  }  

//#endregion

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

  public ManualResponseDto goToEditPage(Long manualId, Principal principal) {
  Manual manual = findManualOrThrow(manualId);
  return toManualFormInputDto(manual);
    }

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

//#region 
//#endregion
//#region 

private boolean canArchiveManual(User user, Manual manual,Category category,String changeNote){
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
User user =userService.getUserByPrincipal(principal);
if(user.getRole()!=UserRole.ADMIN&&user.getRole()!=UserRole.APPROVER){
    throw new UnauthorizedException("権限が不足しています。");
}
if (!user.isActive()) {
    throw new UnauthorizedException("有効なユーザーではありません。");
}
if (manual.getStatus()!=ManualStatus.PENDING) {
    throw new InvalidStateException("有効なユーザーではありません。");
}
if (Objects.equals(manual.getUser().getId(), user.getId())) {
    throw new InvalidStateException("承認ができるのはステータス:PENDINGのマニュアルのみです。");
}
if (changeNote==null||changeNote.isBlank()) {
  throw new NotFoundException("自分が作成したマニュアルの承認をすることは出来ません。");
}
return true;
}

private boolean canRestoreManual(Manual manual,Principal principal,String changeNote){
  User user =userService.getUserByPrincipal(principal);
  if(user.getRole()!=UserRole.ADMIN&&user.getRole()!=UserRole.APPROVER){
    throw new UnauthorizedException("");
}
if (!user.isActive()) {
    throw new UnauthorizedException("");
}
if (manual.getStatus()!=ManualStatus.ARCHIVED) {
    throw new InvalidStateException("");
}
if (!manual.getCategory().isActive()) {
    throw new InvalidStateException("");
}
if (changeNote==null||changeNote.isBlank()) {
  throw new NotFoundException("");
}
  return true;
}

  // Permission check skeletons (shared helpers)
  private void ensureApproverOrAdmin(User user) {
    if (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.APPROVER) {
      throw new UnauthorizedException("");
    }
  }

  private void ensureActiveUser(User user) {
    if (!user.isActive()) {
      throw new UnauthorizedException("指定したユーザーが存在しません");
    }
  }

  private void ensureStatusIn(Manual manual, ManualStatus... statuses) {
    for (ManualStatus status : statuses) {
      if (manual.getStatus() == status) {
        return;
      }
    }
    throw new InvalidStateException("指定したユーザーが存在しません");
  }

  private void ensureNotCreator(User user, Manual manual) {
    if (Objects.equals(manual.getUser().getId(), user.getId())) {
      throw new InvalidStateException("");
    }
  }

  private void ensureCategoryActive(Category category) {
    if (!category.isActive()) {
      throw new InvalidStateException("");
    }
  }

  private void ensureChangeNoteRequired(String changeNote) {
    if (changeNote == null || changeNote.isBlank()) {
      throw new NotFoundException("");
    }
  }

  private void ensureManualHasContent(Manual manual) {
    if (manual.getTitle() == null || manual.getContent() == null
      || manual.getTitle().isBlank() || manual.getContent().isBlank()) {
      throw new NotFoundException("");
    }
  }
//#endregion
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
      throw new RuntimeException("");
    }
    return manualOpt.get();
 }
//#endregion
}
