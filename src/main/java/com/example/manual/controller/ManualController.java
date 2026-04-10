package com.example.manual.controller;

import java.security.Principal;
import java.util.List;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
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

//#region 画面初期表示
//#endregion

//#region 登録・更新
    @PostMapping("/{manualId}/actions/submit")//対応ボタン　マニュアル公開（編集なし）
    public String submitManual(@PathVariable Long manualId, Principal principal) {
        manualService.submitManual(manualId);
        return "マニュアルを公開しました。";
    }

    //ロール必須
    @PostMapping("/{manualId}/actions/approve")//対応ボタン　承認（チェンジノートなし）
    public String approveManual(@PathVariable Long manualId, Authentication authentication) {
        manualService.approveManual(manualId, authentication);
        return "マニュアルを承認しました。";
    }

//#endregion
//#region 新規タブ画面遷移
    @GetMapping("/{manualId}")//対応ボタン　詳細を見る（新規タブ）　
    public ManualDetailDto goToDetailPage(@PathVariable Long manualId) {
        ManualDetailDto detailDto = manualService.goToDetailPage(manualId);
        return detailDto;
    }

    @PostMapping("/{manualId}/actions/copy") //対応ボタン　複製（新規タブ）
    public ManualResponseDto goToCopyPage(@PathVariable Long manualId, Principal principal) {
        ManualResponseDto responseDto = goToCopyPage(manualId, principal);
        return responseDto;
    }
        //対応ボタン　 編集（新規タブ）
    public ManualResponseDto goToEditPage(@PathVariable Long manualId, Principal principal) {
        ManualResponseDto responseDto = goToEditPage(manualId, principal);
        return responseDto;
    }

    //対応ボタン　差し戻し（新規タブ）
    @PostMapping("/{manualId}/actions/rollback")
    public ManualResponseDto goToRollbackPage(@PathVariable Long manualId, Authentication authentication) {
        ManualResponseDto responseDto = manualService.goToRollbackPage(manualId, authentication);
        return responseDto;
    }
//#endregion
    @PutMapping("/{manualId}")
    public ManualResponseDto updateManual(@PathVariable Long manualId, Principal principal) {
        String  loginId = getLoginId(principal);
        ManualResponseDto responseDto = manualService.updateManual(manualId, loginId);
        return responseDto;
    }//マニュアルを更新しました。

    @PostMapping("/{manualId}/actions/archive")
    public ManualResponseDto goToArchivePage(@PathVariable Long manualId, Authentication authentication) {
        ManualResponseDto responseDto = manualService.goToArchivePage(manualId, authentication);
        return responseDto;
    }

    @PostMapping("/{manualId}/actions/rollback")
    public String rollbackManual(@PathVariable Long manualId,@Valid ManualRequestDto requestDto, Principal principal) {
        return "マニュアルを差し戻しました。";
    }

    @PostMapping("/{manualId}/actions/restore")
    public String restoreManual(@PathVariable Long manualId, Principal principal) {
        manualService.restoreManual(manualId);
        return "マニュアルを復帰しました。";
        }

    @PostMapping("/draft")
    public void createDraftManual(@Valid ManualRequestDto requestDto, Principal principal) {
    }//マニュアルを下書きに保存しました。

    @PostMapping("/pending")
    public void createPendingManual(@Valid ManualRequestDto requestDto, Principal principal) {
    }//マニュアルを承認申請しました。

      //Dto未対応　編集予定
    @PostMapping("/{manualId}/actions/copyDraft")
    public void copyDraftManual(@PathVariable Long manualId,@Valid ManualCopyRequestDto requestDto, Principal principal) {
    }//マニュアルを複製し下書きに保存しました。

    @PostMapping("/{manualId}/actions/copyPending")
    public void copyPendingManual(@PathVariable Long manualId,@Valid ManualCopyRequestDto requestDto, Principal principal) {
    }//マニュアルを複製し申請しました。

    @GetMapping("{manualId}/edit")
    public ManualResponseDto getManualForEdit(@PathVariable Long manualId, Principal principal) {
    ManualResponseDto responseDto = getManualForEdit(manualId, principal);
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

//#region　共通処理
    private String getLoginId(Principal principal) {
        String loginId = principal.getName();
        return loginId;
    }
//#endregion
}