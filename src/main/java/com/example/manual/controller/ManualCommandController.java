package com.example.manual.controller;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.manual.dto.ApproveRequestDto;
import com.example.manual.dto.CategoryResponseDto;
import com.example.manual.dto.ManualActionRequestDto;
import com.example.manual.dto.ManualDetailDto;
import com.example.manual.dto.ManualEditFormDto;
import com.example.manual.service.ManualCommandService;
import com.example.manual.service.ManualQueryService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/manuals")
public class ManualCommandController {
        private final ManualQueryService queryService;
        private final ManualCommandService commandService;

        private static final Logger log = LoggerFactory.getLogger(ManualCommandController.class);

        public ManualCommandController(ManualQueryService manualQueryService,
                        ManualCommandService manualCommandService) {
                this.commandService = manualCommandService;
                this.queryService = manualQueryService;
        }

        // ============================================
        // DB保存処理
        // ============================================

        // 新規作成DRAFT保存
        @PostMapping("/create/draft")
        public String saveDraftForCreate(
                        @Valid @ModelAttribute ManualEditFormDto formDto,
                        BindingResult bindingResult,
                        Principal principal,
                        RedirectAttributes message,
                        Model model) {
                log.info("start");
                if (bindingResult.hasErrors()) {
                        model.addAttribute("formDto", formDto);
                        List<CategoryResponseDto> categoryDto = queryService.goToNewCreatePage(principal);
                        model.addAttribute("categoryDto", categoryDto);
                        model.addAttribute("message", "必須項目が入力されていません。");
                        model.addAttribute("messageType", "error");
                        return "manual-create";
                }
                // 既存処理
                commandService.saveDraftForCreate(formDto, principal);
                message.addFlashAttribute("message", "下書きを保存しました。");
                message.addFlashAttribute("messageType", "success");
                return "redirect:/manuals/create";
        }

        // 新規作成PENDING公開
        @PostMapping("/create/pending")
        public String createPendingManual(
                        @Valid @ModelAttribute ManualEditFormDto formDto,
                        BindingResult bindingResult,
                        Principal principal,
                        RedirectAttributes message,
                        Model model) {
                log.info("start");
                if (bindingResult.hasErrors()) {
                        model.addAttribute("formDto", formDto);
                        List<CategoryResponseDto> categoryDto = queryService.goToNewCreatePage(principal);
                        model.addAttribute("categoryDto", categoryDto);
                        model.addAttribute("message", "必須項目が入力されていません。");
                        model.addAttribute("messageType", "error");
                        return "manual-create";
                }
                // 既存処理
                commandService.createPendingManual(formDto, principal);
                message.addFlashAttribute("message", "マニュアルを公開しました。");
                message.addFlashAttribute("messageType", "success");

                return "redirect:/manuals/create";
        }

        // 下書き保存(複製)
        @PostMapping("/{manualId}/actions/save-draft-copy")
        public String saveDraftForCopy(
                        @PathVariable Long manualId,
                        @Valid @ModelAttribute ManualEditFormDto formDto,
                        BindingResult bindingResult,
                        RedirectAttributes message,
                        Principal principal,
                        Model model) {
                log.info("start");
                if (bindingResult.hasErrors()) {
                        model.addAttribute("formDto", formDto);
                        List<CategoryResponseDto> categoryDto = queryService.goToNewCreatePage(principal);
                        model.addAttribute("categoryDto", categoryDto);
                        model.addAttribute("message", "必須項目が入力されていません。");
                        model.addAttribute("messageType", "error");
                        return "manual-form";
                }
                // 既存処理
                commandService.saveDraftForCopy(
                                manualId,
                                formDto,
                                principal);

                message.addFlashAttribute(
                                "message", "複製マニュアルを下書きに保存しました");
                message.addFlashAttribute("messageType", "success");
                return "redirect:/manuals/{manualId}/actions/save-draft-copy";
        }

        // マニュアルを公開(複製)
        @PostMapping("/{manualId}/actions/save-pending-copy")
        public String savePendingForCopy(
                        @PathVariable Long manualId,
                        @Valid @ModelAttribute ManualEditFormDto formDto,
                        BindingResult bindingResult,
                        RedirectAttributes message,
                        Principal principal,
                        Model model) {
                log.info("start");
                if (bindingResult.hasErrors()) {
                        model.addAttribute("formDto", formDto);
                        List<CategoryResponseDto> categoryDto = queryService.goToNewCreatePage(principal);
                        model.addAttribute("categoryDto", categoryDto);
                        model.addAttribute("message", "必須項目が入力されていません。");
                        model.addAttribute("messageType", "error");
                        return "manual-form";
                }
                // 既存処理
                commandService.savePendingForCopy(
                                manualId,
                                formDto,
                                principal);

                message.addFlashAttribute(
                                "message", "複製マニュアルを下書きに保存しました");
                message.addFlashAttribute("messageType", "success");
                return "redirect:/manuals/{manualId}/actions/save-draft-copy";
        }

        // 下書き保存(編集)
        @PostMapping({ "/{manualId}/actions/edit-toDraft", "/{manualId}/actions/save-draft-edit" })
        public String editToDraft(
                        @PathVariable Long manualId,
                        @Valid @ModelAttribute ManualEditFormDto formDto,
                        BindingResult bindingResult,
                        RedirectAttributes message,
                        Principal principal,
                        Model model) {
                log.info("start");
                if (bindingResult.hasErrors()) {
                        model.addAttribute("formDto", formDto);
                        List<CategoryResponseDto> categoryDto = queryService.goToNewCreatePage(principal);
                        model.addAttribute("categoryDto", categoryDto);
                        model.addAttribute("message", "必須項目が入力されていません。");
                        model.addAttribute("messageType", "error");
                        return "manual-form";
                }
                // 既存処理
                commandService.editToDraft(
                                manualId,
                                formDto,
                                principal);

                message.addFlashAttribute(
                                "message", "マニュアルを保存しました。");
                message.addFlashAttribute("messageType", "success");
                return "redirect:/manuals/{manualId}/detail";
        }

        // マニュアルを公開(編集)
        @PostMapping({ "/{manualId}/actions/edit-to-pending", "/{manualId}/actions/save-pending-edit" })
        public String editToPending(
                        @PathVariable Long manualId,
                        @Valid @ModelAttribute ManualEditFormDto formDto,
                        BindingResult bindingResult,
                        RedirectAttributes message,
                        Principal principal,
                        Model model) {
                log.info("start");
                if (bindingResult.hasErrors()) {
                        model.addAttribute("formDto", formDto);
                        List<CategoryResponseDto> categoryDto = queryService.goToNewCreatePage(principal);
                        model.addAttribute("categoryDto", categoryDto);
                        model.addAttribute("message", "必須項目が入力されていません。");
                        model.addAttribute("messageType", "error");
                        return "manual-form";
                }
                // 既存処理
                commandService.editToPending(
                                manualId,
                                formDto,
                                principal);

                message.addFlashAttribute(
                                "message", "マニュアルを公開しました。");
                message.addFlashAttribute("messageType", "success");
                return "redirect:/manuals/{manualId}/detail";
        }

        // ============================================
        // ワンボタン処理
        // ============================================

        // マニュアル公開
        @PostMapping("/{manualId}/actions/submit")
        public String submitManual(
                        @PathVariable Long manualId,
                        Principal principal,
                        RedirectAttributes message) {
                log.info("start");
                commandService.submitManual(manualId, principal);
                message.addFlashAttribute(
                                "message", "マニュアルを公開しました。");
                message.addFlashAttribute("messageType", "success");
                return "redirect:/manuals/{manualId}/detail";
        }

        // 承認
        @PostMapping("/{manualId}/actions/approve")
        public String approveManualWithComment(
                        @PathVariable Long manualId,
                        @Valid @ModelAttribute ApproveRequestDto approveRequestDto,
                        BindingResult bindingResult,
                        RedirectAttributes message,
                        Principal principal,
                        Model model) {
                log.info("start");
                if (bindingResult.hasErrors()) {
                        ManualDetailDto detailDto = queryService.goToDetailPage(manualId, principal);
                        model.addAttribute("detailDto", detailDto);
                        model.addAttribute("message", "必須項目が入力されていません。");
                        model.addAttribute("messageType", "error");
                        return "manual-detail";
                }
                // 既存処理
                commandService.approveManual(
                                manualId,
                                approveRequestDto.getChangeNote(),
                                principal);
                message.addFlashAttribute(
                                "message", "マニュアルを承認しました。");
                message.addFlashAttribute("messageType", "success");
                return "redirect:/manuals/{manualId}/detail";
        }

        // 差し戻し（チェンジノート必須）
        @PostMapping("/{manualId}/actions/rollback")
        public String rollbackEditManual(
                        @PathVariable Long manualId,
                        @Valid @ModelAttribute ManualActionRequestDto actionRequestDto,
                        BindingResult bindingResult,
                        RedirectAttributes message,
                        Principal principal,
                        Model model) {
                log.info("start");
                if (bindingResult.hasErrors()) {
                        ManualDetailDto detailDto = queryService.goToDetailPage(manualId, principal);
                        model.addAttribute("detailDto", detailDto);
                        model.addAttribute("message", "必須項目が入力されていません。");
                        model.addAttribute("messageType", "error");
                        return "manual-detail";
                }
                // 既存処理
                commandService.rollbackEditManual(
                                manualId,
                                actionRequestDto,
                                principal);
                message.addFlashAttribute(
                                "message", "マニュアルを差し戻しました。");
                message.addFlashAttribute("messageType", "success");
                return "redirect:/manuals/{manualId}/detail";
        }

        // アーカイブ（チェンジノート必須)
        @PostMapping("/{manualId}/actions/archive")
        public String archiveManual(
                        @PathVariable Long manualId,
                        @Valid @ModelAttribute ManualActionRequestDto actionRequestDto,
                        BindingResult bindingResult,
                        RedirectAttributes message,
                        Principal principal,
                        Model model) {
                log.info("start");
                if (bindingResult.hasErrors()) {
                        ManualDetailDto detailDto = queryService.goToDetailPage(manualId, principal);
                        model.addAttribute("detailDto", detailDto);
                        model.addAttribute("message", "必須項目が入力されていません。");
                        model.addAttribute("messageType", "error");
                        return "manual-detail";
                }
                // 既存処理
                commandService.archiveManual(manualId,
                                actionRequestDto,
                                principal);
                message.addFlashAttribute(
                                "message", "マニュアルをアーカイブしました。");
                message.addFlashAttribute("messageType", "success");
                return "redirect:/manuals/{manualId}/detail";
        }

        // 復帰（チェンジノート必須）
        @PostMapping("/{manualId}/actions/restore")
        public String restoreManual(
                        @PathVariable Long manualId,
                        @Valid @ModelAttribute ManualActionRequestDto actionRequestDto,
                        BindingResult bindingResult,
                        RedirectAttributes message,
                        Principal principal,
                        Model model) {
                log.info("start");
                if (bindingResult.hasErrors()) {
                        ManualDetailDto detailDto = queryService.goToDetailPage(manualId, principal);
                        model.addAttribute("detailDto", detailDto);
                        model.addAttribute("message", "必須項目が入力されていません。");
                        model.addAttribute("messageType", "error");
                        return "manual-detail";
                }
                // 既存処理
                commandService.restoreManual(
                                manualId,
                                actionRequestDto,
                                principal);
                message.addFlashAttribute(
                                "message", "マニュアルをアーカイブから復帰しました。");
                message.addFlashAttribute("messageType", "success");
                return "redirect:/manuals/{manualId}/detail";
        }

}
