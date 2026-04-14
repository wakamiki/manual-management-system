package com.example.manual.service;

import java.security.Principal;
import java.util.List;

import com.example.manual.entity.Manual;
import com.example.manual.entity.Notification;
import com.example.manual.entity.User;
import com.example.manual.enums.NotificationType;
import com.example.manual.enums.UserRole;
import com.example.manual.exception.InvalidStateException;
import com.example.manual.exception.UnauthorizedException;
import com.example.manual.repository.NotificationRepository;

public class NotificationService {
    
    private final UserService userService;
    private final NotificationRepository notificationRepository;

    public NotificationService(
        UserService userService,
        NotificationRepository notificationRepository){
        this.userService=userService;
        this.notificationRepository = notificationRepository;
    }

public void createSubmitNotifications(
        Manual manual,
        Principal principal){
//承認待ち申請時に承認者全員へ通知を作成する。
//有効ユーザーのadmin/approverに通知を送る（作成者を除く）
    if (!canCreateSubmitNotifications(principal)) {
        throw new UnauthorizedException("判定エラー");
    }
    //通知先ユーザーリスト
    List<User>users = userService.findApproverAndAdminUsersExcept(manual.getId());
    
    for (User user : users) {
    Notification notification = new Notification();
    notification.setTargetUser(user);
    notification.setManual(manual);
    notification.setType(NotificationType.PENDING_APPROVAL);
    notification.setUnread();
    notification.markCreatedNow();
    notificationRepository.save(notification);   
    }
    }

public void createRollbackNotification(Manual manual){
//差し戻し時に作成者へ通知を作成する。
//有効ユーザー　manualCreateUserに通知

}

public void getUnreadCount(User user){
//未読件数を返す。バッヂ表示に使う。
//有効ユーザー　件数を取得。取得方法検討
}

public void markAsRead(Long notificationId, User user){
//指定通知を既読にする。既読ボタンに対応。
//有効ユーザー　画面更新要
}

public void markAllAsRead(User user){
//まとめて既読にする。全既読ボタン用。
//有効ユーザー
}

public void clearPendingNotifications(Long manualId){

}

// public void createApproveNotification(Manual manual){}
//承認完了時に作成者へ通知を作成する。 余裕があれば実装

//権限判定
private boolean isActiveUser(Principal principal){
    User user = userService.getUserByPrincipal(principal);
    if(!user.isActive()){
        throw new UnauthorizedException("有効なユーザーではありません。");
    }
    return true;
}
private boolean canCreateSubmitNotifications(Principal principal){
    User user = userService.getUserByPrincipal(principal);
    if (user.getRole()!=UserRole.APPROVER &&
        user.getRole()!=UserRole.ADMIN) {
        throw new InvalidStateException("権限が不足しています。");
    }
    if (!isActiveUser(principal)) {
        throw new UnauthorizedException("有効なユーザーではありません。");
    }
        return true;
}
}

