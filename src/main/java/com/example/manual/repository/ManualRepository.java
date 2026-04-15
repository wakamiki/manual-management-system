package com.example.manual.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.manual.entity.Manual;
import com.example.manual.entity.User;
import com.example.manual.enums.ManualStatus;

public interface ManualRepository extends JpaRepository<Manual, Long>
, JpaSpecificationExecutor<Manual> {

  //一覧取得（更新昇順）
  List<Manual> findAllByOrderByUpdatedAtDesc();

  // 検索ワードが含まれる一覧取得（更新昇順）
  List<Manual> findByTitleContainingOrderByUpdatedAtDesc(String keyword);

 //status絞り込み検索機能（更新昇順）
 List<Manual> findByStatusOrderByUpdatedAtDesc(
    ManualStatus status);

  //作成者が自分の差し戻しマニュアル取得（更新昇順）
  List<Manual> findByIsRolledBackTrueAndCreatedByUserOrderByUpdatedAtDesc(
      User createdByUser);

  //作成者が自分のマニュアル（作成日新しい順）
  List<Manual> findByCreatedByUserOrderByCreatedAtDesc(
        User createdByUser);

  //作成者が自分ではない任意ステータスマニュアル（更新昇順）
  List<Manual> findByCreatedByUserNotAndStatusOrderByUpdatedAtDesc(
      User createdByUser,
      ManualStatus status);

  // 作成者が自分の任意ステータスマニュアル(更新昇順)
  List<Manual> findByCreatedByUserAndStatusOrderByUpdatedAtDesc(
          User createdByUser,
          ManualStatus status);

  //引数の日付より更新日時が後のマニュアルを取得（更新昇順）
  List<Manual> findByUpdatedAtAfterOrderByUpdatedAtDesc(
          LocalDateTime updatedAt);
          // 引数LocalDateTime.now().minusDays(7)で一週間以内取得

  //数取得  作成者が自分の差し戻しマニュアル取得
  Long countByIsRolledBackTrueAndCreatedByUser(
      User createdByUser);

  //数取得  作成者が自分ではない任意ステータスマニュアル
  Long countByCreatedByUserNotAndStatus(
      User createdByUser,
      ManualStatus status);

  //数取得 　作成者が自分の任意ステータスマニュアル
  Long countByCreatedByUserAndStatus(
          User createdByUser,
          ManualStatus status);

  //数取得 　引数の日付より更新日時が後のマニュアル
  Long countByUpdatedAtAfter(LocalDateTime updatedAt);
  // 引数LocalDateTime.now().minusDays(7)で一週間以内取得

  //数取得　自分作成分のマニュアル全件
  Long countByCreatedByUser(User createdByUser);
}
