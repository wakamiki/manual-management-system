package com.example.manual.service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.manual.entity.Manual;
import com.example.manual.entity.ManualStatus;
import com.example.manual.repository.ManualRepository;

@Service
public class ManualService {

  private final ManualRepository manualRepository;

  public ManualService(ManualRepository manualRepository) {
    this.manualRepository = manualRepository;
  }

  public Manual createManual(Manual manual) {
    manual.setCreatedAt(LocalDateTime.now());
    manual.setUpdatedAt(LocalDateTime.now());
    manual.setApprovedAt(null);
    manual.setStatus(ManualStatus.DRAFT);
    Manual savedManual= manualRepository.save(manual);
    return savedManual;
  }

  public Optional<Manual> getManualById(Long id) {
    return manualRepository.findById(id);
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
    existingManual.setStatus(updatedManual.getStatus());
    updateApprovedAtByStatus(existingManual);
    existingManual.setUpdatedAt(LocalDateTime.now());

    Manual savedManual = manualRepository.save(existingManual);

    return savedManual;
  }

  private void updateApprovedAtByStatus(Manual targetManual) {

    ManualStatus status = targetManual.getStatus();

    if (status == ManualStatus.APPROVED) {
      targetManual.setApprovedAt(LocalDateTime.now());
    } else if (status == ManualStatus.DRAFT ||
        status == ManualStatus.PENDING) {
      targetManual.setApprovedAt(null);
    }
  }

  public void deleteManual(Long id) {
    Optional<Manual> manualopt = manualRepository.findById(id);
    if (manualopt.isPresent()) {
      manualRepository.deleteById(id);
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
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
