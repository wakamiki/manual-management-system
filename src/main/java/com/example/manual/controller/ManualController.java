package com.example.manual.controller;

import com.example.manual.repository.ManualRepository;

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

import com.example.manual.dto.ManualRequestDto;
import com.example.manual.dto.ManualResponseDto;
import com.example.manual.entity.Manual;
import com.example.manual.enums.ManualStatus;
import com.example.manual.service.ManualService;


@RestController
@RequestMapping("/manuals")
public class ManualController {

    private final ManualRepository manualRepository;
    private final ManualService manualService;

    public ManualController(ManualService manualService, ManualRepository manualRepository) {
        this.manualService = manualService;
        this.manualRepository = manualRepository;
    }

      //Dto未対応　編集予定
    @GetMapping("/{id}")
    public ManualResponseDto getManual(@PathVariable Long id) {
    return manualService.getManual(id);
    }

      //Dto未対応　編集予定
    @PutMapping("/{id}")
    public ManualResponseDto updateManual(@PathVariable Long id, @RequestBody ManualRequestDto requestDto) {
        return manualService.updateManual(id, requestDto);
    }//マニュアルを更新しました。

      //Dto未対応　編集予定
    @PutMapping("/{id}/approve")
    public String approveManual(@PathVariable Long id) {
    manualService.approveManual(id);
    return "マニュアルを承認しました。";
    }

      //Dto未対応　編集予定
    @PutMapping("/{id}/submit")
    public String submitManual(@PathVariable Long id) {
        manualService.submitManual(id);
        return "申請を完了しました。";
    }

      //Dto未対応　編集予定
    @PutMapping("/{id}rollback")
    public String rollbackManual(@PathVariable Long id) {
        manualService.rollbackManual(id);
        return "マニュアルを差し戻しました。";
    }

      //Dto未対応　編集予定
    @PutMapping("/{id}archive")
    public String archiveManual(@PathVariable Long id) {
        manualService.archiveManual(id);
        return "マニュアルをアーカイブしました。";
        }
      //Dto未対応　編集予定
    @PutMapping("/{id}/restore")
    public String restoreManual(@PathVariable Long id) {
        manualService.restoreManual(id);
        return "マニュアルを復帰しました。";
        }

      //Dto未対応　編集予定
    @PostMapping
    public ManualResponseDto createManual(@RequestBody ManualRequestDto requestDto) {
     manualResponseDto savedManual = manualService.createManual(requestDto);
    
     return savedManual;
    }//マニュアルを作成しました。

      //Dto未対応　編集予定
    @PostMapping("/{id}/copy")
    public ManualResponseDto copyManual(@RequestBody ManualRequestDto manual) {
        ManualResponseDto savedManual = manualService.copyManual(manual);
        return savedManual;
    }//マニュアルを複製しました。
    
      //Dto未対応　編集予定
    //全件取得
    @GetMapping
    public List<ManualResponseDto> getAllManuals(){
        return manualService.getAllManuals();
    }

        //Dto未対応　編集予定
    //タイトル検索
    @GetMapping("/search")
    public List<ManualResponseDto> searchByTitle(@RequestParam String keyword) {
        return manualService.searchByTitle(keyword);
    }

        //Dto未対応　編集予定
    //status絞り込み検索
    @GetMapping("/status")
    public List<ManualResponseDto> searchByStatus(@RequestParam ManualStatus status) {
        return manualService.searchByStatus(status);
    }
}