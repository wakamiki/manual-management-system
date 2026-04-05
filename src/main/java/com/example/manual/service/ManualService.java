package com.example.manual.service;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.manual.entity.Category;
import com.example.manual.entity.Manual;
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

  public Manual createManual(Manual manual) {
    manual.markCreatedNow();
    manual.markUpdatedNow();
    //ユーザー入力は分離予定
    //ステータス初期状態はDRAFT(下書き)状態 Approvedは作成時はNULL
    manual.markStatusDRAFT();
    manual.setTargetUser(manual.getTargetUser());
      if (manual.getCategory() == null||
          manual.getCategory().getId() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }
        Long id = manual.getCategory().getId();
        Optional<Category> categoryOpt = categoryRepository.findById(id);
      if(categoryOpt.isPresent()){
        manual.setCategory(categoryOpt.get());
      } else {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
      }
    Manual savedManual= manualRepository.save(manual);
    return savedManual;
  }

  public Manual copyManual(Manual manual) {
    Optional<Manual> manualOpt = manualRepository.findById(manual.getId());

    if (manualOpt.isPresent()) {
      Manual originalManual = manualOpt.get();
      Manual copiedManual = new Manual();
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

  public Manual updateManual(Long id, Manual updatedManual) {
    Optional<Manual> manualOpt = manualRepository.findById(id);
    Manual existingManual;

    if (manualOpt.isPresent()) {
      existingManual = manualOpt.get();
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    existingManual.setTitle(updatedManual.getTitle());
    existingManual.setContent(updatedManual.getContent());  
    //ステータス変更処理
    existingManual.markUpdatedNow();

    Manual savedManual = manualRepository.save(existingManual);

    return savedManual;
  }
  
  public Manual approveManual(Long id) {
    Optional<Manual> manualOpt = getManualById(id);
    if (!manualOpt.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    Manual targetManual = manualOpt.get();

    // APPROVED のときのみ承認日時を自動設定する
    if (targetManual.getStatus() == ManualStatus.APPROVED
      // すでに承認日時が設定されている場合は更新しない（初回の承認日時を保持するため）
       && targetManual.getApprovedAt() == null) {
      targetManual.approve();
    } // DRAFT / PENDING は未承認状態のため approvedAt は保持しない
        Manual savedManual= manualRepository.save(targetManual);
    return savedManual;
  }

  public Manual submitManual(Long id) {
    Optional<Manual> manualOpt = getManualById(id);
    if (!manualOpt.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    Manual targetManual = manualOpt.get();
    if (targetManual.getStatus() == ManualStatus.DRAFT) {
      targetManual.submitForApproval();
    } else {
      throw new IllegalStateException("下書き状態のマニュアルのみ申請できます");
    }
    Manual savedManual= manualRepository.save(targetManual);
    return savedManual;
  }

  public Manual rollbackManual(Long id) {
    Optional<Manual> manualOpt = getManualById(id);
    if (!manualOpt.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    Manual targetManual = manualOpt.get();
    if (targetManual.getStatus() == ManualStatus.PENDING) { 
      targetManual.rollbackToDraft();
    } else {
      throw new IllegalStateException("申請状態のマニュアルのみ差し戻しができます");
    }
    Manual savedManual= manualRepository.save(targetManual);
    return savedManual;
  }
  
  public Manual archiveManual(Long id) {
      Optional<Manual> manualOpt = getManualById(id);
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
    Manual savedManual= manualRepository.save(targetManual);
    return savedManual;
    }
  

//カテゴリーが同カテゴリーでアクティブ状態のときのみ復元可能の機能未実装
  public Manual restoreManual(Long id) {
      Optional<Manual> manualOpt = getManualById(id);
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
    Manual savedManual= manualRepository.save(targetManual);
    return savedManual;
    }

  
  public Optional<Manual> getManualById(Long id) {
    return manualRepository.findById(id);
  }

  public List<Manual> getAllManuals() {
   return manualRepository.findAllByOrderByUpdatedAtDesc();
  }

  public List<Manual> searchByTitle(String keyword) {
    return manualRepository.findByTitleContainingOrderByUpdatedAtDesc(keyword);
  }

  public List<Manual> searchByStatus(ManualStatus status) {
    return manualRepository.findByStatusOrderByUpdatedAtDesc(status);
  }
}
