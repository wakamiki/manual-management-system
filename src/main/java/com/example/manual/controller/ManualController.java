package com.example.manual.controller;

import java.security.Principal;
import java.util.List;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    //対応ボタン　マニュアル公開（編集なし）
    @PostMapping("/{manualId}/actions/submit")
    public String submitManual(@PathVariable Long manualId, Principal principal) {
        manualService.submitManual(manualId);
        return "マニュアルを公開しました。";
    }

    //対応ボタン　承認（チェンジノート無）
    //TODO: ロール必須
    @PostMapping("/{manualId}/actions/approve")
    public String approveManual(@PathVariable Long manualId, Authentication authentication) {
        manualService.approveManual(manualId, authentication);
        return "マニュアルを承認しました。";
    }

    //対応ボタン　承認（チェンジノート有）
    @PostMapping("/{manualId}/actions/approve")
    public String approveEditManual(@PathVariable Long manualId,@Valid ManualRequestDto requestDto, Authentication authentication) {
        manualService.approveEditManual(manualId,requestDto.getChangeNote(),authentication);
        return "マニュアルを承認しました。";
    }

    //対応ボタン　下書き保存
    @PostMapping("/{manualId}/draft")
    public String saveDraftForCreate(@PathVariable Long manualId,@Valid ManualRequestDto requestDto, Principal principal) {
        manualService.saveDraftForCreate(manualId,requestDto, principal);
        return "マニュアルを下書きに保存しました";
    }
    
    //対応ボタン　下書き保存(複製ボタンから遷移)
    @PostMapping("/{manualId}/draft")
    public String saveDraftForCopy(@PathVariable Long manualId,@Valid ManualRequestDto requestDto, Principal principal) {
        manualService.saveDraftForCopy(manualId,requestDto, principal);
        return "複製マニュアルを下書きに保存しました";
    }

    //対応ボタン　マニュアルを公開（新規作成から遷移）
    @PostMapping("/{manualId}/pending")
    public String submitToPending(@PathVariable Long manualId,@Valid ManualRequestDto requestDto, Principal principal) {
        manualService.submitToPending(manualId, requestDto, principal);
        return "マニュアルを承認待ち公開しました。";
    }

    //対応ボタン　マニュアルを公開(編集画面から遷移)
    @PostMapping("/{manualId}/pending")
    public String editToPending(@PathVariable Long manualId,@Valid ManualRequestDto requestDto, Principal principal) {
        manualService.editToPending(manualId, requestDto, principal);
        return "マニュアルを承認待ち公開しました。";
    }

    //対応ボタン　差し戻し（チェンジノート必須）
    @PostMapping("/{manualId}/actions/rollback")
    public String rollbackEditManual(@PathVariable Long manualId,@Valid ManualRequestDto requestDto,Authentication authentication) {
        manualService.rollbackEditManual(manualId,requestDto.getChangeNote(), authentication);
        return "マニュアルを差し戻しました。";
    }

    //対応ボタン　アーカイブ（チェンジノート必須）
    @PostMapping("/{manualId}/actions/archive")
    public String archiveManual(@PathVariable Long manualId,@Valid ManualRequestDto requestDto,Authentication authentication){
        manualService.archiveManual(manualId, requestDto, authentication);
        return "マニュアルをアーカイブしました。";
    }

//#endregion
//#region 新規タブ画面遷移

    //対応ボタン　詳細を見る（新規タブ）
    @GetMapping("/{manualId}")
    public ManualDetailDto goToDetailPage(@PathVariable Long manualId) {
        ManualDetailDto detailDto = manualService.goToDetailPage(manualId);
        return detailDto;
    }

    //対応ボタン　複製（新規タブ）
    @PostMapping("/{manualId}/actions/copy") 
    public ManualResponseDto goToCopyPage(@PathVariable Long manualId, Principal principal) {
        ManualResponseDto responseDto = manualService.goToCopyPage(manualId, principal);
        return responseDto;
     }
    
     //対応ボタン　 編集（新規タブ）
    public ManualResponseDto goToEditPage(@PathVariable Long manualId, Principal principal) {
        ManualResponseDto responseDto = manualService.goToEditPage(manualId, principal);
        return responseDto;
    }

    //対応ボタン　差し戻し（新規タブ）
    @PostMapping("/{manualId}/actions/rollback")
    public ManualResponseDto goToRollbackPage(@PathVariable Long manualId, Authentication authentication) {
        ManualResponseDto responseDto = manualService.goToRollbackPage(manualId, authentication);
        return responseDto;
    }
    
    //対応ボタン　アーカイブ(新規タブ)
    @PostMapping("/{manualId}/actions/archive")
    public ManualResponseDto goToArchivePage(@PathVariable Long manualId, Authentication authentication) {
        ManualResponseDto responseDto = manualService.goToArchivePage(manualId, authentication);
        return responseDto;
    }

    //対応ボタン　復帰（新規タブ）
    @PostMapping("/{manualId}/actions/restore")
    public ManualResponseDto goToRestorePage(@PathVariable Long manualId, Authentication authentication) {
        ManualResponseDto responseDto = manualService.goToRestorePage(manualId,authentication);
        return responseDto;
        }

//#endregion

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