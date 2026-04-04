package com.example.manual.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.manual.entity.Manual;
import com.example.manual.enums.ManualStatus;
import com.example.manual.service.ManualService;


@RestController
@RequestMapping("/manuals")
public class ManualController {

    private final ManualService manualService;

    public ManualController(ManualService manualService) {
        this.manualService = manualService;
    }

    @GetMapping("/{id}")
    public Manual getManual(@PathVariable Long id) {
        Optional<Manual> manualOpt = manualService.getManualById(id);
        if (manualOpt.isPresent()) {
            return manualOpt.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "指定したマニュアルが存在しません");
    }

    @PutMapping("/{id}")
    public Manual updateManual(@PathVariable Long id, @RequestBody Manual updatedManual) {
        return manualService.updateManual(id, updatedManual);
    }

    @PutMapping("/{id}")
    public String approveManual(Long id) {
    manualService.approveManual(id);
    return "申請を受け付けました";
    }

    @PutMapping("/{id}")
    public String submitManual(Long id) {
        manualService.submitManual(id);
        return "申請を受け付けました";
    }

    @PutMapping("/{id}")
    public String rollbackManual(Long id) {
        manualService.rollbackManual(id);
        return "申請を受け付けました";
    }

    @PutMapping("/{id}")
    public String archiveManual(Long id) {
        manualService.archiveManual(id);
        return "申請を受け付けました";
        }
    
    @PutMapping("/{id}")
    public String restoreManual(Long id) {
        manualService.restoreManual(id);
        return "申請を受け付けました";
        }

    @PostMapping
    public Manual createManual(@RequestBody Manual manual) {
        Manual savedManual = manualService.createManual(manual);
        return savedManual;
    }

    @PostMapping("/copy")
    public Manual copyManual(@RequestBody Manual manual) {
        Manual savedManual = manualService.copyManual(manual);
        return savedManual;
    }

    //全件取得
    @GetMapping
    public List<Manual> getAllManuals(){
        return manualService.getAllManuals();
    }

    //タイトル検索
    @GetMapping("/search")
    public List<Manual> searchByTitle(@RequestParam String keyword) {
        return manualService.searchByTitle(keyword);
    }

    //status絞り込み検索
    @GetMapping("/status")
    public List<Manual> searchByStatus(@RequestParam ManualStatus status) {
        return manualService.searchByStatus(status);
    }
}