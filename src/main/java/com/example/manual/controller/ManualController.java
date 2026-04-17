package com.example.manual.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.manual.dto.ManualActionRequestDto;
import com.example.manual.dto.ManualDetailDto;
import com.example.manual.dto.ManualIndexDto;
import com.example.manual.dto.ManualRequestDto;
import com.example.manual.dto.ManualResponseDto;
import com.example.manual.dto.ManualSearchConditionDto;
import com.example.manual.enums.ManualStatus;
import com.example.manual.service.ManualService;

import jakarta.validation.Valid;


@Controller
@RequestMapping("/manuals")
public class ManualController {

        private static final Logger log = LoggerFactory.getLogger(ManualController.class);

private final ManualService manualService;

public ManualController(ManualService manualService) {
        this.manualService = manualService;
}

// index表示
@GetMapping("/index")
public String showIndex(
                @Valid @ModelAttribute ManualSearchConditionDto condition,
                Principal principal,
                Model model) {
        log.info("start");
        // 検索チェックボックスStatus初期設定（アーカイブ以外全選択）
        condition.setStatuses(defaultStatusCheck(condition));

        ManualIndexDto listDto = manualService.showIndex(principal, condition);
        log.info("listDto null? {}", listDto == null);
        log.info("activeCategories null? {}", listDto != null ? listDto.getActiveCategories() == null : null);
        model.addAttribute("listDto", listDto);
        log.info("listDto added to model");
        return "index";

}

//============================================
//登録・更新
//============================================

    //対応ボタン　下書き保存
    @PostMapping("/{manualId}/actions/save-draft")
    public String saveDraftForCreate(
            @PathVariable Long manualId,
            Principal principal,
            @Valid @ModelAttribute ManualRequestDto requestDto,
            RedirectAttributes redirectAttributes) {
            log.info("start");
        manualService.saveDraftForCreate(
                manualId,
                requestDto,
                principal);

        redirectAttributes.addFlashAttribute(
                "message", "マニュアルを下書きに保存しました");

        return "redirect:/manuals/{manualId}/actions/save-draft";
    }

    //対応ボタン　下書き保存(複製ボタンから遷移)
    @PostMapping("/{manualId}/actions/save-draft-copy")
    public String saveDraftForCopy(
            @PathVariable Long manualId,
            @Valid @ModelAttribute ManualRequestDto requestDto,
            RedirectAttributes redirectAttributes,
            Principal principal) {
        log.info("start");
        manualService.saveDraftForCopy(
                manualId,
                requestDto,
                principal);

        redirectAttributes.addFlashAttribute(
                "message", "複製マニュアルを下書きに保存しました");

        return "redirect:/manuals/{manualId}/actions/save-draft-copy";
    }

     //対応ボタン　マニュアルを公開（新規作成から遷移）
    @PostMapping("/{manualId}/actions/submit-pending")
    public String submitToPending(
            @PathVariable Long manualId,
            @Valid @ModelAttribute ManualRequestDto requestDto,
            RedirectAttributes redirectAttributes,
            Principal principal) {
            log.info("start");
        manualService.submitToPending(
                manualId,
                requestDto,
                principal);

        redirectAttributes.addFlashAttribute(
            "message", "マニュアルを承認待ち公開しました。");
        return "redirect:/manuals/{manualId}/actions/submit-pending";
    }
    //対応ボタン　マニュアルを公開(編集画面から遷移)
    @PostMapping("/{manualId}/actions/edit-to-pending")
    public String editToPending(
            @PathVariable Long manualId,
            @Valid @ModelAttribute ManualRequestDto requestDto,
            RedirectAttributes redirectAttributes,
            Principal principal) {
            log.info("start");
        manualService.editToPending(
                manualId,
                requestDto,
                principal);

        redirectAttributes.addFlashAttribute(
            "message", "マニュアルを承認待ち公開しました。");
        return "redirect:/manuals/{manualId}/actions/edit-to-pending";
    }


//============================================
//新規タブ画面遷移
//============================================

    //対応ボタン　詳細を見る（新規タブ）
    @GetMapping("/{manualId}/edit")
    public ManualDetailDto goToDetailPage(
            @PathVariable Long manualId,
            Principal principal) {
            log.info("start");
        ManualDetailDto detailDto = manualService.goToDetailPage(
                                    manualId,principal);
        return detailDto;
    }

    //対応ボタン　新規作成
    @GetMapping("/{manualId}/create")
    public String goToNewCreatePage(Principal principal){
        manualService.goToNewCreatePage(principal);
        log.info("start");
        return "/manual-create";
    }

     //対応ボタン　複製（新規タブ）
    @GetMapping("/{manualId}/actions/copy")
    public ManualResponseDto goToCopyPage(
            @PathVariable Long manualId,
            Principal principal) {
            log.info("start");
        ManualResponseDto responseDto = manualService.goToCopyPage(
                                        manualId, principal);
        return responseDto;
     }
     //対応ボタン　 編集（新規タブ）
    @GetMapping("/{manualId}/actions/edit")
    public ManualResponseDto goToEditPage(
            @PathVariable Long manualId,
            Principal principal) {
            log.info("start");
        ManualResponseDto responseDto = manualService.goToEditPage(
                                        manualId, principal);
        return responseDto;
    }


//============================================
//action
//============================================

    //対応ボタン　マニュアル公開（編集なし）
    @PostMapping("/{manualId}/actions/submit")
    public String submitManual(
            @PathVariable Long manualId,
            RedirectAttributes redirectAttributes) {
            log.info("start");
        manualService.submitManual(manualId);
        redirectAttributes.addFlashAttribute(
                "message", "マニュアルを公開しました。");

        return "redirect:/manuals/{manualId}/actions/submit";
    }

    //対応ボタン　承認（チェンジノート無）
    @PostMapping("/{manualId}/actions/approve")
    public String approveManual(
            @PathVariable Long manualId,
            RedirectAttributes redirectAttributes,
            Principal principal) {
            log.info("start");
        manualService.approveManual(manualId, principal);
        redirectAttributes.addFlashAttribute(
                "message", "マニュアルを承認しました。");
        return "redirect:/manuals/{manualId}";
    }
    //対応ボタン　承認（チェンジノート有）
    @PostMapping("/{manualId}/actions/approve-with-comment")
    public String approveManualWithComment(
            @PathVariable Long manualId,
            @Valid @ModelAttribute ManualActionRequestDto actionRequestDto,
            RedirectAttributes redirectAttributes,
            Principal principal) {
            log.info("start");
        manualService.approveManualWithComment(
                manualId,
                actionRequestDto.getChangeNote(),
                principal);
        redirectAttributes.addFlashAttribute(
                "message", "マニュアルを承認しました。");

        return "redirect:/manuals/{manualId}";
    }

    //対応ボタン　差し戻し（チェンジノート必須）
    @PostMapping("/{manualId}/actions/rollback")
    public String rollbackEditManual(
            @PathVariable Long manualId,
            @Valid @ModelAttribute ManualActionRequestDto actionRequestDto,
            RedirectAttributes redirectAttributes,
            Principal principal) {
            log.info("start");
        manualService.rollbackEditManual(
                manualId,
                actionRequestDto,
                principal);
        redirectAttributes.addFlashAttribute(
                "message", "マニュアルを差し戻しました。");

        return "redirect:/manuals/{manualId}";
    }

     //対応ボタン　アーカイブ（チェンジノート必須)
    @PostMapping("/{manualId}/actions/archive")
    public String archiveManual(
            @PathVariable Long manualId,
            @Valid @ModelAttribute ManualActionRequestDto actionRequestDto,
            RedirectAttributes redirectAttributes,
            Principal principal) {
            log.info("start");
        manualService.archiveManual(manualId,
                actionRequestDto,
                principal);
        redirectAttributes.addFlashAttribute(
                "message", "マニュアルをアーカイブしました。");
        return "redirect:/manuals/{manualId}";

    }
    //対応ボタン　復帰（チェンジノート必須）
    @PostMapping("/{manualId}/actions/restore")
    public String restoreManual(
            @PathVariable Long manualId,
            @Valid @ModelAttribute ManualActionRequestDto actionRequestDto,
            RedirectAttributes redirectAttributes,
            Principal principal) {
            log.info("start");
        manualService.restoreManual(
                manualId,
                actionRequestDto,
                principal);
        redirectAttributes.addFlashAttribute(
                "message", "マニュアルをアーカイブから復帰しました。");
        return "redirect:/manuals/{manualId}";
    }

//====================================
//検索・取得系
//====================================

public List<ManualResponseDto> searchManuals(
        @ModelAttribute ManualSearchConditionDto condition,
                Principal principal) {
        log.info("start");
        List<ManualResponseDto> manualDtoList = (List<ManualResponseDto>) manualService.searchManuals(
                        condition,
                        principal);

        return manualDtoList;
}

// ====================================
// 判定
// ====================================

        //検索チェックボックス初期設定
private List<ManualStatus> defaultStatusCheck(ManualSearchConditionDto condition) {
        List<ManualStatus> statuses = condition.getStatuses();
        log.info("start");
        if (statuses != null && !statuses.isEmpty()) {
                List<ManualStatus> targetStatuses = new ArrayList<>(statuses);
                targetStatuses.remove(ManualStatus.DRAFT);
                return targetStatuses;
        }
        List<ManualStatus> defaultStatuses = new ArrayList<>();
        defaultStatuses.add(ManualStatus.PENDING);
        defaultStatuses.add(ManualStatus.APPROVED);
        return defaultStatuses;
}
}
