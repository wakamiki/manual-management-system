package com.example.manual.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.manual.dto.ManualActionRequestDto;
import com.example.manual.dto.ManualDetailDto;
import com.example.manual.dto.ManualListDto;
import com.example.manual.dto.ManualRequestDto;
import com.example.manual.dto.ManualResponseDto;
import com.example.manual.dto.ManualSearchConditionDto;
import com.example.manual.service.ManualService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/manuals")
public class ManualController {


private final ManualService manualService;

public ManualController(ManualService manualService) {
    this.manualService = manualService;
    }


@GetMapping
public List<ManualListDto>searchManuals(ManualSearchConditionDto condition){
    List<ManualListDto>manualDtoList = manualService.searchManuals(condition);
    return manualDtoList;
}
//#endregion
//#region 登録・更新
    //対応ボタン　マニュアル公開（編集なし）
    @PostMapping("/{manualId}/actions/submit")
    public String submitManual(@PathVariable Long manualId, Principal principal) {
        manualService.submitManual(manualId);
        return "マニュアルを公開しました。";
    }
    //対応ボタン　下書き保存
    @PostMapping("/{manualId}/actions/save-draft")
    public String saveDraftForCreate(@PathVariable Long manualId,@Valid ManualRequestDto requestDto, Principal principal) {
        manualService.saveDraftForCreate(manualId,requestDto, principal);
        return "マニュアルを下書きに保存しました";
    }
    //対応ボタン　下書き保存(複製ボタンから遷移)
    @PostMapping("/{manualId}/actions/save-draft-copy")
    public String saveDraftForCopy(@PathVariable Long manualId,@Valid ManualRequestDto requestDto, Principal principal) {
        manualService.saveDraftForCopy(manualId,requestDto, principal);
        return "複製マニュアルを下書きに保存しました";
    }

     //対応ボタン　マニュアルを公開（新規作成から遷移）
    @PostMapping("/{manualId}/actions/submit-pending")
    public String submitToPending(@PathVariable Long manualId,@Valid ManualRequestDto requestDto, Principal principal) {
        manualService.submitToPending(manualId, requestDto, principal);
        return "マニュアルを承認待ち公開しました。";
    }
    //対応ボタン　マニュアルを公開(編集画面から遷移)
    @PostMapping("/{manualId}/actions/edit-to-pending")
    public String editToPending(@PathVariable Long manualId,@Valid ManualRequestDto requestDto, Principal principal) {
        manualService.editToPending(manualId, requestDto, principal);
        return "マニュアルを承認待ち公開しました。";
    }

//#endregion 新規タブ画面遷移
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
    @PostMapping("/{manualId}/actions/edit")
    public ManualResponseDto goToEditPage(@PathVariable Long manualId, Principal principal) {
        ManualResponseDto responseDto = manualService.goToEditPage(manualId, principal);
        return responseDto;
    }
    
//#endregion
//#region action
    //対応ボタン　承認（チェンジノート無）
    @PostMapping("/{manualId}/actions/approve")
    public String approveManual(@PathVariable Long manualId, Principal principal) {
        manualService.approveManual(manualId, principal);
        return "マニュアルを承認しました。";
    }
    //対応ボタン　承認（チェンジノート有）
    @PostMapping("/{manualId}/actions/approve-with-comment")
    public String approveManualWithComment(@PathVariable Long manualId,@Valid ManualActionRequestDto actionRequestDto, Principal principal) {
        manualService.approveManualWithComment(manualId,actionRequestDto.getChangeNote(),principal);
        return "マニュアルを承認しました。";
    }
    //対応ボタン　差し戻し（チェンジノート必須）
    @PostMapping("/{manualId}/actions/rollback")
    public String rollbackEditManual(@PathVariable Long manualId,@Valid ManualActionRequestDto actionRequestDto,Principal principal) {
        manualService.rollbackEditManual(manualId,actionRequestDto.getChangeNote(), principal);
        return "マニュアルを差し戻しました。";
    }
     //対応ボタン　アーカイブ（チェンジノート必須)
    @PostMapping("/{manualId}/actions/archive")
    public String archiveManual(@PathVariable Long manualId,@Valid ManualActionRequestDto actionRequestDto,Principal principal){
        manualService.archiveManual(manualId, actionRequestDto, principal);
        return "マニュアルをアーカイブしました。";

    }
    //対応ボタン　復帰（チェンジノート必須）
    @PostMapping("/{manualId}/actions/restore")
    public String restoreManual(@PathVariable Long manualId,ManualActionRequestDto actionRequestDto,Principal principal) {
        manualService.restoreManual(manualId,actionRequestDto.getChangeNote(),principal);
        return "マニュアルをアーカイブから復帰しました。";
        }
//#endregion 共通処理
    private String getLoginId(Principal principal) {
        String loginId = principal.getName();
        return loginId;
    }
//#endregion
}
