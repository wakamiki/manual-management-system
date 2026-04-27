package com.example.manual.service;

import java.util.Objects;

import com.example.manual.dto.ManualEditFormDto;
import com.example.manual.entity.Category;
import com.example.manual.entity.Manual;
import com.example.manual.entity.User;
import com.example.manual.enums.ManualStatus;
import com.example.manual.enums.UserRole;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.exception.UnauthorizedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
        if (!isApproverOrAdmin(playUser)) {
            return false;
        }
        if (!isStatusArchived(manual)) {
            return false;
        }
        return true;
    }

    public boolean canCopy(Manual manual) {
        if (!manual.getCategory().isActive()) {
            return false;
        }
        if (!isStatusPending(manual) &&
                !isStatusApproved(manual) &&
                !isStatusArchived(manual)) {
            return false;
        }
        return true;
    }

    public boolean canApprove(Manual manual, User playUser) {
        if (!isApproverOrAdmin(playUser)) {
            return false;
        }
        // 作成者じゃない時のみOK
        if (isOwner(manual.getCreatedByUser(), playUser)) {
            return false;
        }
        if (!isStatusPending(manual)) {
            return false;
        }
        return true;
    }

    public boolean canRollback(Manual manual, User playUser) {
        if (!isApproverOrAdmin(playUser)) {
            return false;
        }
        if (isOwner(manual.getCreatedByUser(), playUser)) {
            return false;
        }
        if (!isStatusPending(manual)) {
            return false;
        }
        return true;
    }

    public boolean PublishDtaftManual(Manual manual, User playUser) {
        if (!isStatusDraft(manual)) {
            return false;
        }
        if (!isOwner(manual.getCreatedByUser(), playUser)) {
            return false;
        }
        return true;
    }

    public boolean canEditManual(Manual manual, User playUser) {
        if (!isOwner(manual.getCreatedByUser(), playUser)) {
            return false;
        }
        if (!isStatusDraft(manual) &&
                !isStatusPending(manual)) {
            return false;
        }
        return true;
    }

    public boolean canArchive(Manual manual, User playUser) {
        if (!isApproverOrAdmin(playUser)) {
            return false;
        }
        if (!isStatusApproved(manual)&&!isStatusPending(manual)&&!isStatusDraft(manual)) {
            return false;
        }
        if (!isTitleAndContent(manual.getTitle(), manual.getContent())) {
            return false;
        }
        return true;
    }

    public boolean canPending(Manual manual, User playUser) {
        if (!isOwner(manual.getCreatedByUser(), playUser)) {
            return false;
        }
        if (!isStatusDraft(manual)) {
            return false;
        }
        if (!isTitleAndContent(manual.getTitle(), manual.getContent())) {
            return false;
        }
        return true;
    }

    // ============================
    // 権限判定
    // ============================

    public boolean canGoToDetailPage(User playUser) {
        log.info("start");
        // アクティブユーザー
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        return true;
    }

    public boolean canGoToCopyPage(User playUser) {
        log.info("start");
        // アクティブユーザー
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        return true;
    }

    public boolean canFindManualsBySearch(User playUser) {
        log.info("start");
        // アクティブユーザー
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        return true;
    }

    public boolean canGoToEditPage(
            User playUser,
            Manual manual) {
        log.info("start");
        Category category = categoryService.getCategoryById(manual.getCategory().getId());
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        if (!isCategoryActivate(category)) {
            throw new InvalidStateException(
                    "使用中カテゴリでのみ復帰が出来ます。");
        }
        if (!isStatusPending(manual) &&
                !isStatusDraft(manual)) {
            throw new InvalidStateException(
                    "編集ができるのはステータス:DRAFT/PENDINGのマニュアルのみです。");
        }
        if (!isOwner(manual.getCreatedByUser(), playUser)) {
            throw new InvalidStateException(
                    "編集ができるのは自分が作成したマニュアルだけです。");
        }
        return true;
    }

    public boolean canGoToNewCreatePage(User playUser) {
        log.info("start");
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
        log.info("start");
        if (!isApproverOrAdmin(playUser)) {
            throw new UnauthorizedException("権限が不足しています。");
        }
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        if (!isStatusDraft(manual) &&
                !isStatusPending(manual) &&
                !isStatusApproved(manual)) {
            throw new InvalidStateException("マニュアルのステータスが条件を満たしていません。");
        }
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
        log.info("start");
        if (!isApproverOrAdmin(playUser)) {
            throw new UnauthorizedException("権限が不足しています。");
        }
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        if (!isStatusPending(manual)) {
            throw new InvalidStateException(
                    "承認ができるのはステータス:PENDINGのマニュアルのみです。");
        }
        if (isOwner(manual.getCreatedByUser(), playUser)) {
            throw new InvalidStateException(
                    "自分が作成したマニュアルの承認をすることは出来ません。");
        }
        if (!isActive(playUser)) {
            throw new InvalidStateException(
                    "有効でないカテゴリーでは承認することが出来ません。");
        }
        return true;
    }

    public boolean canRollbackManual(
            Manual manual,
            User playUser,
            String changeNote) {
        log.info("start");
        if (!isApproverOrAdmin(playUser)) {
            throw new UnauthorizedException("権限が不足しています。");
        }
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        if (!isStatusPending(manual)) {
            throw new InvalidStateException(
                    "差し戻しができるのはステータス:PENDINGのマニュアルのみです。");
        }
        if (isOwner(manual.getCreatedByUser(), playUser)) {
            throw new InvalidStateException(
                    "自分が作成したマニュアルを差し戻しすることは出来ません。");
        }
        if (!isChangeNote(changeNote)) {
            throw new InvalidStateException("更新期歴は必須です。");
        }
        return true;
    }

    public boolean canRestoreManual(
            Manual manual,
            User playUser,
            String changeNote) {
        log.info("start");
        if (!isApproverOrAdmin(playUser)) {
            throw new UnauthorizedException("権限が不足しています。");
        }
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        if (!isStatusArchived(manual)) {
            throw new InvalidStateException(
                    "復帰ができるのはステータス:ARCHIVEDのマニュアルのみです。");
        }
        if (!isActive(playUser)) {
            throw new InvalidStateException(
                    "使用中カテゴリでのみ復帰が出来ます。");
        }
        if (!isChangeNote(changeNote)) {
            throw new InvalidStateException("更新履歴は必須です。");
        }
        return true;
    }

    public boolean canSaveDraftForCreate(
            User playUser,
            Manual manual,
            Category category) {
        log.info("start");

        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
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
        log.info("start");
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        if (!isCategoryActivate(category)) {
            throw new InvalidStateException(
                    "使用停止中のカテゴリーが選択されています。");
        }
        if (!isTitleAndContent(formDto.getTitle(), formDto.getContent())) {
            throw new InvalidStateException("必須項目が入力されていません。");
        }
        return true;
    }

    public boolean canSaveDraftForCopy(
            User playUser,
            Category category,
            String changeNote) {
        log.info("start");
        // アクティブユーザー アクティブカテゴリー チェンジノート必須
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        if (!isCategoryActivate(category)) {
            throw new InvalidStateException(
                    "停止中カテゴリにマニュアルは作成できません。");
        }
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
        log.info("start");
        // アクティブユーザー アクティブカテゴリー チェンジノート必須 タイトル・コンテンツ必須
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        if (!isCategoryActivate(category)) {
            throw new InvalidStateException(
                    "停止中カテゴリにマニュアルは作成できません。");
        }
        if (!isChangeNote(changeNote)) {
            throw new InvalidStateException("更新履歴は必須です。");
        }
        if (!isTitleAndContent(title, content)) {
            throw new InvalidStateException("タイトル・本文は必須です。");
        }
        return true;
    }

    public boolean canEditToPending(
            User playUser,
            Manual manual, ManualEditFormDto formDto) {
        log.info("start");
        // アクティブユーザー ステータスドラフト・ペンディングのみ 作成者のみ編集可 アクティブカテゴリ
        Category category = categoryService.getCategoryById(manual.getCategory().getId());
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        if (!isCategoryActivate(category)) {
            throw new InvalidStateException(
                    "停止中カテゴリにマニュアルは作成できません。");
        }
        if (!isStatusPending(manual) && !isStatusDraft(manual)) {
            throw new InvalidStateException(
                    "編集ができるのはステータス:DRAFT/PENDINGのマニュアルのみです。");
        }
        if (!isOwner(manual.getCreatedByUser(), playUser)) {
            throw new InvalidStateException(
                    "自分が作成したマニュアル以外を編集することはできません。");
        }
        if (!isTitleAndContent(formDto.getTitle(), formDto.getContent())) {
            throw new InvalidStateException(
                    "タイトルと本文は必須項目です。");
        }
        return true;
    }

    public boolean canEditToDraft(
            User playUser,
            Manual manual) {
        log.info("start");
        // アクティブユーザー ステータスドラフト・ペンディングのみ 作成者のみ編集可 アクティブカテゴリ
        Category category = categoryService.getCategoryById(manual.getCategory().getId());
        if (!isActive(playUser)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        if (!isCategoryActivate(category)) {
            throw new InvalidStateException(
                    "停止中カテゴリにマニュアルは作成できません。");
        }
        if (!isStatusPending(manual) && !isStatusDraft(manual)) {
            throw new InvalidStateException(
                    "編集ができるのはステータス:DRAFT/PENDINGのマニュアルのみです。");
        }
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
        if (playUser.isActive()) {
            return true;
        }
        return false;
    }

    private boolean isUserActive(User playUser) {
        if (playUser.isActive() == true) {
            return true;
        }
        return false;
    }

    private boolean isOwner(User createdUser, User playUser) {
        if (Objects.equals(createdUser.getId(), playUser.getId())) {
            return true;
        }
        return false;
    }

    private boolean isApproverOrAdmin(User playUser) {
        if (playUser.getRole() == UserRole.ADMIN ||
                playUser.getRole() == UserRole.APPROVER) {
            return true;
        }
        return false;
    }

    private boolean isStatusDraft(Manual manual) {
        if (manual.getStatus() == ManualStatus.DRAFT) {
            return true;
        }
        return false;
    }

    private boolean isStatusPending(Manual manual) {
        if (manual.getStatus() == ManualStatus.PENDING) {
            return true;
        }
        return false;
    }

    private boolean isStatusApproved(Manual manual) {
        if (manual.getStatus() == ManualStatus.APPROVED) {
            return true;
        }
        return false;
    }

    private boolean isStatusArchived(Manual manual) {
        if (manual.getStatus() == ManualStatus.ARCHIVED) {
            return true;
        }
        return false;
    }

    private boolean isCategoryActivate(Category category) {
        if (category.isActive()) {
            return true;
        }
        return false;
    }

    private boolean isChangeNote(String changeNote) {
        if (changeNote == null || changeNote.isBlank()) {
            return false;
        }
        return true;
    }

    private boolean isTitleAndContent(String title, String content) {
        if (title == null ||
                content == null ||
                title.isBlank() ||
                content.isBlank()) {
            return false;
        }
        return true;
    }

    public boolean isGuest(User playUser) {
        if (playUser.getRole() == UserRole.GUEST) {
            return true;
        }
        return false;
    }

}
