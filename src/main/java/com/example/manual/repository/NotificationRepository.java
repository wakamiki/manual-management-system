package com.example.manual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.manual.entity.Notification;
import com.example.manual.entity.User;
import com.example.manual.enums.NotificationType;



public interface NotificationRepository extends JpaRepository<Notification, Long> {

  //マニュアルIDで検索したTYPEが同じで未読のユーザー通知
  List<Notification> findByManualIdAndNotificationType(
      Long manualId, NotificationType notificationType);

  // マニュアルIDで検索したTYPEが同じで未読のユーザー通知を削除
  void deleteByManualIdAndNotificationType(Long manualId, NotificationType PENDING_APPROVAL);

  //数取得　ユーザーが未読で取得TYPE任意の通知の数を取得
  Long countByTargetUserAndNotificationType(
      User user, NotificationType notificationType);

}
