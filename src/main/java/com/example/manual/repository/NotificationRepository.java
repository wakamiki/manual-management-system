package com.example.manual.repository;

import java.util.List;

import com.example.manual.entity.Notification;
import com.example.manual.entity.User;
import com.example.manual.enums.ManualStatus;
import com.example.manual.enums.NotificationType;

import org.springframework.data.jpa.repository.JpaRepository;



public interface NotificationRepository extends JpaRepository<Notification, Long> {

  //マニュアルIDで検索したTYPEが同じで未読のユーザー通知
  List<Notification> findByManualIdAndType(
      Long manualId, NotificationType type);

  // マニュアルIDで検索したTYPEが同じで未読のユーザー通知を削除
  void deleteByManualIdAndType(Long manualId, NotificationType type);

  //マニュアルIDで検索した通知を全削除
void deleteByManualId(Long manualId);

  //数取得　ユーザーが未読で取得TYPE任意の通知の数を取得
  Long countByTargetUserAndType(
      User user, NotificationType type);

      //数取得　任意のマニュアルステータスで作成者が該当ユーザーでないもの
long countByTypeAndManual_StatusAndManual_CreatedByUserNot(
    NotificationType type,
    ManualStatus status,
    User user
);
}
