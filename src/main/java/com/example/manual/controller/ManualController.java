package com.example.manual.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.manual.dto.ManualCopyRequestDto;
import com.example.manual.dto.ManualDetailDto;
import com.example.manual.dto.ManualRequestDto;
import com.example.manual.dto.ManualResponseDto;
import com.example.manual.entity.Manual;
import com.example.manual.enums.ManualStatus;
import com.example.manual.service.ManualService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/manuals")
public class ManualController {


    private final ManualService manualService;

    public ManualController(ManualService manualService) {
        this.manualService = manualService;
    }

    @GetMapping("/{manualId}")
    public ManualDetailDto getManualDetail(@PathVariable Long manualId) {
    ManualDetailDto detailDto = manualService.getManualDetail(manualId);
    return detailDto;
    }

    @PutMapping("/{manualId}")
    public void updateManual(@PathVariable Long id) {
        manualService.updateManual(id);
    }//マニュアルを更新しました。

    @PostMapping("/{manualId}/actions/submit")
    public String submitManual(@PathVariable Long id) {
        manualService.submitManual(id);
        return "マニュアルを公開しました。";
    }

    @PostMapping("/{manualId}/actions/approve")
    public String approveManual(@PathVariable Long id) {
    manualService.approveManual(id);
    return "マニュアルを承認しました。";
    }

    @PostMapping("/{manualId}/actions/rollback")
    public String rollbackManual(@PathVariable Long id,@Valid ManualRequestDto requestDto) {
        return "マニュアルを差し戻しました。";
    }

    @PostMapping("/{manualId}/actions/archive")
    public String archiveManual(@PathVariable Long id) {
        manualService.archiveManual(id);
        return "マニュアルをアーカイブしました。";
    }

    @PostMapping("/{manualId}/actions/restore")
    public String restoreManual(@PathVariable Long id) {
        manualService.restoreManual(id);
        return "マニュアルを復帰しました。";
        }

    @PostMapping("/draft")
    public void createDraftManual(@Valid ManualRequestDto requestDto) {
    }//マニュアルを下書きに保存しました。

    @PostMapping("/pending")
    public void createPendingManual(@Valid ManualRequestDto requestDto) {
    }//マニュアルを承認申請しました。

      //Dto未対応　編集予定
      @PostMapping("/{manualId}/actions/copyDraft")
    public void copyDraftManual(@PathVariable Long id,@Valid ManualCopyRequestDto requestDto) {
    }//マニュアルを複製し下書きに保存しました。

    @PostMapping("/{manualId}/action/copyPending")
    public void copyPendingManual(@PathVariable Long id,@Valid ManualCopyRequestDto requestDto) {
    }//マニュアルを複製し申請しました。

    @GetMapping("{manualId}")
    public ManualResponseDto getManualForEdit(@PathVariable Long manualId) {
    ManualResponseDto responseDto = getManualForEdit(manualId);
        return responseDto;
    }
    
    

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