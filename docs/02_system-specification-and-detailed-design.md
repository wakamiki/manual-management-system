# 業務マニュアル管理システム 仕様書

Version: 01.07.00
更新日: 2026-04-07
更新内容: DTO / Service / API 設計方針を現行案へ更新

---

## 1. システム仕様概要

本システムは、日々変化する現場業務に対応するための **業務ナレッジ共有・管理システム** です。

正式な冊子マニュアルとは別に、現場で発生する運用変更や補足事項を迅速に共有できることを目的としています。

以下の課題解決を目的として設計しています。

* 業務知識の属人化防止
* 最新情報の迅速な共有
* 承認状態の可視化
* 旧版履歴の保持
* 「言った / 言わない」問題の防止

---

## 2. 対象機能

* マニュアル CRUD
* 状態遷移管理
* 承認フロー
* カテゴリ管理
* ユーザー管理
* ログイン認証
* 権限制御
* 履歴管理
* 検索機能

---

## 3. 利用者・前提条件

### 3-1. 利用者

* USER
* APPROVER
* ADMIN

### 3-2. 前提条件

* 全機能ログイン必須
* 権限に応じて操作制御
* 物理削除は行わない
* ARCHIVED により論理管理する

### 3-3. 権限制御概要

* USER：作成 / 編集 / 申請
* APPROVER：承認 / 旧版化 / 復帰
* ADMIN：カテゴリ / ユーザー管理

### 3-4. 画面利用方針

* トップ画面は検索 / 絞り込み / 画面遷移の起点とする
* マニュアル詳細画面は、検索しながら複数マニュアルを突き合わせて参照できるよう別タブで開く
* 新規作成 / 複製 / 編集は入力専用の独立タブとして扱う
* 管理画面もトップ画面を閉じずに参照・更新できるよう別タブで開く

### 3-5. 終了導線方針

* 別タブ画面の終了導線は `戻る` ではなく `閉じる` を基本とする
* `閉じる` はモック段階では「現在の別タブ画面を終了する」操作意図を表す
* 詳細画面の `閉じる` は、一覧へ戻る意味ではなく比較参照用タブを終了する意味で扱う
* 新規作成 / 複製 / 編集 / 管理画面の `閉じる` も、入力・管理用タブを終了する導線として扱う

---

## 4. 状態遷移仕様

### 4-1. マニュアル状態一覧

* DRAFT
* PENDING
* APPROVED
* ARCHIVED

### 4-2. 状態遷移ルール

* DRAFT → PENDING
* PENDING → APPROVED
* PENDING → DRAFT
* PENDING → ARCHIVED
* APPROVED → ARCHIVED
* ARCHIVED → APPROVED

### 4-3. 状態遷移条件

* title / content 必須
* 作成者本人承認禁止
* APPROVER 以上のみ承認可能
* 使用停止カテゴリでは承認不可
* 復帰は同一アクティブカテゴリのみ

### 4-4. 承認日時制御

* DRAFT：null
* PENDING：null
* APPROVED：初回承認日時保存
* ARCHIVED：保持

---

## 5. 業務ルール仕様

### 5-1. 編集ルール

* DRAFT：編集可
* PENDING：作成者のみ編集可
* APPROVED：直接編集不可
* ARCHIVED：編集不可

### 5-2. 複製ルール

* 複製元から title / content を引き継ぐ
* id は新規採番
* changeNote 必須
* 複製先は DRAFT
* approvedAt = null
* createdAt / updatedAt は新規値を設定
* createdByUser は複製実行ユーザー
* category 指定必須
* 複製時は履歴を必ず 1 件登録

### 5-3. 複製画面遷移ルール

* 複製開始元は詳細画面 / 編集画面
* 複製開始時は新規作成画面を複製モードで別タブ表示する
* title / content は初期表示する
* category は複製先カテゴリを再選択可能とする
* changeNote は未入力で初期表示し、保存前に必須チェックする
* 保存完了後は複製先マニュアル詳細画面へ遷移する

### 5-4. 検索ルール

* title 部分一致
* 大文字小文字無視
* includeArchived 対応

### 5-5. disabled ボタン理由表示

* 非活性理由を画面表示する
* 権限不足
* 状態不一致
* 使用停止カテゴリ

### 5-6. 別タブ画面の保存後方針

* 詳細画面は参照タブとして扱うため、保存処理は持たない
* 新規作成 / 複製 / 編集は独立タブで操作する
* モック段階では `閉じる` により作業タブ終了を表現する
* 実装時の保存完了後遷移は各機能ルールに従う
* 例：
  * 複製保存完了後は複製先マニュアル詳細画面へ遷移
  * 新規作成保存完了後の遷移先は Controller / UI 実装時に確定
  * 編集保存完了後の遷移先は詳細再表示または完了メッセージ表示を前提に実装時に確定

---

## 6. Entity設計

### 6-1. Category

* id
* categoryName
* isActive
* createdAt
* updatedAt

### 6-2. User

* id
* userId
* password
* displayName
* role
* isActive
* lastLoginAt
* createdAt
* updatedAt

### 6-3. Manual

* id
* categoryId
* createdByUserId
* title
* content
* status
* createdAt
* updatedAt
* approvedAt

### 6-4. ManualHistory

* id
* manualId
* changeNote
* changedAt

### 6-5. UserOperationHistory

* id
* targetUserId
* operatedByUserId
* operationType
* operationDetail
* createdAt

---

## 7. Service設計

### 7-1. ManualService

主要メソッド

* createDraftManual()
* createAndSubmitManual()
* updateManual()
* getManualDetail()
* getManualHistories()
* submitManual()
* approveManual()
* rollbackManual()
* archiveManual()
* restoreManual()
* copyDraftManual()
* copyPendingManual()
* getManualList()

責務

* CRUD
* 状態遷移制御
* 権限制御
* changeNote 履歴保存

### 7-2. UserService

* createUser()
* updateUser()
* changeRole()
* deactivateUser()
* activateUser()
* resetPassword()

### 7-3. CategoryService

* createCategory()
* updateCategory()
* deactivateCategory()
* activateCategory()
* findAllActive()

### 7-4. AuthService

* login()
* logout()
* hasPermission()

---

## 8. Controller設計

### 8-1. ManualController

| メソッド | エンドポイント               | 機能   |
| ---- | --------------------- | ---- |
| GET  | /api/manuals | 一覧取得 |
| GET  | /api/manuals/{manualId} | 詳細取得 |
| GET  | /api/manuals/{manualId}/histories | 履歴取得 |
| POST | /api/manuals | 下書き保存 |
| POST | /api/manuals/submit | 申請付き新規作成 |
| PUT  | /api/manuals/{manualId} | 更新 |
| POST | /api/manuals/{manualId}/actions/copy | 複製 |
| POST | /api/manuals/{manualId}/actions/submit | 申請 |
| POST | /api/manuals/{manualId}/actions/approve | 承認 |
| POST | /api/manuals/{manualId}/actions/rollback | 差し戻し |
| POST | /api/manuals/{manualId}/actions/archive | 旧版化 |
| POST | /api/manuals/{manualId}/actions/restore | 復帰 |

### 8-2. UserController

| メソッド | エンドポイント                | 機能     |
| ---- | ---------------------- | ------ |
| GET  | /api/users | 一覧取得 |
| GET  | /api/users/{id} | 詳細取得 |
| POST | /api/users | ユーザー作成 |
| PUT  | /api/users/{id} | 更新 |
| PUT  | /api/users/{id}/deactivate | 停止 |
| PUT  | /api/users/{id}/activate | 再有効化 |
| PUT  | /api/users/{id}/reset-password | パスワード再設定 |

### 8-3. CategoryController

| メソッド | エンドポイント                     | 機能   |
| ---- | --------------------------- | ---- |
| GET  | /api/categories | 一覧取得 |
| POST | /api/categories | 新規作成 |
| PUT  | /api/categories/{id} | 更新 |
| PUT  | /api/categories/{id}/deactivate | 停止 |
| PUT  | /api/categories/{id}/activate | 再有効化 |

### 8-4. AuthController

| メソッド | エンドポイント      | 機能    |
| ---- | ------------ | ----- |
| POST | /api/auth/login | ログイン |
| POST | /api/auth/logout | ログアウト |

---

## 9. DTO設計

### 9-1. RequestDto

* ManualDraftRequestDto
* ManualRequestDto
* ManualCopyRequestDto
* ManualActionRequestDto
* UserRequestDto
* CategoryRequestDto
* LoginRequestDto

### 9-2. ResponseDto

* ManualResponseDto
* UserResponseDto
* CategoryResponseDto
* ManualHistoryDto

### 9-3. 一覧用Dto

* ManualListDto

### 9-4. 詳細用Dto

* ManualDetailDto

---

## 10. バリデーション仕様

### 10-1. title

* 必須
* 最大 100 文字

### 10-2. content

* 必須
* 最大 10000 文字

### 10-3. changeNote

* 必須
* 最大 100 文字
* trim

### 10-4. userId

* 必須
* 重複不可

### 10-5. categoryName

* 必須
* 最大 50 文字
* 重複不可

---

## 11. 例外設計

例外クラス

* ResourceNotFoundException
* ValidationException
* UnauthorizedException
* InvalidStatusTransitionException

共通処理

* @ControllerAdvice
* 統一エラーレスポンス

---

## 12. ログ設計

ログレベル

* INFO
* WARN
* ERROR

使用技術

* SLF4J
* Logback

ログ対象

* ログイン
* 状態遷移
* ユーザー停止
* 承認操作
* 例外

---

## 13. 実装順

1. Entity
2. Repository
3. Service
4. Controller
5. DTO
6. Validation
7. Exception
8. Logging
9. Security

---

## 14. 将来拡張設計

* 関連マニュアル
* 確認チェック
* コメント
* タグ
* お気に入り

---

## 改版履歴

| Version  | 日付         | 更新内容 |
| -------- | ---------- | -------- |
| 01.01.00 | 2026-03-29 | 初版作成、基本構成定義 |
| 01.02.00 | 2026-03-30 | status仕様・状態遷移・承認履歴仕様追加 |
| 01.03.00 | 2026-03-31 | CRUD仕様確定、検索API追加、approvedAt制御ルール確定 |
| 01.04.00 | 2026-04-04 | 詳細設計資料統合版作成 |
| 01.05.00 | 2026-04-05 | Manual複製仕様の詳細化 |
| 01.06.00 | 2026-04-05 | 別タブ運用方針、`閉じる` 終了導線、保存後方針の整理を追加 |
