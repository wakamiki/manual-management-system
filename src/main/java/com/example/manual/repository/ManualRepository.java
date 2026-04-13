package com.example.manual.repository;

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
 List<Manual> findByStatusOrderByUpdatedAtDesc(ManualStatus status);

  //作成者が自分の差し戻しマニュアル取得（更新昇順）
  List<Manual> findByIsRolledBackTrueAndCreatedByUserOrderByUpdatedAtDesc(
      User createdByUser);

  //作成者が自分のマニュアル（作成日降順）
  List<Manual> findByCreatedByUserOrderByCreatedAtDesc(User createdByUser);

  //PENDINGで作成者が自分ではないもの（更新昇順）
  List<Manual> findByCreatedByUserNotAndStatusOrderByUpdatedAtDesc(
      User createdByUser,
      ManualStatus status);


}
