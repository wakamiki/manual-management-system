package com.example.manual.service;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.manual.controller.ManualController;
import com.example.manual.entity.Manual;
import com.example.manual.repository.ManualRepository;
import java.time.LocalDateTime;

@Service
public class ManualService {

  private final ManualRepository manualRepository;

  public ManualService(ManualRepository manualRepository) {
    this.manualRepository = manualRepository;
  }

  public Optional<Manual> getManualById(Long id) {
    return manualRepository.findById(id);
  }

  //IDを入れたら中身のマニュアルを返す
  public Manual updateManual(Long id,Manual updatedManual) {
    //画面から受け取ったIDをつかってマニュアルを呼び出す。
    Optional<Manual> manualOpt = manualRepository.findById(id);
    Manual existingManual;

    if (manualOpt.isPresent()) {
      existingManual = manualOpt.get();
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    existingManual.setTitle(updatedManual.getTitle());
    existingManual.setContent(updatedManual.getContent());
    existingManual.setStatus(updatedManual.getStatus());
    existingManual.setUpdatedAt(LocalDateTime.now());

        Manual savedManual = manualRepository.save(existingManual);


        return savedManual;

  }
}
