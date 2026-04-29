package com.example.manual.controller;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.manual.dto.ApproveRequestDto;
import com.example.manual.dto.CategoryResponseDto;
import com.example.manual.dto.ManualActionRequestDto;
import com.example.manual.dto.ManualDetailDto;
import com.example.manual.dto.ManualDraftDto;
import com.example.manual.dto.ManualEditFormDto;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.exception.NotFoundException;
import com.example.manual.exception.UnauthorizedException;
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
                        @Valid @ModelAttribute ManualDraftDto formDto,
                        BindingResult bindingResult,
                        Principal principal,
                        RedirectAttributes message,
                        Model model) {
                log.info("[{}][START] args={}");
                // @validエラー処理
                if (bindingResult.hasErrors()) {
                        String url = returnDraftManualCreateWithError(model, formDto, principal);
                        model.addAttribute("message", "必須項目が入力されていません。");
                        model.addAttribute("messageType", "error");
                        log.info("[{}][END] result={}");
                        return url;
                }
                // 既存処理
                try {
                        commandService.saveDraftForCreate(formDto, principal);
                } catch (UnauthorizedException | InvalidStateException | NotFoundException e) {
                        String url = returnDraftManualCreateWithError(model, formDto, principal);
                        model.addAttribute("message", e.getMessage());
                        model.addAttribute("messageType", "error");
                        log.info("[{}][END] result={}");
                        return url;
                }
                // 成功処理
                message.addFlashAttribute("message", "下書きを保存しました。");
                message.addFlashAttribute("messageType", "success");
                message.addFlashAttribute("manualListNeedsRefresh", true);
                message.addFlashAttribute("myPageNeedsRefresh", true);
                log.info("[{}][END] result={}");
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
                log.info("[{}][START] args={}");
                // @validエラー処理
                if (bindingResult.hasErrors()) {
                        String url = returnPendingManualCreateWithError(model, formDto, principal);
                        model.addAttribute("message", "必須項目が入力されていません。");
                        model.addAttribute("messageType", "error");
                        log.info("[{}][END] result={}");
                        return url;
                }
                // 既存処理
                try {
                        commandService.createPendingManual(formDto, principal);
                } catch (UnauthorizedException | InvalidStateException | NotFoundException e) {
                        String url = returnPendingManualCreateWithError(model, formDto, principal);
                        model.addAttribute("message", e.getMessage());
                        model.addAttribute("messageType", "error");
                        log.info("[{}][END] result={}");
                        return url;
                }
                // 成功処理
                message.addFlashAttribute("message", "マニュアルを公開しました。");
                message.addFlashAttribute("messageType", "success");
                message.addFlashAttribute("manualListNeedsRefresh", true);
                message.addFlashAttribute("myPageNeedsRefresh", true);
                log.info("[{}][END] result={}");
                return "redirect:/manuals/create";
        }

        // 下書き保存(複製)
        @PostMapping("/{manualId}/actions/save-draft-copy")
        public String saveDraftForCopy(
                        @PathVariable Long manualId,
                        @Valid @ModelAttribute ManualDraftDto formDto,
                        BindingResult bindingResult,
                        RedirectAttributes message,
                        Principal principal,
                        Model model) {
                log.info("[{}][START] args={}");
                // @validエラー処理
                if (bindingResult.hasErrors()) {
                        String url = returnDraftManualFormWithError(model, formDto, principal);
                        model.addAttribute("message", "必須項目が入力されていません。");
                        model.addAttribute("messageType", "error");
                        log.info("[{}][END] result={}");
                        return url;
                }
                // 既存処理
                try {
                        commandService.saveDraftForCopy(
                                        manualId,
                                        formDto,
                                        principal);
                } catch (UnauthorizedException | InvalidStateException | NotFoundException e) {
                        String url = returnDraftManualFormWithError(model, formDto, principal);
                        model.addAttribute("message", e.getMessage());
                        model.addAttribute("messageType", "error");
                        log.info("[{}][END] result={}");
                        return url;
                }
                // 成功処理
                message.addFlashAttribute(
                                "message", "複製マニュアルを下書きに保存しました");
                message.addFlashAttribute("messageType", "success");
                message.addFlashAttribute("manualListNeedsRefresh", true);
                message.addFlashAttribute("myPageNeedsRefresh", true);
                log.info("[{}][END] result={}");
                return "redirect:/manuals/{manualId}/actions/copy";
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
                log.info("[{}][START] args={}");
                // @validエラー処理
                if (bindingResult.hasErrors()) {
                        String url = returnPendingManualFormWithError(model, formDto, principal);
                        model.addAttribute("message", "必須項目が入力されていません。");
                        model.addAttribute("messageType", "error");
                        log.info("[{}][END] result={}");
                        return url;
                }
                // 既存処理
                try {
                        commandService.savePendingForCopy(
                                        manualId,
                                        formDto,
                                        principal);
                } catch (UnauthorizedException | InvalidStateException | NotFoundException e) {
                        String url = returnPendingManualFormWithError(model, formDto, principal);
                        model.addAttribute("message", e.getMessage());
                        model.addAttribute("messageType", "error");
                        log.info("[{}][END] result={}");
                        return url;
                }
                message.addFlashAttribute(
                                "message", "複製マニュアルを公開しました。");
                message.addFlashAttribute("messageType", "success");
                message.addFlashAttribute("manualListNeedsRefresh", true);
                message.addFlashAttribute("myPageNeedsRefresh", true);
                log.info("[{}][END] result={}");
                return "redirect:/manuals/{manualId}/actions/copy";
        }

        // 下書き保存(編集)
        @PostMapping({ "/{manualId}/actions/edit-toDraft", "/{manualId}/actions/save-draft-edit" })
        public String editToDraft(
                        @PathVariable Long manualId,
                        @Valid @ModelAttribute ManualDraftDto formDto,
                        BindingResult bindingResult,
                        RedirectAttributes message,
                        Principal principal,
                        Model model) {
                log.info("[{}][START] args={}");
                // @validエラー処理
                if (bindingResult.hasErrors()) {
                        String url = returnDraftManualFormWithError(model, formDto, principal);
                        model.addAttribute("message", "必須項目が入力されていません。");
                        model.addAttribute("messageType", "error");
                        log.info("[{}][END] result={}");
                        return url;
                }
                // 既存処理
                try {
                        commandService.editToDraft(
                                        manualId,
                                        formDto,
                                        principal);
                } catch (UnauthorizedException | InvalidStateException | NotFoundException e) {
                        String url = returnDraftManualFormWithError(model, formDto, principal);
                        model.addAttribute("message", e.getMessage());
                        model.addAttribute("messageType", "error");
                        log.info("[{}][END] result={}");
                        return url;
                }
                // 成功処理
                message.addFlashAttribute(
                                "message", "マニュアルを保存しました。");
                message.addFlashAttribute("messageType", "success");
                message.addFlashAttribute("manualListNeedsRefresh", true);
                message.addFlashAttribute("myPageNeedsRefresh", true);
                log.info("[{}][END] result={}");
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
                log.info("[{}][START] args={}");
                // @validエラー処理
                if (bindingResult.hasErrors()) {
                        String url = returnPendingManualFormWithError(model, formDto, principal);
                        model.addAttribute("message", "必須項目が入力されていません。");
                        model.addAttribute("messageType", "error");
                        log.info("[{}][END] result={}");
                        return url;
                }
                // 既存処理
                try {
                        commandService.editToPending(
                                        manualId,
                                        formDto,
                                        principal);
                } catch (UnauthorizedException | InvalidStateException | NotFoundException e) {
                        String url = returnPendingManualFormWithError(model, formDto, principal);
                        model.addAttribute("message", e.getMessage());
                        model.addAttribute("messageType", "error");
                        log.info("[{}][END] result={}");
                        return url;
                }
                // 成功処理
                message.addFlashAttribute(
                                "message", "マニュアルを公開しました。");
                message.addFlashAttribute("messageType", "success");
                message.addFlashAttribute("manualListNeedsRefresh", true);
                message.addFlashAttribute("myPageNeedsRefresh", true);
                log.info("[{}][END] result={}");
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
                        RedirectAttributes message,
                        Model model) {
                log.info("[{}][START] args={}");
                try {
                        commandService.submitManual(manualId, principal);
                } catch (UnauthorizedException | InvalidStateException | NotFoundException e) {
                        String url = returnManualDetailWithError(model, manualId, principal);
                        model.addAttribute("message", e.getMessage());
                        model.addAttribute("messageType", "error");
                        log.info("[{}][END] result={}");
                        return url;
                }
                // 成功処理
                message.addFlashAttribute(
                                "message", "マニュアルを公開しました。");
                message.addFlashAttribute("messageType", "success");
                message.addFlashAttribute("manualListNeedsRefresh", true);
                message.addFlashAttribute("myPageNeedsRefresh", true);
                log.info("[{}][END] result={}");
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
                log.info("[{}][START] args={}");
                // @validエラー処理
                if (bindingResult.hasErrors()) {
                        String url = returnManualDetailWithError(model, manualId, principal);
                        model.addAttribute("message", "必須項目が入力されていません。");
                        model.addAttribute("messageType", "error");
                        log.info("[{}][END] result={}");
                        return url;
                }
                // 既存処理
                try {
                        commandService.approveManual(
                                        manualId,
                                        approveRequestDto.getChangeNote(),
                                        principal);
                } catch (UnauthorizedException | InvalidStateException | NotFoundException e) {
                        String url = returnManualDetailWithError(model, manualId, principal);
                        model.addAttribute("message", e.getMessage());
                        model.addAttribute("messageType", "error");
                        log.info("[{}][END] result={}");
                        return url;
                }
                // 成功処理
                message.addFlashAttribute(
                                "message", "マニュアルを承認しました。");
                message.addFlashAttribute("messageType", "success");
                message.addFlashAttribute("manualListNeedsRefresh", true);
                message.addFlashAttribute("myPageNeedsRefresh", true);
                log.info("[{}][END] result={}");
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
                log.info("[{}][START] args={}");
                // @validエラー処理
                if (bindingResult.hasErrors()) {
                        String url = returnManualDetailWithError(model, manualId, principal);
                        model.addAttribute("message", "必須項目が入力されていません。");
                        model.addAttribute("messageType", "error");
                        log.info("[{}][END] result={}");
                        return url;
                }
                // 既存処理
                try {
                        commandService.rollbackEditManual(
                                        manualId,
                                        actionRequestDto,
                                        principal);
                } catch (UnauthorizedException | InvalidStateException | NotFoundException e) {
                        String url = returnManualDetailWithError(model, manualId, principal);
                        model.addAttribute("message", e.getMessage());
                        model.addAttribute("messageType", "error");
                        log.info("[{}][END] result={}");
                        return url;
                }
                // 成功処理
                message.addFlashAttribute(
                                "message", "マニュアルを差し戻しました。");
                message.addFlashAttribute("messageType", "success");
                message.addFlashAttribute("manualListNeedsRefresh", true);
                message.addFlashAttribute("myPageNeedsRefresh", true);
                log.info("[{}][END] result={}");
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
                log.info("[{}][START] args={}");
                // @validエラー処理
                if (bindingResult.hasErrors()) {
                        String url = returnManualDetailWithError(model, manualId, principal);
                        model.addAttribute("message", "必須項目が入力されていません。");
                        model.addAttribute("messageType", "error");
                        log.info("[{}][END] result={}");
                        return url;
                }
                // 既存処理
                try {
                        commandService.archiveManual(manualId,
                                        actionRequestDto,
                                        principal);
                } catch (UnauthorizedException | InvalidStateException | NotFoundException e) {
                        String url = returnManualDetailWithError(model, manualId, principal);
                        model.addAttribute("message", e.getMessage());
                        model.addAttribute("messageType", "error");
                        log.info("[{}][END] result={}");
                        return url;
                }
                // 成功処理
                message.addFlashAttribute(
                                "message", "マニュアルをアーカイブしました。");
                message.addFlashAttribute("messageType", "success");
                message.addFlashAttribute("manualListNeedsRefresh", true);
                message.addFlashAttribute("myPageNeedsRefresh", true);
                log.info("[{}][END] result={}");
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
                log.info("[{}][START] args={}");
                // @validエラー処理
                if (bindingResult.hasErrors()) {
                        String url = returnManualDetailWithError(model, manualId, principal);
                        model.addAttribute("message", "必須項目が入力されていません。");
                        model.addAttribute("messageType", "error");
                        log.info("[{}][END] result={}");
                        return url;
                }
                // 既存処理
                try {
                        commandService.restoreManual(
                                        manualId,
                                        actionRequestDto,
                                        principal);
                } catch (UnauthorizedException | InvalidStateException | NotFoundException e) {
                        String url = returnManualDetailWithError(model, manualId, principal);
                        model.addAttribute("message", e.getMessage());
                        model.addAttribute("messageType", "error");
                        log.info("[{}][END] result={}");
                        return url;
                }
                // 成功処理
                message.addFlashAttribute(
                                "message", "マニュアルをアーカイブから復帰しました。");
                message.addFlashAttribute("messageType", "success");
                message.addFlashAttribute("manualListNeedsRefresh", true);
                message.addFlashAttribute("myPageNeedsRefresh", true);
                log.info("[{}][END] result={}");
                return "redirect:/manuals/{manualId}/detail";
        }

        // ================================================
        // 共通処理
        // ================================================

        private String returnDraftManualCreateWithError(
                        Model model,
                        ManualDraftDto formDto,
                        Principal principal) {
                model.addAttribute("formDto", formDto);
                List<CategoryResponseDto> categoryDto = queryService.goToNewCreatePage(principal);
                model.addAttribute("categoryDto", categoryDto);
                log.info("[{}][END] result={}");
                return "manual-create";
        }

        private String returnPendingManualCreateWithError(
                        Model model,
                        ManualEditFormDto formDto,
                        Principal principal) {
                model.addAttribute("formDto", formDto);
                List<CategoryResponseDto> categoryDto = queryService.goToNewCreatePage(principal);
                model.addAttribute("categoryDto", categoryDto);
                log.info("[{}][END] result={}");
                return "manual-create";
        }

        private String returnDraftManualFormWithError(
                        Model model,
                        ManualDraftDto formDto,
                        Principal principal) {

                model.addAttribute("formDto", formDto);
                List<CategoryResponseDto> categoryDto = queryService.goToNewCreatePage(principal);
                model.addAttribute("categoryDto", categoryDto);
                log.info("[{}][END] result={}");
                return "manual-form";
        }

        private String returnPendingManualFormWithError(
                        Model model,
                        ManualEditFormDto formDto,
                        Principal principal) {

                model.addAttribute("formDto", formDto);
                List<CategoryResponseDto> categoryDto = queryService.goToNewCreatePage(principal);
                model.addAttribute("categoryDto", categoryDto);
                log.info("[{}][END] result={}");
                return "manual-form";
        }

        private String returnManualDetailWithError(
                        Model model,
                        Long manualId,
                        Principal principal) {
                ManualDetailDto detailDto = queryService.goToDetailPage(manualId, principal);
                model.addAttribute("detailDto", detailDto);

                log.info("[{}][END] result={}");
                return "manual-detail";
        }

}
