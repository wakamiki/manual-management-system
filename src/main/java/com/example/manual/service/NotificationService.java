package com.example.manual.service;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.manual.entity.Manual;
import com.example.manual.entity.Notification;
import com.example.manual.entity.User;
import com.example.manual.enums.ManualStatus;
import com.example.manual.enums.NotificationType;
import com.example.manual.enums.UserRole;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.exception.UnauthorizedException;
import com.example.manual.repository.NotificationRepository;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final UserService userService;
    private final NotificationRepository notificationRepository;

    public NotificationService(
            UserService userService,
            NotificationRepository notificationRepository) {
        this.userService = userService;
        this.notificationRepository = notificationRepository;
    }

    // ========================================================
    // 通知作成
    // ========================================================

    // 承認待ち申請時に承認者全員へ通知を作成する。
    // 有効ユーザーのadmin/approverに通知を送る（作成者を除く）

    public void createSubmitNotifications(Principal principal, Manual manual) {

        User user = userService.getUserByPrincipal(principal);
        if (!canCreateSubmitNotifications(principal)) {
            throw new UnauthorizedException("判定エラー");
        }
        // 通知先ユーザーリスト
        List<User> users = userService.findApproverAndAdminUsersExcept(user.getId());

        for (User targetUser : users) {
            Notification notification = new Notification();
            notification.setTargetUser(targetUser);
            notification.setManual(manual);
            notification.setType(NotificationType.PENDING_APPROVAL);
            notification.markCreatedNow();
            notificationRepository.save(notification);
        }
    }

    public void createRollbackNotification(Manual manual) {

        // 差し戻し時に作成者へ通知を作成する。
        User targetUser = userService.getUserByLoginId(
                manual.getCreatedByUser().getLoginId());

        Notification notification = new Notification();
        notification.setTargetUser(targetUser);
        notification.setManual(manual);
        notification.setType(NotificationType.ROLLBACK);
        notification.markCreatedNow();
        notificationRepository.save(notification);
    }

    // ========================================================
    // 通知削除
    // ========================================================

    // マニュアル承認時またはPending→Draftステータス変更時にそのマニュアルに紐づく通知を送ったユーザーの通知を削除する。
    @Transactional
    public void deletePendingApprovalNotificationsByManualId(
            Long manualId) {

        notificationRepository.deleteByManualIdAndType(
                manualId, NotificationType.PENDING_APPROVAL);
    }

    // 差し戻しマニュアルがPENDINGに変わった時に通知を削除する。
    @Transactional
    public void deleteRollbackNotification(Long manualId, User user) {

        // 通知のあるユーザーがPENDINGしたときに実行
        notificationRepository.deleteByManualIdAndType(
                manualId, NotificationType.ROLLBACK);
    }

    // 該当マニュアルに紐づく通知を全削除
    @Transactional
    public void deleteByManualIdNotification(Long manualId) {

        notificationRepository.deleteByManualId(manualId);
    }

    public void deleteAsRead(Long notificationId, User user) {

        // 指定通知を削除する。既読ボタンに対応。
        // 有効ユーザー 画面更新要
    }

    public void deleteAllAsRead(User user) {

        // まとめて削除にする。全既読ボタン用。
        // 有効ユーザー
    }

    public void clearPendingNotifications(Long manualId) {

    }

    // ========================================================
    // 通知数取得
    // ========================================================
    public void getUnreadCount(User user) {

        // 未読件数を返す。バッヂ表示に使う。
        // 有効ユーザー 件数を取得。取得方法検討
    }

    // ユーザー未読の差し戻し通知数を取得
    public int unreadRollbackCount(Principal principal) {

        User playUser = userService.getUserByPrincipal(principal);
        Long count = notificationRepository.countByTargetUserAndType(playUser, NotificationType.ROLLBACK);
        int notificationCount = Math.toIntExact(count);
        return notificationCount;
    }

    // ユーザー未読の承認待ち通知数（自分作成マニュアルを除く）を取得
    public int pendingUnCreatedCount(Principal principal) {

        User playUser = userService.getUserByPrincipal(principal);
        Long count = notificationRepository.countByTypeAndManual_StatusAndManual_CreatedByUserNot(
                NotificationType.PENDING_APPROVAL,
                ManualStatus.PENDING,
                playUser);
        int notificationCount = Math.toIntExact(count);
        return notificationCount;
    }

    // ========================================================
    // 権限判定
    // ========================================================
    private boolean isActiveUser(Principal principal) {

        User user = userService.getUserByPrincipal(principal);
        if (!user.isActive()) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        return true;
    }

    private boolean canCreateSubmitNotifications(Principal principal) {

        User user = userService.getUserByPrincipal(principal);
        if (user.getRole() == UserRole.GUEST) {
            throw new InvalidStateException("権限が不足しています。");
        }
        if (!isActiveUser(principal)) {
            throw new UnauthorizedException("有効なユーザーではありません。");
        }
        return true;
    }
}
