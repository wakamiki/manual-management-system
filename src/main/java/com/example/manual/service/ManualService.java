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
    manual.submitPENDING();//驕ｷ遘ｻ縲繝壹Φ繝・ぅ繝ｳ繧ｰ縲繝峨Λ繝輔ヨ竊偵・繝ｳ繝・ぅ繝ｳ繧ｰ
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
      return detailDto; //TODO: 繝√ぉ繝ｳ繧ｸ繝弱・繝医繝ｪ繧ｹ繝医↓縺吶ｋ
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
//TODO: 騾夂衍螳溯｣・ｺ亥ｮ・
  public void approveManual(Long manualId, Principal principal) {
    Manual manual = findManualOrThrow(manualId);
    if (!canApproveManual(manual, manual.getCategory(), principal)) {
      throw new UnauthorizedException("蛻､螳壹お繝ｩ繝ｼ");
    }
    manual.approve();
    manual.markUpdatedNow();
    manual.markApprovedNow();
    manualRepository.save(manual);
  }

  public void approveManualWithComment(Long manualId,String changeNote,Principal principal) {
    Manual manual = findManualOrThrow(manualId);
    if (!canApproveManual(manual, manual.getCategory(), principal)) {
      throw new UnauthorizedException("蛻､螳壹お繝ｩ繝ｼ");
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
    throw new UnauthorizedException("蛻､螳壹お繝ｩ繝ｼ");
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
      throw new UnauthorizedException("蛻､螳壹お繝ｩ繝ｼ");
    }
    manual.archive();
    manual.markUpdatedNow();
    Manual savedManual = manualRepository.save(manual);
    manualHistoryService.createHistory(savedManual,actionRequestDto.getChangeNote(),principal);
  }

  public void restoreManual(Long manualId,String changeNote,Principal principal) {
  Manual manual = findManualOrThrow(manualId);
  if (!canRestoreManual(manual, principal, changeNote)) {
      throw new UnauthorizedException("蛻､螳壹お繝ｩ繝ｼ");
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

//#region 迥ｶ諷狗｢ｺ隱・
//#endregion
//#region 讓ｩ髯千｢ｺ隱・

private boolean canArchiveManual(User user, Manual manual,Category category,String changeNote){
if(user.getRole()!=UserRole.ADMIN&&user.getRole()!=UserRole.APPROVER){
  throw new UnauthorizedException("讓ｩ髯舌′荳崎ｶｳ縺励※縺・∪縺吶・);
}
if (!user.isActive()) {
  throw new UnauthorizedException("譛牙柑縺ｪ繝ｦ繝ｼ繧ｶ繝ｼ縺ｧ縺ｯ縺ゅｊ縺ｾ縺帙ｓ縲・);
}
if(manual.getStatus()!=ManualStatus.DRAFT&&manual.getStatus()!=ManualStatus.PENDING&&manual.getStatus()!=ManualStatus.APPROVED){
  throw new InvalidStateException("繝槭ル繝･繧｢繝ｫ縺ｮ繧ｹ繝・・繧ｿ繧ｹ縺梧擅莉ｶ繧呈ｺ縺溘＠縺ｦ縺・∪縺帙ｓ縲・);
}
if(!category.isActive()){
  throw new UnauthorizedException("謖・ｮ壹き繝・ざ繝ｪ縺ｯ繧｢繧ｯ繝・ぅ繝悶〒縺ｯ縺ゅｊ縺ｾ縺帙ｓ縲・);
}
if (changeNote==null||changeNote.isBlank()) {
  throw new NotFoundException("譖ｴ譁ｰ螻･豁ｴ縺瑚ｦ九▽縺九ｊ縺ｾ縺帙ｓ縲・);
}
if(manual.getTitle()==null||manual.getContent()==null||manual.getTitle().isBlank()||manual.getContent().isBlank()){
  throw new NotFoundException("繧ｿ繧､繝医Ν繝ｻ繧ｳ繝ｳ繝・Φ繝・′縺ゅｊ縺ｾ縺帙ｓ縲・);
}
return true;
}

//TODO: 騾夂衍螳溯｣・ｺ亥ｮ・
private boolean canApproveManual(Manual manual,Category category,Principal principal){
User user =userService.getUserByPrincipal(principal);
if(user.getRole()!=UserRole.ADMIN&&user.getRole()!=UserRole.APPROVER){
    throw new UnauthorizedException("讓ｩ髯舌′荳崎ｶｳ縺励※縺・∪縺吶・);
}
if (!user.isActive()) {
    throw new UnauthorizedException("譛牙柑縺ｪ繝ｦ繝ｼ繧ｶ繝ｼ縺ｧ縺ｯ縺ゅｊ縺ｾ縺帙ｓ縲・);
}
if (manual.getStatus()!=ManualStatus.PENDING) {
    throw new InvalidStateException("謇ｿ隱阪′縺ｧ縺阪ｋ縺ｮ縺ｯ繧ｹ繝・・繧ｿ繧ｹ:PENDING縺ｮ繝槭ル繝･繧｢繝ｫ縺ｮ縺ｿ縺ｧ縺吶・);
}
if (Objects.equals(manual.getUser().getId(), user.getId())) {
    throw new InvalidStateException("閾ｪ蛻・′菴懈・縺励◆繝槭ル繝･繧｢繝ｫ縺ｮ謇ｿ隱阪ｒ縺吶ｋ縺薙→縺ｯ蜃ｺ譚･縺ｾ縺帙ｓ縲・);
}
if (!category.isActive()) {
    throw new InvalidStateException("譛牙柑縺ｧ縺ｪ縺・き繝・ざ繝ｪ繝ｼ縺ｧ縺ｯ謇ｿ隱阪☆繧九％縺ｨ縺悟・譚･縺ｾ縺帙ｓ縲・);
}
return true;
}

private boolean canRollbackManual(Manual manual,Principal principal,String changeNote){
User user =userService.getUserByPrincipal(principal);
if(user.getRole()!=UserRole.ADMIN&&user.getRole()!=UserRole.APPROVER){
    throw new UnauthorizedException("讓ｩ髯舌′荳崎ｶｳ縺励※縺・∪縺吶・);
}
if (!user.isActive()) {
    throw new UnauthorizedException("譛牙柑縺ｪ繝ｦ繝ｼ繧ｶ繝ｼ縺ｧ縺ｯ縺ゅｊ縺ｾ縺帙ｓ縲・);
}
if (manual.getStatus()!=ManualStatus.PENDING) {
    throw new InvalidStateException("謇ｿ隱阪′縺ｧ縺阪ｋ縺ｮ縺ｯ繧ｹ繝・・繧ｿ繧ｹ:PENDING縺ｮ繝槭ル繝･繧｢繝ｫ縺ｮ縺ｿ縺ｧ縺吶・);
}
if (Objects.equals(manual.getUser().getId(), user.getId())) {
    throw new InvalidStateException("閾ｪ蛻・′菴懈・縺励◆繝槭ル繝･繧｢繝ｫ縺ｮ謇ｿ隱阪ｒ縺吶ｋ縺薙→縺ｯ蜃ｺ譚･縺ｾ縺帙ｓ縲・);
}
if (changeNote==null||changeNote.isBlank()) {
  throw new NotFoundException("譖ｴ譁ｰ螻･豁ｴ縺瑚ｦ九▽縺九ｊ縺ｾ縺帙ｓ縲・);
}
return true;
}

private boolean canRestoreManual(Manual manual,Principal principal,String changeNote){
  User user =userService.getUserByPrincipal(principal);
  if(user.getRole()!=UserRole.ADMIN&&user.getRole()!=UserRole.APPROVER){
    throw new UnauthorizedException("讓ｩ髯舌′荳崎ｶｳ縺励※縺・∪縺吶・);
}
if (!user.isActive()) {
    throw new UnauthorizedException("譛牙柑縺ｪ繝ｦ繝ｼ繧ｶ繝ｼ縺ｧ縺ｯ縺ゅｊ縺ｾ縺帙ｓ縲・);
}
if (manual.getStatus()!=ManualStatus.ARCHIVED) {
    throw new InvalidStateException("蠕ｩ蟶ｰ縺瑚｡後∴繧九・縺ｯ繧ｹ繝・・繧ｿ繧ｹ:ARCHIVED縺ｮ繝槭ル繝･繧｢繝ｫ縺ｮ縺ｿ縺ｧ縺吶・);
}
if (!manual.getCategory().isActive()) {
    throw new InvalidStateException("譛牙柑縺ｧ縺ｪ縺・き繝・ざ繝ｪ繝ｼ縺ｧ縺ｯ謇ｿ隱阪☆繧九％縺ｨ縺悟・譚･縺ｾ縺帙ｓ縲・);
}
if (changeNote==null||changeNote.isBlank()) {
  throw new NotFoundException("譖ｴ譁ｰ螻･豁ｴ縺瑚ｦ九▽縺九ｊ縺ｾ縺帙ｓ縲・);
}
  return true;
}

  // Permission check skeletons (shared helpers)
  private void ensureApproverOrAdmin(User user) {
    if (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.APPROVER) {
      throw new UnauthorizedException("謇ｿ隱肴ｨｩ髯舌′縺ゅｊ縺ｾ縺帙ｓ縲・);
    }
  }

  private void ensureActiveUser(User user) {
    if (!user.isActive()) {
      throw new UnauthorizedException("蛛懈ｭ｢荳ｭ繝ｦ繝ｼ繧ｶ繝ｼ縺ｯ謫堺ｽ懊〒縺阪∪縺帙ｓ縲・);
    }
  }

  private void ensureStatusIn(Manual manual, ManualStatus... statuses) {
    for (ManualStatus status : statuses) {
      if (manual.getStatus() == status) {
        return;
      }
    }
    throw new InvalidStateException("縺薙・繧ｹ繝・・繧ｿ繧ｹ縺ｧ縺ｯ謫堺ｽ懊〒縺阪∪縺帙ｓ縲・);
  }

  private void ensureNotCreator(User user, Manual manual) {
    if (Objects.equals(manual.getUser().getId(), user.getId())) {
      throw new InvalidStateException("菴懈・閠・悽莠ｺ縺ｯ縺薙・謫堺ｽ懊ｒ螳溯｡後〒縺阪∪縺帙ｓ縲・);
    }
  }

  private void ensureCategoryActive(Category category) {
    if (!category.isActive()) {
      throw new InvalidStateException("菴ｿ逕ｨ蛛懈ｭ｢荳ｭ繧ｫ繝・ざ繝ｪ縺ｧ縺ｯ謫堺ｽ懊〒縺阪∪縺帙ｓ縲・);
    }
  }

  private void ensureChangeNoteRequired(String changeNote) {
    if (changeNote == null || changeNote.isBlank()) {
      throw new NotFoundException("譖ｴ譁ｰ螻･豁ｴ縺ｯ蠢・医〒縺吶・);
    }
  }

  private void ensureManualHasContent(Manual manual) {
    if (manual.getTitle() == null || manual.getContent() == null
      || manual.getTitle().isBlank() || manual.getContent().isBlank()) {
      throw new NotFoundException("繧ｿ繧､繝医Ν縺ｾ縺溘・譛ｬ譁・′譛ｪ蜈･蜉帙〒縺吶・);
    }
  }
//#endregion
//#endregion
//#region DTO螟画鋤
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
//#region 蜈ｱ騾壼・逅・
  private Manual findManualOrThrow(Long id) {
    Optional<Manual> manualOpt = manualRepository.findById(id);
    if (manualOpt.isEmpty()) {
      throw new RuntimeException("謖・ｮ壹＆繧後◆繝槭ル繝･繧｢繝ｫ縺ｯ蟄伜惠縺励∪縺帙ｓ");
    }
    return manualOpt.get();
 }
//#endregion
}
