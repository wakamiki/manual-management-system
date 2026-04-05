package com.example.manual.service;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.manual.dto.ManualResponseDto;
import com.example.manual.entity.Category;
import com.example.manual.entity.Manual;
import com.example.manual.entity.ManualRequestDto;
import com.example.manual.enums.ManualStatus;
import com.example.manual.repository.CategoryRepository;
import com.example.manual.repository.ManualRepository;

@Service
public class ManualService {

  private final ManualRepository manualRepository;
  private final CategoryRepository categoryRepository;

  public ManualService(ManualRepository manualRepository,CategoryRepository categoryRepository) {
    this.manualRepository = manualRepository;
    this.categoryRepository = categoryRepository;
  }

  public ManualResponseDto createManual(ManualRequestDto requestDto) {
    Manual manual = new Manual();
    manual.markCreatedNow();
    manual.markUpdatedNow();
    manual.setTitle(requestDto.getTitle());
    manual.setContent(requestDto.getContent());
    manual.setTargetUser(requestDto.getTargetUser());
    //ステータス初期状態はDRAFT(下書き)状態 Approvedは作成時はNULL
    manual.markStatusDRAFT();
    //承認日時は作成時はNULL
      if (Manual.getCategory() == null||
          Manual.getCategory().getId() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }
        Long id = Manual.getCategory().getId();
        Optional<Category> categoryOpt = categoryRepository.findById(id);
      if(categoryOpt.isPresent()){
        Manual.setCategory(categoryOpt.get());
      } else {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
      }
    Manual savedManual = manualRepository.save(manual);

    ManualResponseDto responseDto = new ManualResponseDto();
    responseDto.setTitle(savedManual.getTitle());
    responseDto.setContent(savedManual.getContent());
     responseDto.setStatus(savedManual.getStatus());
     responseDto.setCategoryName(savedManual.getCategory().getName());
     responseDto.setDisplayName(savedManual.getTargetUser().getDisplayName());
     responseDto.setCreatedAt(savedManual.getCreatedAt());
     responseDto.setUpdatedAt(savedManual.getUpdatedAt());
    return responseDto;
  }

  //Dto未対応　編集予定
  public ManualResponseDto copyManual(ManualRequestDto manual) {
    Optional<Manual> manualOpt = manualRepository.findById(manual.getId());

    if (manualOpt.isPresent()) {
      ManualRequestDto originalManual = manualOpt.get();
      ManualRequestDto copiedManual = new ManualRequestDto();
      copiedManual.setTitle(originalManual.getTitle());
      copiedManual.setContent(originalManual.getContent());
      copiedManual.setCategory(originalManual.getCategory());
      //ユーザー入力は分離予定
      //履歴作成必須ルール適応前
      copiedManual.markStatusDRAFT(); // コピーしたマニュアルは下書き状態にする
      return createManual(copiedManual);
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "指定したマニュアルが存在しません");
    }
    //セーブ処理考え中
  }

    //Dto未対応　編集予定
  public ManualResponseDto updateManual(Long id, ManualRequestDto requestDto) {
    Optional<Manual> manualOpt = manualRepository.findById(id);
    Manual existingManual;

    if (manualOpt.isPresent()) {
      existingManual = manualOpt.get();
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    existingManual.setTitle(requestDto.getTitle());
    existingManual.setContent(requestDto.getContent());  
    //ステータス変更処理
    existingManual.markUpdatedNow();

    Manual savedManual = manualRepository.save(existingManual);
    ManualResponseDto responseDto = new ManualResponseDto();
    responseDto.setId(savedManual.getId());
    responseDto.setTitle(savedManual.getTitle());
    responseDto.setContent(savedManual.getContent());
    return responseDto;
  }
  
    //Dto未対応　編集予定
  public ManualResponseDto approveManual(Long id) {
    Optional<Manual> manualOpt = getManualById(id);
    if (!manualOpt.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    ManualRequestDto targetManual = manualOpt.get();

    // APPROVED のときのみ承認日時を自動設定する
    if (targetManual.getStatus() == ManualStatus.APPROVED
      // すでに承認日時が設定されている場合は更新しない（初回の承認日時を保持するため）
       && targetManual.getApprovedAt() == null) {
      targetManual.approve();
    } // DRAFT / PENDING は未承認状態のため approvedAt は保持しない
        ManualRequestDto savedManual= manualRepository.save(targetManual);
    return savedManual;
  }

    //Dto未対応　編集予定
  public ManualRequestDto submitManual(Long id) {
    Optional<ManualRequestDto> manualOpt = getManualById(id);
    if (!manualOpt.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    ManualRequestDto targetManual = manualOpt.get();
    if (targetManual.getStatus() == ManualStatus.DRAFT) {
      targetManual.submitForApproval();
    } else {
      throw new IllegalStateException("下書き状態のマニュアルのみ申請できます");
    }
    ManualRequestDto savedManual= manualRepository.save(targetManual);
    return savedManual;
  }

    //Dto未対応　編集予定
  public ManualRequestDto rollbackManual(Long id) {
    Optional<ManualRequestDto> manualOpt = getManualById(id);
    if (!manualOpt.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    ManualRequestDto targetManual = manualOpt.get();
    if (targetManual.getStatus() == ManualStatus.PENDING) { 
      targetManual.rollbackToDraft();
    } else {
      throw new IllegalStateException("申請状態のマニュアルのみ差し戻しができます");
    }
    ManualRequestDto savedManual= manualRepository.save(targetManual);
    return savedManual;
  }
  
    //Dto未対応　編集予定
  public ManualRequestDto archiveManual(Long id) {
      Optional<ManualRequestDto> manualOpt = getManualById(id);
      if (!manualOpt.isPresent()) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
      }
      ManualRequestDto targetManual = manualOpt.get();
      if (targetManual.getStatus() == ManualStatus.APPROVED
      ||targetManual.getStatus() == ManualStatus.PENDING) {
        targetManual.archive();
      } else {
        throw new IllegalStateException("公開されているマニュアルのみアーカイブできます");
      } 
    ManualRequestDto savedManual= manualRepository.save(targetManual);
    return savedManual;
    }
  

    //Dto未対応　編集予定
//カテゴリーが同カテゴリーでアクティブ状態のときのみ復元可能の機能未実装
  public ManualResponseDto restoreManual(Long id) {
      Optional<Manual> manualOpt = getManualById(id);
      if (!manualOpt.isPresent()) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
      }
      ManualRequestDto targetManual = manualOpt.get();
      if (targetManual.getStatus() == ManualStatus.ARCHIVED
        && targetManual.getCategory().isActive() == true)
       {
        targetManual.restoreToApproved();
      } else {
        throw new IllegalStateException("アーカイブ状態のマニュアルのみ復元できます");
      }
    ManualRequestDto savedManual= manualRepository.save(targetManual);
    return savedManual;
    }

      //Dto未対応　編集予定
  public ManualResponseDto getManual(Long id) {
    Optional<Manual> manualOpt = manualRepository.findById(id);
        if (manualOpt.isPresent()==false) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "指定したマニュアルが存在しません");    
        }
        Manual getManual = manualOpt.get();
        ManualResponseDto responseDto = new ManualResponseDto();
        responseDto.setId(getManual.getId());
        responseDto.setTitle(getManual.getTitle());
        responseDto.setContent(getManual.getContent());

        return responseDto;
        
    }

      //Dto未対応　編集予定
  public List<ManualRequestDto> getAllManuals() {
   return manualRepository.findAllByOrderByUpdatedAtDesc();
  }

    //Dto未対応　編集予定
  public List<ManualRequestDto> searchByTitle(String keyword) {
    return manualRepository.findByTitleContainingOrderByUpdatedAtDesc(keyword);
  }

    //Dto未対応　編集予定
  public List<ManualRequestDto> searchByStatus(ManualStatus status) {
    return manualRepository.findByStatusOrderByUpdatedAtDesc(status);
  }
}
