package com.example.manual.service;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.manual.dto.ManualResponseDto;
import com.example.manual.entity.Category;
import com.example.manual.entity.Manual;
import com.example.manual.entity.ManualHistory;
import com.example.manual.enums.ManualStatus;
import com.example.manual.repository.CategoryRepository;
import com.example.manual.repository.ManualRepository;
import com.example.manual.dto.ManualRequestDto;

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

  public ManualResponseDto createDraftManual(com.example.manual.dto.ManualRequestDto requestDto) {
    Manual manual = new Manual();
    manual.markCreatedNow();
    manual.markUpdatedNow();
    manual.setTitle(requestDto.getTitle());
    manual.setContent(requestDto.getContent());
    //ユーザー名とidを呼び
    manual.markStatusDRAFT();
    //承認日時は作成時はNULL
    if (requestDto.getCategoryId() == null ||
        requestDto.getCategoryId() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
    Long id = requestDto.getCategoryId();
    Optional<Category> categoryOpt = categoryRepository.findById(id);
    if (categoryOpt.isPresent()) {
      manual.setCategory(categoryOpt.get());
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    Manual savedManual = manualRepository.save(manual);

    ManualResponseDto responseDto = new ManualResponseDto();
    responseDto.setCategoryName(savedManual.getCategory().getCategoryName());
    responseDto.setTitle(savedManual.getTitle());
    responseDto.setContent(savedManual.getContent());
    responseDto.setStatus(savedManual.getStatus());
    responseDto.setCategoryName(savedManual.getCategory().getCategoryName());
    responseDto.setDisplayName(savedManual.getTargetUser().getDisplayName());
    responseDto.setCreatedAt(savedManual.getCreatedAt());
    responseDto.setUpdatedAt(savedManual.getUpdatedAt());
    return responseDto;
  }

  // Responseにユーザー名とid未実装　権限チェック(ログイン中ユーザーか)
  public ManualResponseDto createAndSubmitManual(com.example.manual.dto.ManualRequestDto requestDto) {
    Manual manual = new Manual();
    manual.markCreatedNow();
    manual.markUpdatedNow();
    if (requestDto.getTitle()==null|| requestDto.getContent()==null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    manual.setTitle(requestDto.getTitle());
    manual.setContent(requestDto.getContent());
    // ユーザー名とidを呼び
    manual.submitPENDING();
    // 承認日時は作成時はNULL
    if (requestDto.getCategoryId() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
    Long id = requestDto.getCategoryId();
    Optional<Category> categoryOpt = categoryRepository.findById(id);
    if (categoryOpt.isPresent()) {
      categoryOpt.get();
      manual.setCategory(categoryOpt.get());
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    Manual savedManual = manualRepository.save(manual);

    ManualResponseDto responseDto = new ManualResponseDto();
    responseDto.setTitle(savedManual.getTitle());
    responseDto.setContent(savedManual.getContent());
    responseDto.setStatus(savedManual.getStatus());
    responseDto.setCreatedAt(savedManual.getCreatedAt());
    responseDto.setUpdatedAt(savedManual.getUpdatedAt());
    return responseDto;
  }

  //編集予定　新規作成（DRAFT）ユーザー関係未実装　履歴作成必須
  public ManualResponseDto copyManualDRAFT(com.example.manual.dto.ManualRequestDto requestDto) {
    Optional<Manual> manualOpt = manualRepository.findById(requestDto.getId());
   if(!manualOpt.isPresent()){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "指定したマニュアルが存在しません");
    }
    Manual manual = manualOpt.get();
    manual.setTitle(requestDto.getTitle());
    manual.setContent(requestDto.getContent());

    // ユーザー入力は分離予定
    // 履歴作成必須ルール適応前
    manual.markStatusDRAFT();
    manual.markCreatedNow();
    manual.markUpdatedNow();

    Manual savedManual = manualRepository.save(manual);

    ManualResponseDto responseDto;
    responseDto = manualHistoryService.createHistory(manual, requestDto.getChangeNote());
    responseDto.setTitle(savedManual.getTitle());
    responseDto.setContent(savedManual.getContent());
    responseDto.setCategoryName(savedManual.getCategory().getCategoryName());
    responseDto.setStatus(savedManual.getStatus());
    responseDto.setCreatedAt(savedManual.getCreatedAt());
    responseDto.setUpdatedAt(savedManual.getUpdatedAt());
    responseDto.setCangeNote(savedManual.getCangeNote());
    responseDto.setChangedAt(savedManual.getCangedAt());
    return responseDto;
  }

  // 編集予定 新規作成（DRAFT）ユーザー関係未実装 履歴作成必須
  public ManualResponseDto copyManualPENDING(com.example.manual.dto.ManualRequestDto requestDto) {
    Optional<Manual> manualOpt = manualRepository.findById(requestDto.getId());

    if (manualOpt.isPresent()) {
      if (requestDto.getTitle() == null || requestDto.getContent() == null
          || requestDto.getCategory() == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
      }
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "指定したマニュアルが存在しません");
    }
    Manual manual = manualOpt.get();
    manual.setTitle(requestDto.getTitle());
    manual.setContent(requestDto.getContent());
    manual.setCategory(requestDto.getCategory());// チェック機構入れる
    // ユーザー入力は分離予定
    // 履歴作成必須ルール適応前
    manual.submitPENDING();
    manual.markCreatedNow();
    manual.markUpdatedNow();

    Manual savedManual = manualRepository.save(manual);

    ManualResponseDto responseDto;
    responseDto = manualHistoryService.createHistory(manual, requestDto.getChangeNote());
    responseDto.setTitle(savedManual.getTitle());
    responseDto.setContent(savedManual.getContent());
    responseDto.setCategoryName(savedManual.getCategory().getCategoryName());
    responseDto.setStatus(savedManual.getStatus());
    responseDto.setCreatedAt(savedManual.getCreatedAt());
    responseDto.setUpdatedAt(savedManual.getUpdatedAt());
    return responseDto;
  }

    //編集予定 ユーザー関係未実装
  public ManualResponseDto updateManual(ManualRequestDto requestDto) {
    Optional<Manual> manualOpt = manualRepository.findById(requestDto.getId());

    if (manualOpt.isPresent()) {
      existingManual = manualOpt.get();
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    existingManual = manualOpt.get();
    existingManual.setTitle(requestDto.getTitle());
    existingManual.setContent(requestDto.getContent());
    existingManual.markUpdatedNow();
    createHistory(existingManual, requestDto.getChangeNote());

    Manual savedManual = manualRepository.save(existingManual);

    ManualResponseDto responseDto = new ManualResponseDto();
    responseDto = manualHistoryService.createHistory(existingManual, requestDto.getChangeNote());
    responseDto.setId(savedManual.getId());
    responseDto.setTitle(savedManual.getTitle());
    responseDto.setContent(savedManual.getContent());
    responseDto.setStatus(savedManual.getStatus());
    responseDto.setUpdatedAt(savedManual.getUpdatedAt());

    return responseDto;
  }

    //編集予定 ユーザー関係未実装
  public ManualResponseDto approveManual(Long id) {
    Optional<Manual> manualOpt = manualRepository.findById(id);
    if (!manualOpt.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    Manual targetManual = manualOpt.get();

    // APPROVED のときのみ承認日時を自動設定する
    if (targetManual.getStatus() == ManualStatus.APPROVED
      // すでに承認日時が設定されている場合は更新しない（初回の承認日時を保持するため）
       && targetManual.getApprovedAt() == null) {
      targetManual.approve();
    }
      Manual savedManual= manualRepository.save(targetManual);

      ManualResponseDto responseDto = new ManualResponseDto();
      responseDto.setStatus(savedManual.getStatus());
      responseDto.setApprovedAt(savedManual.getApprovedAt());
    return responseDto;
  }

    //編集予定 更新履歴必須 ユーザー関係未実装
  public ManualResponseDto rollbackManual(com.example.manual.dto.ManualRequestDto requestDto) {
    Optional<Manual> manualOpt = manualRepository.findById(requestDto.getId());
    if (!manualOpt.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    Manual targetManual = manualOpt.get();
    if (targetManual.getStatus() == ManualStatus.PENDING) {
      targetManual.rollbackToDraft();
    } else {
      throw new IllegalStateException("申請状態のマニュアルのみ差し戻しができます");
    }
    Manual savedManual = manualRepository.save(targetManual);

    ManualResponseDto responseDto = new ManualResponseDto();
    responseDto = manualHistoryService.createHistory(savedManual, requestDto.getChangeNote());
    responseDto.setStatus(savedManual.getStatus());
    return responseDto;
  }

    //編集予定　ユーザー関係未実装　更新履歴必須
    public ManualResponseDto archiveManual(com.example.manual.dto.ManualRequestDto requestDto) {
      Optional<Manual> manualOpt = manualRepository.findById(requestDto.getId());
      if (!manualOpt.isPresent()) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
      }
      Manual targetManual = manualOpt.get();
      if (targetManual.getStatus() == ManualStatus.APPROVED
      ||targetManual.getStatus() == ManualStatus.PENDING) {
        targetManual.archive();
      } else {
        throw new IllegalStateException("公開されているマニュアルのみアーカイブできます");
      }
      targetManual.markUpdatedNow();
    Manual savedManual= manualRepository.save(targetManual);

    ManualResponseDto responseDto = new ManualResponseDto();
    responseDto = manualHistoryService.createHistory(savedManual, requestDto.getChangeNote());
    responseDto.setCategory(savedManual.getCategory());
    responseDto.setStatus(savedManual.getStatus());
    responseDto.setUpdatedAt(savedManual.getUpdatedAt());
    return responseDto;
    }

    //編集予定 ユーザー関係未実装
//カテゴリーが同カテゴリーでアクティブ状態のときのみ復元可能の機能未実装
  public ManualResponseDto restoreManual(Long id) {
      Optional<Manual> manualOpt = manualRepository.findById(id);
      if (!manualOpt.isPresent()) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
      }
      Manual targetManual = manualOpt.get();
      if (targetManual.getStatus() == ManualStatus.ARCHIVED
        && targetManual.getCategory().isActive() == true)
       {
        targetManual.restoreToApproved();
      } else {
        throw new IllegalStateException("アーカイブ状態のマニュアルのみ復元できます");
      }
      Manual savedManual = manualRepository.save(targetManual);

      ManualResponseDto responseDto = new ManualResponseDto();
      responseDto.setStatus(savedManual.getStatus());
    return responseDto;
    }

      //編集予定　ユーザー関係未実装
  public ManualResponseDto getManual(Long id) {
    Optional<Manual> manualOpt = manualRepository.findById(id);
        if (manualOpt.isPresent()==false) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "指定したマニュアルが存在しません");
        }
        Manual getManual = manualOpt.get();
        ManualResponseDto responseDto = new ManualResponseDto();
        List<ManualHistory> historyList = manualHistoryService.getManualIdHistory(id);
        if (!historyList.isEmpty()) {
          ManualHistory latestHistory = historyList.get(0);
        }
        responseDto.setId(getManual.getId());
        responseDto.setTitle(getManual.getTitle());
        responseDto.setContent(getManual.getContent());
        responseDto.setStatus(getManual.getStatus());
        responseDto.setUpdatedAt(getManual.getUpdatedAt());
        responseDto.setCreatedAt(getManual.getCreatedAt());
        responseDto.setCategory(getManual.getCategory());

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
}
 