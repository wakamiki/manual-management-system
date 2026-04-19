package com.example.manual.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.manual.dto.CategoryResponseDto;
import com.example.manual.dto.ManualActionRequestDto;
import com.example.manual.dto.ManualDetailDto;
import com.example.manual.dto.ManualDraftRequestDto;
import com.example.manual.dto.ManualIndexDto;
import com.example.manual.dto.ManualRequestDto;
import com.example.manual.dto.ManualResponseDto;
import com.example.manual.dto.ManualSearchConditionDto;
import com.example.manual.entity.Manual;
import com.example.manual.enums.ManualStatus;
import com.example.manual.service.ManualService;
import com.example.manual.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/manuals")
public class ManualController {

        private static final Logger log = LoggerFactory.getLogger(ManualController.class);

        private final UserService userService;

        private final ManualService manualService;

        public ManualController(ManualService manualService, UserService userService) {
                this.manualService = manualService;
                this.userService = userService;
        }

        // ============================================
        // 画面表示
        // ============================================

        // index表示
        @GetMapping("/index")
        public String showIndex(
                        @Valid @ModelAttribute ManualSearchConditionDto condition,
                        Principal principal,
                        Model model) {
                log.info("start");
                // 検索チェックボックスStatus初期設定（アーカイブ以外全選択）
                condition.setStatuses(defaultStatusCheck(condition));

                ManualIndexDto listDto = manualService.showIndex(
                                principal,
                                manualService.findManualsBySearch(
                                                condition, principal));
                log.info("listDto null? {}", listDto == null);
                log.info("activeCategories null? {}", listDto != null ? listDto.getActiveCategories() == null : null);
                model.addAttribute("listDto", listDto);
                log.info("listDto added to model");
                return "index";

        }

        // 自分の作成マニュアル一覧表示
        @GetMapping("/index/my-created")
        public String showMyManuals(@Valid @ModelAttribute ManualSearchConditionDto condition,
                        Principal principal,
                        Model model,
                        RedirectAttributes message) {
                log.info("start");
                try {
                        List<Manual> manuals = manualService.findMyCreatedManuals(principal);
                        ManualIndexDto listDto = manualService.showIndex(principal, manuals);
                        model.addAttribute("listDto", listDto);
                        return "index";
                } catch (Exception e) {
                        message.addFlashAttribute("message", "マニュアルを表示できませんでした。");
                        message.addFlashAttribute("messageType", "error");
                        return "index";
                }
        }

        // 自分の申請中マニュアル一覧表示
        @GetMapping("/index/my-pending")
        public String showCreatedPendingManuals(@Valid @ModelAttribute ManualSearchConditionDto condition,
                        Principal principal,
                        Model model,
                        RedirectAttributes message) {
                log.info("start");
                try {
                        List<Manual> manuals = manualService.findMyPendingManuals(principal);
                        ManualIndexDto listDto = manualService.showIndex(principal, manuals);
                        model.addAttribute("listDto", listDto);
                        return "index";
                } catch (Exception e) {
                        message.addFlashAttribute("message", "マニュアルを表示できませんでした。");
                        message.addFlashAttribute("messageType", "error");
                        return "index";
                }
        }

        // 最近更新のマニュアル一覧表示
        @GetMapping("/index/new-updatedAt")
        public String showRecentWeeklyManuals(@Valid @ModelAttribute ManualSearchConditionDto condition,
                        Principal principal,
                        Model model,
                        RedirectAttributes message) {
                log.info("start");
                try {
                        List<Manual> manuals = manualService.findRecentlyUpdatedManuals();
                        ManualIndexDto listDto = manualService.showIndex(principal, manuals);
                        model.addAttribute("listDto", listDto);
                        return "index";
                } catch (Exception e) {
                        message.addFlashAttribute("message", "マニュアルを表示できませんでした。");
                        message.addFlashAttribute("messageType", "error");
                        return "index";
                }
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
                log.info("start");
                ManualDetailDto detailDto = manualService.goToDetailPage(
                                manualId, principal);
                model.addAttribute("detailDto", detailDto);
                return "manual-detail";
        }

        // 新規作成（新規タブ）
        @GetMapping("/create")
        public String goToNewCreatePage(Principal principal, Model model) {
                log.info("start");
                List<CategoryResponseDto> categoryDto = manualService.goToNewCreatePage(principal);
                model.addAttribute("categoryDto", categoryDto);
                return "manual-create";
        }

        // 複製（新規タブ）
        @GetMapping("/{manualId}/actions/copy")
        public ManualResponseDto goToCopyPage(
                        @PathVariable Long manualId,
                        Principal principal) {
                log.info("start");
                ManualResponseDto responseDto = manualService.goToCopyPage(
                                manualId, principal);
                return responseDto;
        }

        // 編集（新規タブ）
        @GetMapping("/{manualId}/actions/edit")
        public ManualResponseDto goToEditPage(
                        @PathVariable Long manualId,
                        Principal principal) {
                log.info("start");
                ManualResponseDto responseDto = manualService.goToEditPage(
                                manualId, principal);
                return responseDto;
        }

        // ============================================
        // DB保存処理
        // ============================================

        // 新規作成DRAFT保存
        @PostMapping("/create/draft")
        public String saveDraftForCreate(
                        @Valid @ModelAttribute ManualDraftRequestDto requestDto,
                        Principal principal,
                        RedirectAttributes message) {
                try {
                        log.info("start");
                        manualService.saveDraftForCreate(requestDto, principal);
                        message.addFlashAttribute("message", "下書きを保存しました。");
                        message.addFlashAttribute("messageType", "success");
                        return "redirect:/manuals/create";
                } catch (Exception e) {
                        message.addFlashAttribute("message", "保存に失敗しました。");
                        message.addFlashAttribute("messageType", "error");
                        return "redirect:/manuals/create";
                }
        }

        // 新規作成PENDING公開
        // @PostMapping("/create/pending")
        // public String createPendingManual(
        // @Valid @ModelAttribute ManualRequestDto requestDto,
        // Principal principal,
        // RedirectAttributes message){
        // log.info("start");
        // try{
        // manualService.createPendingManual(requestDto,principal);
        // message.addFlashAttribute("message","マニュアルを公開しました。");
        // message.addFlashAttribute("messageType","success");

        // return "redirect:/manuals/create";
        // }catch(Exception e){
        // message.addFlashAttribute("message","公開に失敗しました。");
        // message.addFlashAttribute("messageType","error");
        // return "redirect:/manuals/create";
        // }
        // }

        // 下書き保存(複製ボタンから遷移)
        @PostMapping("/{manualId}/actions/save-draft-copy")
        public String saveDraftForCopy(
                        @PathVariable Long manualId,
                        @Valid @ModelAttribute ManualRequestDto requestDto,
                        RedirectAttributes message,
                        Principal principal) {
                log.info("start");
                try {
                        manualService.saveDraftForCopy(
                                        manualId,
                                        requestDto,
                                        principal);

                        message.addFlashAttribute(
                                        "message", "複製マニュアルを下書きに保存しました");
                        message.addFlashAttribute("messageType", "success");
                        return "redirect:/manuals/{manualId}/actions/save-draft-copy";
                } catch (Exception e) {
                        message.addFlashAttribute("message", "複製マニュアルを保存できませんでした。");
                        message.addFlashAttribute("messageType", "error");
                        return "redirect:/manuals/{manualId}/actions/save-draft-copy";
                }
        }

        // マニュアルを公開（新規作成から遷移）
        @PostMapping("/{manualId}/actions/submit-pending")
        public String submitToPending(
                        @PathVariable Long manualId,
                        @Valid @ModelAttribute ManualRequestDto requestDto,
                        RedirectAttributes message,
                        Principal principal) {
                log.info("start");
                try {

                        message.addFlashAttribute(
                                        "message", "マニュアルを公開しました。");
                        message.addFlashAttribute("messageType", "success");
                        return "redirect:/manuals/{manualId}/actions/submit-pending";
                } catch (Exception e) {
                        message.addFlashAttribute("message", "マニュアルを公開できませんでした。");
                        message.addFlashAttribute("messageType", "error");
                        return "redirect:/manuals/{manualId}/actions/submit-pending";
                }
        }

        // マニュアルを公開(編集画面から遷移)
        @PostMapping("/{manualId}/actions/edit-to-pending")
        public String editToPending(
                        @PathVariable Long manualId,
                        @Valid @ModelAttribute ManualRequestDto requestDto,
                        RedirectAttributes message,
                        Principal principal) {
                log.info("start");
                try {
                        manualService.editToPending(
                                        manualId,
                                        requestDto,
                                        principal);

                        message.addFlashAttribute(
                                        "message", "マニュアルを公開しました。");
                        message.addFlashAttribute("messageType", "success");
                        return "redirect:/manuals/{manualId}/actions/edit-to-pending";
                } catch (Exception e) {
                        message.addFlashAttribute("message", "マニュアルを公開できませんでした。");
                        message.addFlashAttribute("messageType", "error");
                        return "redirect:/manuals/{manualId}/actions/edit-to-pending";
                }
        }

        // 下書き保存
        @PostMapping("/{manualId}/actions/save-draft")
        public String saveDraftForCreate(
                        @PathVariable Long manualId,
                        Principal principal,
                        @Valid @ModelAttribute ManualRequestDto requestDto,
                        RedirectAttributes message) {
                log.info("start");
                try {
                        message.addFlashAttribute(
                                        "message", "マニュアルを下書きに保存しました");
                        message.addFlashAttribute("messageType", "success");
                        return "redirect:/manuals/{manualId}/actions/save-draft";
                } catch (Exception e) {
                        message.addFlashAttribute("message", "マニュアルを保存できませんでした。");
                        message.addFlashAttribute("messageType", "error");
                        return "redirect:/manuals/{manualId}/actions/save-draft";
                }
        }

        // マニュアル公開（編集なしワンボタン）
        @PostMapping("/{manualId}/actions/submit")
        public String submitManual(
                        @PathVariable Long manualId,
                        RedirectAttributes message) {
                log.info("start");
                try {
                        message.addFlashAttribute(
                                        "message", "マニュアルを公開しました。");
                        message.addFlashAttribute("messageType", "success");
                        return "redirect:/manuals/{manualId}/actions/submit";
                } catch (Exception e) {
                        message.addFlashAttribute("message", "マニュアルを公開できませんでした。");
                        message.addFlashAttribute("messageType", "error");
                        return "index";
                }
        }

        // 承認（チェンジノート無）
        // @PostMapping("/{manualId}/actions/approve")
        // public String approveManual(
        // @PathVariable Long manualId,
        // RedirectAttributes message,
        // Principal principal) {
        // log.info("start");
        // try {
        // manualService.approveManual(manualId, principal);
        // message.addFlashAttribute(
        // "message", "マニュアルを承認しました。");
        // message.addFlashAttribute("messageType", "success");
        // return "redirect:/manuals/";
        // } catch (Exception e) {
        // message.addFlashAttribute("message", "マニュアルを承認できませんでした。");
        // message.addFlashAttribute("messageType", "error");
        // return "redirect:/manuals/index";
        // }
        // }
        // 承認（チェンジノート有）
        @PostMapping("/{manualId}/actions/approve-with-comment")
        public String approveManualWithComment(
                        @PathVariable Long manualId,
                        @Valid @ModelAttribute ManualActionRequestDto actionRequestDto,
                        RedirectAttributes message,
                        Principal principal) {
                log.info("start");
                try {
                        manualService.approveManualWithComment(
                                        manualId,
                                        actionRequestDto.getChangeNote(),
                                        principal);
                        message.addFlashAttribute(
                                        "message", "マニュアルを承認しました。");
                        message.addFlashAttribute("messageType", "success");
                        return "redirect:/manuals/{manualId}";
                } catch (Exception e) {
                        message.addFlashAttribute("message", "マニュアルを承認できませんでした。");
                        message.addFlashAttribute("messageType", "error");
                        return "index";
                }
        }

        // 差し戻し（チェンジノート必須）
        // @PostMapping("/{manualId}/actions/rollback")
        // public String rollbackEditManual(
        // @PathVariable Long manualId,
        // @Valid @ModelAttribute ManualActionRequestDto actionRequestDto,
        // RedirectAttributes message,
        // Principal principal) {
        // log.info("start");
        // try {
        // manualService.rollbackEditManual(
        // manualId,
        // actionRequestDto,
        // principal);
        // message.addFlashAttribute(
        // "message", "マニュアルを差し戻しました。");
        // message.addFlashAttribute("messageType", "success");
        // return "redirect:/manuals/{manualId}";
        // } catch (Exception e) {
        // message.addFlashAttribute("message", "処理中にエラーが発生しました。");
        // message.addFlashAttribute("messageType", "error");
        // return "index";
        // }
        // }

        // アーカイブ（チェンジノート必須)
        // @PostMapping("/{manualId}/actions/archive")
        // public String archiveManual(
        // @PathVariable Long manualId,
        // @Valid @ModelAttribute ManualActionRequestDto actionRequestDto,
        // RedirectAttributes message,
        // Principal principal) {
        // log.info("start");
        // try {
        // manualService.archiveManual(manualId,
        // actionRequestDto,
        // principal);
        // message.addFlashAttribute(
        // "message", "マニュアルをアーカイブしました。");
        // message.addFlashAttribute("messageType", "success");
        // return "redirect:/manuals/{manualId}";
        // } catch (Exception e) {
        // message.addFlashAttribute("message", "処理中にエラーが発生しました。");
        // message.addFlashAttribute("messageType", "error");
        // return "index";
        // }

        // }

        // 復帰（チェンジノート必須）
        // @PostMapping("/{manualId}/actions/restore")
        // public String restoreManual(
        // @PathVariable Long manualId,
        // @Valid @ModelAttribute ManualActionRequestDto actionRequestDto,
        // RedirectAttributes message,
        // Principal principal) {
        // log.info("start");
        // try {
        // manualService.restoreManual(
        // manualId,
        // actionRequestDto,
        // principal);
        // message.addFlashAttribute(
        // "message", "マニュアルをアーカイブから復帰しました。");
        // message.addFlashAttribute("messageType", "success");
        // return "redirect:/manuals/{manualId}";
        // } catch (Exception e) {
        // message.addFlashAttribute("message", "処理中にエラーが発生しました。");
        // message.addFlashAttribute("messageType", "error");
        // return "index";
        // }
        // }

        // ====================================
        // 判定
        // ====================================

        // 検索チェックボックス初期設定
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
