package com.example.manual.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.manual.entity.Manual;
import com.example.manual.repository.ManualRepository;
import com.example.manual.service.ManualService;

@RestController
@RequestMapping("/manuals")
public class ManualController {

    @Autowired
    private ManualRepository manualRepository;

    @Autowired
    private ManualService manualService;

    @GetMapping("/{id}")
    public Manual getManual(@PathVariable Long id) {
        Optional<Manual> manualOpt = manualService.getManualById(id);
        if (manualOpt.isPresent()) {
            return manualOpt.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "指定したマニュアルが存在しません");
    }

    //HTTPからIDとボディからupdateManualを受け取ってサービスに渡す
    @PutMapping("/{id}")
    public Manual updateManual(@PathVariable Long id,@RequestBody Manual updatedManual) {
        return manualService.updateManual(id, updatedManual);
    }

    @PostMapping
    public Manual createManual(@RequestBody Manual manual) {
        manual.setCreatedAt(LocalDateTime.now());
        manual.setUpdatedAt(LocalDateTime.now());
        return manualRepository.save(manual);
    }
}