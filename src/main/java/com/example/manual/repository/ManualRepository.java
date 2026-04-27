package com.example.manual.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.example.manual.entity.Manual;
import com.example.manual.entity.User;
import com.example.manual.enums.ManualStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ManualRepository extends JpaRepository<Manual, Long>, JpaSpecificationExecutor<Manual> {

    // 一覧取得（更新昇順）
    Page<Manual> findAllByOrderByUpdatedAtDesc(Pageable pageable);

    // 検索ワードが含まれる一覧取得（更新昇順）
    Page<Manual> findByTitleContainingOrderByUpdatedAtDesc(String keyword, Pageable pageable);

    // status絞り込み検索機能（更新昇順）
    Page<Manual> findByStatusOrderByUpdatedAtDesc(
            ManualStatus status, Pageable pageable);

    // 作成者が自分の差し戻しマニュアル取得（更新昇順）
    Page<Manual> findByIsRolledbackTrueAndCreatedByUserOrderByUpdatedAtDesc(
                    User createdByUser, Pageable pageable);

    List<Manual> findByIsRolledbackTrueAndCreatedByUserOrderByUpdatedAtDesc(
                    User createdByUser);

    // 作成者が自分のマニュアル（作成日新しい順）
    Page<Manual> findByCreatedByUserOrderByCreatedAtDesc(
                    User createdByUser, Pageable pageable);

    List<Manual> findByCreatedByUserOrderByCreatedAtDesc(
                    User createdByUser);

    // 作成者が自分ではない任意ステータスマニュアル（更新昇順）
    Page<Manual> findByCreatedByUserNotAndStatusOrderByUpdatedAtDesc(
            User createdByUser,
            ManualStatus status,
                    Pageable pageable);

    List<Manual> findByCreatedByUserNotAndStatusOrderByUpdatedAtDesc(
            User createdByUser,
                    ManualStatus status);

    // 作成者が自分の任意ステータスマニュアル(更新昇順)
    Page<Manual> findByCreatedByUserAndStatusOrderByUpdatedAtDesc(
            User createdByUser,
            ManualStatus status,
            Pageable pageable);

    // 引数の日付より更新日時が後のマニュアルを取得（更新昇順）
    Page<Manual> findByUpdatedAtAfterOrderByUpdatedAtDesc(
            LocalDateTime updatedAt, Pageable pageable);
    // 引数LocalDateTime.now().minusDays(7)で一週間以内取得

    // 指定ステータス以外(Draft想定) 引数の日付より更新日時が後のマニュアルを取得（更新昇順）
    Page<Manual> findByUpdatedAtAfterAndStatusNotOrderByUpdatedAtDesc(
                    LocalDateTime updatedAt,
                    ManualStatus draft,
                    Pageable pageable);
    // 引数LocalDateTime.now().minusDays(7)で一週間以内取得

    // 数取得 作成者が自分の差し戻しマニュアル取得
    Long countByIsRolledbackTrueAndCreatedByUser(
            User createdByUser);

    // 数取得 作成者が自分ではない任意ステータスマニュアル
    Long countByCreatedByUserNotAndStatus(
            User createdByUser,
            ManualStatus status);

    // 数取得 作成者が自分の任意ステータスマニュアル
    Long countByCreatedByUserAndStatus(
            User createdByUser,
            ManualStatus status);

    // 数取得 引数の日付より更新日時が後のマニュアル
    Long countByUpdatedAtAfter(LocalDateTime updatedAt);
    // 引数LocalDateTime.now().minusDays(7)で一週間以内取得

    // 数取得 指定ステータス以外(Draft想定) 引数の日付より更新日時が後のマニュアルを取得（更新昇順）
    Long countByUpdatedAtAfterAndStatusNot(
                    LocalDateTime updatedAt,
                    ManualStatus draft);
    // 引数LocalDateTime.now().minusDays(7)で一週間以内取得

    // 数取得 自分作成分のマニュアル全件
    Long countByCreatedByUser(User createdByUser);
}
