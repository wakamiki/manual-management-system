package com.example.manual.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.manual.dto.ManualRequestDto;
import com.example.manual.dto.ManualResponseDto;
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
    public ManualResponseDto getManual(@PathVariable Long id) {
    return manualService.getManual(id);
    }

    @PutMapping("/update")
    public ManualResponseDto updateManual(@RequestBody ManualRequestDto requestDto) {
        return manualService.updateManual(requestDto);
    }//マニュアルを更新しました。

    @PutMapping("/{id}/approve")
    public String approveManual(@PathVariable Long id) {
    manualService.approveManual(id);
    return "マニュアルを承認しました。";
    }

    @PutMapping("/rollback")
    public String rollbackManual(@RequestBody ManualRequestDto requestDto) {
        manualService.rollbackManual(requestDto);
        return "マニュアルを差し戻しました。";
    }

    @PutMapping("/archive")
    public String archiveManual(@RequestBody ManualRequestDto requestDto) {
        manualService.archiveManual(requestDto);
        return "マニュアルをアーカイブしました。";
        }
    @PutMapping("/{id}/restore")
    public String restoreManual(@PathVariable Long id) {
        manualService.restoreManual(id);
        return "マニュアルを復帰しました。";
        }

    @PostMapping("/draft")
    public ManualResponseDto createDraftManual(@RequestBody ManualRequestDto requestDto) {
     return manualService.createDraftManual(requestDto);
    }//マニュアルを下書きに保存しました。

    @PostMapping("/submit")
    public ManualResponseDto createAndSubmitManual(@RequestBody ManualRequestDto requestDto) {
    return manualService.createAndSubmitManual(requestDto);
    }//マニュアルを承認申請しました。

      //Dto未対応　編集予定
    @PostMapping("/copy")
    public ManualResponseDto copyManualDRAFT(@RequestBody ManualRequestDto requestDto) {
        return manualService.copyManualDRAFT(requestDto);
    }//マニュアルを複製し下書きに保存しました。

        @PostMapping("/copy/submit")
    public ManualResponseDto copyManualPENDING(@RequestBody ManualRequestDto requestDto) {
        return manualService.copyManualPENDING(requestDto);
    }//マニュアルを複製し申請しました。

      //マニュアルを直接返す形になっている編集予定
    //全件取得
    @GetMapping
    public List<Manual> getAllManuals(){
        return manualService.getAllManuals();
    }

        //マニュアルを直接返す形になっている編集予定
    //タイトル検索
    @GetMapping("/search")
    public List<Manual> searchByTitle(@RequestParam String keyword) {
        return manualService.searchByTitle(keyword);
    }

     //マニュアルを直接返す形になっている編集予定
    //status絞り込み検索
    @GetMapping("/status")
    public List<Manual> searchByStatus(@RequestParam ManualStatus status) {
        return manualService.searchByStatus(status);
    }

}