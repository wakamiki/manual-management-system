package com.example.manual.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.manual.entity.ManualHistory;
import com.example.manual.service.ManualHistoryService;

@RestController
@RequestMapping("/manuals")
public class ManualHistoryController {
      private final ManualHistoryService manualHistoryService;

    public ManualHistoryController(ManualHistoryService manualHistoryService) {
        this.manualHistoryService = manualHistoryService;
    }

    @GetMapping("/{id}")
    public ManualHistory getManualHistory(@PathVariable Long id) {
        Optional<ManualHistory> manualHistoryOpt = manualHistoryService.getManualHistoryById(id);
        if (manualHistoryOpt.isPresent()) {
            return manualHistoryOpt.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "指定した更新履歴が存在しません");
    }

    
}
