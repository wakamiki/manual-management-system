package com.example.manual.service;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.manual.dto.ManualEditFormDto;
import com.example.manual.entity.Category;
import com.example.manual.entity.Manual;
import com.example.manual.entity.User;
import com.example.manual.enums.ManualStatus;
import com.example.manual.enums.UserRole;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.exception.UnauthorizedException;

@Service
public class ManualPermissionService {
    private static final Logger log = LoggerFactory.getLogger(ManualPermissionService.class);

    private final CategoryService categoryService;

    public ManualPermissionService(
            CategoryService categoryService) {

        this.categoryService = categoryService;
    }

    // ============================
    // ボタン表示非表示判定
    // ============================

    public boolean canRestore(User playUser, Manual manual) {
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isApproverOrAdmin(playUser)) {
            return false;
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isStatusArchived(manual)) {
            return false;
        }
        return true;
    }

    public boolean canCopy(Manual manual) {
        log.info("[{}][PERMISSION][START] rule={}");
        if (!manual.getCategory().isActive()) {
            return false;
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isStatusPending(manual) &&
                !isStatusApproved(manual) &&
                !isStatusArchived(manual)) {
            return false;
        }
        return true;
    }

    public boolean canApprove(Manual manual, User playUser) {
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isApproverOrAdmin(playUser)) {
            return false;
        }
        // 作成者じゃない時のみOK
        log.info("[{}][PERMISSION][START] rule={}");
        if (isOwner(manual.getCreatedByUser(), playUser)) {
            return false;
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isStatusPending(manual)) {
            return false;
        }
        return true;
    }

    public boolean canRollback(Manual manual, User playUser) {
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isApproverOrAdmin(playUser)) {
            return false;
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (isOwner(manual.getCreatedByUser(), playUser)) {
            return false;
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isStatusPending(manual)) {
            return false;
        }
        return true;
    }

    public boolean PublishDtaftManual(Manual manual, User playUser) {
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isStatusDraft(manual)) {
            return false;
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isOwner(manual.getCreatedByUser(), playUser)) {
            return false;
        }
        return true;
    }

    public boolean canEditManual(Manual manual, User playUser) {
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isOwner(manual.getCreatedByUser(), playUser)) {
            return false;
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isStatusDraft(manual) &&
                !isStatusPending(manual)) {
            return false;
        }
        return true;
    }

    public boolean canArchive(Manual manual, User playUser) {
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isApproverOrAdmin(playUser)) {
            return false;
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isStatusApproved(manual) && !isStatusPending(manual) && !isStatusDraft(manual)) {
            return false;
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isTitleAndContent(manual.getTitle(), manual.getContent())) {
            return false;
        }
        return true;
    }

    public boolean canPending(Manual manual, User playUser) {
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isOwner(manual.getCreatedByUser(), playUser)) {
            return false;
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isStatusDraft(manual)) {
            return false;
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isTitleAndContent(manual.getTitle(), manual.getContent())) {
            return false;
        }
        return true;
    }

    // ============================
    // 権限判定
    // ============================

    public boolean canGoToDetailPage(User playUser) {

        // アクティブユーザー
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        return true;
    }

    public boolean canGoToCopyPage(User playUser) {

        // アクティブユーザー
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        return true;
    }

    public boolean canFindManualsBySearch(User playUser) {

        // アクティブユーザー
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        return true;
    }

    public boolean canGoToEditPage(
            User playUser,
            Manual manual) {

        Category category = categoryService.getCategoryById(manual.getCategory().getId());
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isCategoryActivate(category)) {
            throw new InvalidStateException(
                    "使用中カテゴリでのみ復帰が出来ます。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isStatusPending(manual) &&
                !isStatusDraft(manual)) {
            throw new InvalidStateException(
                    "編集ができるのはステータス:DRAFT/PENDINGのマニュアルのみです。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isOwner(manual.getCreatedByUser(), playUser)) {
            throw new InvalidStateException(
                    "編集ができるのは自分が作成したマニュアルだけです。");
        }
        return true;
    }

    public boolean canGoToNewCreatePage(User playUser) {

        log.info("[{}][PERMISSION][START] rule={}");
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        return true;
    }

    public boolean canArchiveManual(
            User playUser,
            Manual manual,
            Category category,
            String changeNote) {

        log.info("[{}][PERMISSION][START] rule={}");
        if (!isApproverOrAdmin(playUser)) {
            throw new UnauthorizedException("権限が不足しています。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isStatusDraft(manual) &&
                !isStatusPending(manual) &&
                !isStatusApproved(manual)) {
            throw new InvalidStateException("マニュアルのステータスが条件を満たしていません。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isChangeNote(changeNote)) {
            throw new InvalidStateException("更新期歴は必須です。");
        }
        return true;
    }

    // TODO: 通知実装予定
    public boolean canApproveManual(
            Manual manual,
            Category category,
            User playUser) {

        log.info("[{}][PERMISSION][START] rule={}");
        if (!isApproverOrAdmin(playUser)) {
            throw new UnauthorizedException("権限が不足しています。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isStatusPending(manual)) {
            throw new InvalidStateException(
                    "承認ができるのはステータス:PENDINGのマニュアルのみです。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (isOwner(manual.getCreatedByUser(), playUser)) {
            throw new InvalidStateException(
                    "自分が作成したマニュアルの承認をすることは出来ません。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!category.isActive()) {
            throw new InvalidStateException(
                    "有効でないカテゴリーでは承認することが出来ません。");
        }
        return true;
    }

    public boolean canRollbackManual(
            Manual manual,
            User playUser,
            String changeNote) {

        log.info("[{}][PERMISSION][START] rule={}");
        if (!isApproverOrAdmin(playUser)) {
            throw new UnauthorizedException("権限が不足しています。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isStatusPending(manual)) {
            throw new InvalidStateException(
                    "差し戻しができるのはステータス:PENDINGのマニュアルのみです。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (isOwner(manual.getCreatedByUser(), playUser)) {
            throw new InvalidStateException(
                    "自分が作成したマニュアルを差し戻しすることは出来ません。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isChangeNote(changeNote)) {
            throw new InvalidStateException("更新期歴は必須です。");
        }
        return true;
    }

    public boolean canRestoreManual(
            Manual manual,
            User playUser,
            String changeNote) {

        log.info("[{}][PERMISSION][START] rule={}");
        if (!isApproverOrAdmin(playUser)) {
            throw new UnauthorizedException("権限が不足しています。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isStatusArchived(manual)) {
            throw new InvalidStateException(
                    "復帰ができるのはステータス:ARCHIVEDのマニュアルのみです。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isActive(playUser)) {
            throw new InvalidStateException(
                    "使用中カテゴリでのみ復帰が出来ます。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isChangeNote(changeNote)) {
            throw new InvalidStateException("更新履歴は必須です。");
        }
        return true;
    }

    public boolean canSaveDraftForCreate(
            User playUser,
            Manual manual,
            Category category) {

        log.info("[{}][PERMISSION][START] rule={}");
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isCategoryActivate(category)) {
            throw new InvalidStateException(
                    "選択できるのは使用中カテゴリのみです。");
        }
        return true;
    }

    public boolean canCreatePendingManual(
            User playUser,
            Category category,
            ManualEditFormDto formDto) {

        log.info("[{}][PERMISSION][START] rule={}");
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isCategoryActivate(category)) {
            throw new InvalidStateException(
                    "使用停止中のカテゴリーが選択されています。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isTitleAndContent(formDto.getTitle(), formDto.getContent())) {
            throw new InvalidStateException("必須項目が入力されていません。");
        }
        return true;
    }

    public boolean canSaveDraftForCopy(
            User playUser,
            Category category,
            String changeNote) {

        // アクティブユーザー アクティブカテゴリー チェンジノート必須
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isCategoryActivate(category)) {
            throw new InvalidStateException(
                    "停止中カテゴリにマニュアルは作成できません。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isChangeNote(changeNote)) {
            throw new InvalidStateException("更新履歴は必須です。");
        }
        return true;
    }

    public boolean canSavePendingForCopy(
            User playUser,
            Category category,
            String changeNote,
            String title, String content) {

        // アクティブユーザー アクティブカテゴリー チェンジノート必須 タイトル・コンテンツ必須
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isCategoryActivate(category)) {
            throw new InvalidStateException(
                    "停止中カテゴリにマニュアルは作成できません。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isChangeNote(changeNote)) {
            throw new InvalidStateException("更新履歴は必須です。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isTitleAndContent(title, content)) {
            throw new InvalidStateException("タイトル・本文は必須です。");
        }
        return true;
    }

    public boolean canEditToPending(
            User playUser,
            Manual manual, ManualEditFormDto formDto) {

        // アクティブユーザー ステータスドラフト・ペンディングのみ 作成者のみ編集可 アクティブカテゴリ
        Category category = categoryService.getCategoryById(manual.getCategory().getId());
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isCategoryActivate(category)) {
            throw new InvalidStateException(
                    "停止中カテゴリにマニュアルは作成できません。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isStatusPending(manual) && !isStatusDraft(manual)) {
            throw new InvalidStateException(
                    "編集ができるのはステータス:DRAFT/PENDINGのマニュアルのみです。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isOwner(manual.getCreatedByUser(), playUser)) {
            throw new InvalidStateException(
                    "自分が作成したマニュアル以外を編集することはできません。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isTitleAndContent(formDto.getTitle(), formDto.getContent())) {
            throw new InvalidStateException(
                    "タイトルと本文は必須項目です。");
        }
        return true;
    }

    public boolean canEditToDraft(
            User playUser,
            Manual manual) {

        // アクティブユーザー ステータスドラフト・ペンディングのみ 作成者のみ編集可 アクティブカテゴリ
        Category category = categoryService.getCategoryById(manual.getCategory().getId());
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isCategoryActivate(category)) {
            throw new InvalidStateException(
                    "停止中カテゴリにマニュアルは作成できません。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isStatusPending(manual) && !isStatusDraft(manual)) {
            throw new InvalidStateException(
                    "編集ができるのはステータス:DRAFT/PENDINGのマニュアルのみです。");
        }
        log.info("[{}][PERMISSION][START] rule={}");
        if (!isOwner(manual.getCreatedByUser(), playUser)) {
            throw new InvalidStateException(
                    "自分が作成したマニュアル以外を編集することはできません。");
        }
        return true;
    }

    // ============================
    // 権限共通処理
    // ============================

    private boolean isActive(User playUser) {
        log.info("[{}][PERMISSION][START] rule={}");
        if (playUser.isActive()) {
            return true;
        }
        return false;
    }

    private boolean isUserActive(User playUser) {
        log.info("[{}][PERMISSION][START] rule={}");
        if (playUser.isActive() == true) {
            return true;
        }
        return false;
    }

    private boolean isOwner(User createdUser, User playUser) {
        log.info("[{}][PERMISSION][START] rule={}");
        if (Objects.equals(createdUser.getId(), playUser.getId())) {
            return true;
        }
        return false;
    }

    private boolean isApproverOrAdmin(User playUser) {
        log.info("[{}][PERMISSION][START] rule={}");
        if (playUser.getRole() == UserRole.ADMIN ||
                playUser.getRole() == UserRole.APPROVER) {
            return true;
        }
        return false;
    }

    private boolean isStatusDraft(Manual manual) {
        log.info("[{}][PERMISSION][START] rule={}");
        if (manual.getStatus() == ManualStatus.DRAFT) {
            return true;
        }
        return false;
    }

    private boolean isStatusPending(Manual manual) {
        log.info("[{}][PERMISSION][START] rule={}");
        if (manual.getStatus() == ManualStatus.PENDING) {
            return true;
        }
        return false;
    }

    private boolean isStatusApproved(Manual manual) {
        log.info("[{}][PERMISSION][START] rule={}");
        if (manual.getStatus() == ManualStatus.APPROVED) {
            return true;
        }
        return false;
    }

    private boolean isStatusArchived(Manual manual) {
        log.info("[{}][PERMISSION][START] rule={}");
        if (manual.getStatus() == ManualStatus.ARCHIVED) {
            return true;
        }
        return false;
    }

    private boolean isCategoryActivate(Category category) {
        log.info("[{}][PERMISSION][START] rule={}");
        if (category.isActive()) {
            return true;
        }
        return false;
    }

    private boolean isChangeNote(String changeNote) {
        log.info("[{}][PERMISSION][START] rule={}");
        if (changeNote == null || changeNote.isBlank()) {
            return false;
        }
        return true;
    }

    private boolean isTitleAndContent(String title, String content) {
        log.info("[{}][PERMISSION][START] rule={}");
        if (title == null ||
                content == null ||
                title.isBlank() ||
                content.isBlank()) {
            return false;
        }
        return true;
    }

    public boolean isGuest(User playUser) {
        log.info("[{}][PERMISSION][START] rule={}");
        if (playUser.getRole() == UserRole.GUEST) {
            return true;
        }
        return false;
    }

}
