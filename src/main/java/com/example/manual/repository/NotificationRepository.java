package com.example.manual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.manual.entity.Notification;
import com.example.manual.entity.User;
import com.example.manual.enums.NotificationType;



public interface NotificationRepository extends JpaRepository<Notification, Long> {

  //マニュアルIDで検索したTYPEが同じで未読のユーザー通知
  List<Notification> findByManualIdAndType(
    Long manualId, NotificationType type);

    // マニュアルIDで検索したTYPEが同じで未読のユーザー通知を削除
  void deleteByManualIdAndType(Long manualId, NotificationType type);

  //数取得　ユーザーが未読で取得TYPE任意の通知の数を取得
  Long countByTargetUserAndType(
      User user, NotificationType type);
}
