package com.example.manual.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.manual.dto.CategoryResponseDto;
import com.example.manual.dto.ManualDetailDto;
import com.example.manual.dto.ManualEditFormDto;
import com.example.manual.dto.ManualIndexDto;
import com.example.manual.dto.ManualSearchConditionDto;
import com.example.manual.entity.Manual;
import com.example.manual.enums.FormMode;
import com.example.manual.enums.ManualStatus;
import com.example.manual.service.ManualQueryService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/manuals")
public class ManualQueryController {
    private final ManualQueryService queryService;

    private static final Logger log = LoggerFactory.getLogger(ManualQueryController.class);

    public ManualQueryController(ManualQueryService manualQueryService) {
        this.queryService = manualQueryService;
    }

    // ============================================
    // 画面表示
    // ============================================

    // index表示
    @GetMapping("/index")
    public String showIndex(
            @Valid @ModelAttribute ManualSearchConditionDto condition,
            BindingResult bindingResult,
            Principal principal,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {
        // 検索チェックボックスStatus初期設定（アーカイブ以外全選択）
        condition.setStatuses(defaultStatusCheck(condition));
        ManualIndexDto listDto = queryService.showIndex(
                principal,
                queryService.findManualsBySearch(
                        condition, principal, pageable),
                condition);
        model.addAttribute("listDto", listDto);
        return "index";

    }

    // 自分の作成マニュアル一覧表示
    @GetMapping("/index/my-created")
    public String showMyManuals(
            @Valid @ModelAttribute ManualSearchConditionDto condition,
            BindingResult bindingResult,
            Principal principal,
            Model model,
            RedirectAttributes message,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<Manual> manuals = queryService.findMyCreatedManualsPage(principal, pageable);
        ManualIndexDto listDto = queryService.showIndex(principal, manuals, condition);
        model.addAttribute("listDto", listDto);
        return "index";
    }

    // 自分の申請中マニュアル一覧表示
    @GetMapping("/index/my-pending")
    public String showCreatedPendingManuals(
            @Valid @ModelAttribute ManualSearchConditionDto condition,
            BindingResult bindingResult,
            Principal principal,
            Model model,
            RedirectAttributes message,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<Manual> manuals = queryService.findMyPendingManuals(principal, pageable);
        ManualIndexDto listDto = queryService.showIndex(principal, manuals, condition);
        model.addAttribute("listDto", listDto);
        return "index";
    }

    // 最近更新のマニュアル一覧表示
    @GetMapping("/index/new-updatedAt")
    public String showRecentWeeklyManuals(
            @Valid @ModelAttribute ManualSearchConditionDto condition,
            BindingResult bindingResult,
            Principal principal,
            Model model,
            RedirectAttributes message,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<Manual> manuals = queryService.findRecentlyUpdatedManuals(pageable);
        ManualIndexDto listDto = queryService.showIndex(principal, manuals, condition);
        model.addAttribute("listDto", listDto);
        return "index";
    }

    // ============================================
    // 新規タブ画面遷移
    // ============================================

    // 詳細を見る（新規タブ）
    @GetMapping("/{manualId}/detail")
    public String goToDetailPage(
            @PathVariable Long manualId,
            Principal principal,
            Model model) {
        ManualDetailDto detailDto = queryService.goToDetailPage(
                manualId, principal);
        model.addAttribute("detailDto", detailDto);
        return "manual-detail";
    }

    // 新規作成（新規タブ）
    @GetMapping("/create")
    public String goToNewCreatePage(Principal principal, Model model) {
        List<CategoryResponseDto> categoryDto = queryService.goToNewCreatePage(principal);
        ManualEditFormDto formDto = new ManualEditFormDto();
        model.addAttribute("categoryDto", categoryDto);
        model.addAttribute("formDto", formDto);
        return "manual-create";
    }

    // 複製（新規タブ）
    @GetMapping("/{manualId}/actions/copy")
    public String goToCopyPage(
            @PathVariable Long manualId,
            Principal principal,
            Model model) {
        ManualEditFormDto formDto = queryService.goToCopyPage(
                manualId, principal);
        String pendingSubmit = "/manuals/" + manualId + "/actions/save-pending-copy";
        String draftSubmit = "/manuals/" + manualId + "/actions/save-draft-copy";
        formDto.setMode(FormMode.copy);
        formDto.setPendingSubmit(pendingSubmit);
        formDto.setDraftSubmit(draftSubmit);
        model.addAttribute("formDto", formDto);
        List<CategoryResponseDto> categoryDto = queryService.goToNewCreatePage(principal);
        model.addAttribute("categoryDto", categoryDto);

        return "manual-form";
    }

    // 編集（新規タブ）
    @GetMapping("/{manualId}/actions/edit")
    public String goToEditPage(
            @PathVariable Long manualId,
            Principal principal,
            Model model) {
        ManualEditFormDto formDto = queryService.goToEditPage(
                manualId, principal);
        String pendingSubmit = "/manuals/" + manualId + "/actions/save-pending-edit";
        String draftSubmit = "/manuals/" + manualId + "/actions/save-draft-edit";
        formDto.setPendingSubmit(pendingSubmit);
        formDto.setDraftSubmit(draftSubmit);
        model.addAttribute("formDto", formDto);
        List<CategoryResponseDto> categoryDto = queryService.goToNewCreatePage(principal);
        model.addAttribute("categoryDto", categoryDto);
        return "manual-form";
    }

    // ====================================
    // 判定
    // ====================================

    // 検索チェックボックス初期設定
    private List<ManualStatus> defaultStatusCheck(ManualSearchConditionDto condition) {
        List<ManualStatus> statuses = condition.getStatuses();
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
