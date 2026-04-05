# 業務マニュアル管理システム 仕様書

Version: 01.05.00
更新日: 2026-04-05
更新内容: Manual複製仕様の詳細化
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

### 1-1. 対象機能

* マニュアル CRUD
* 状態遷移管理
* 承認フロー
* カテゴリ管理
* ユーザー管理
* ログイン認証
* 権限制御
* 履歴管理
* 検索機能

### 1-2. 利用者

* USER
* APPROVER
* ADMIN

### 1-3. 前提条件

* 全機能ログイン必須
* 権限に応じて操作制御
* 物理削除は行わない
* ARCHIVED により論理管理

### 1-4. 権限制御概要

* USER：作成 / 編集 / 申請
* APPROVER：承認 / 旧版化 / 復帰
* ADMIN：カテゴリ / ユーザー管理

---

## 2. 状態遷移仕様

### 2-1. マニュアル状態一覧

* DRAFT
* PENDING
* APPROVED
* ARCHIVED

### 2-2. 状態遷移ルール

* DRAFT → PENDING
* PENDING → APPROVED
* PENDING → DRAFT
* PENDING → ARCHIVED
* APPROVED → ARCHIVED
* ARCHIVED → APPROVED

### 2-3. 状態遷移条件

* title / content 必須
* 作成者本人承認禁止
* APPROVER 以上のみ承認可能
* 使用停止カテゴリでは承認不可
* 復帰は同一アクティブカテゴリのみ

### 2-4. 承認日時制御

* DRAFT：null
* PENDING：null
* APPROVED：初回承認日時保存
* ARCHIVED：保持

---

## 3. 業務ルール仕様

### 3-1. 編集ルール

* DRAFT：編集可
* PENDING：作成者のみ編集可
* APPROVED：直接編集不可
* ARCHIVED：編集不可

### 3-2. 複製ルール

* 複製元から title / content を引き継ぐ
* id は新規採番
* changeNote 必須
* 複製先は DRAFT
* approvedAt = null
* createdAt / updatedAt 再採番
* createdByUser は複製実行ユーザー
* category 指定必須
* 複製時は履歴を必ず1件登録

### 3-3. 複製画面遷移ルール

* 複製開始元は詳細画面 / 編集画面
* 複製開始時は新規作成画面を複製モードで表示
* title / content は初期表示
* category は複製先カテゴリを再選択可能
* changeNote は未入力で初期表示し、保存前に必須チェック
* 保存完了後は複製先マニュアル詳細画面へ遷移

### 3-4. 検索ルール

* title 部分一致
* 大文字小文字無視
* includeArchived 対応

### 3-5. disabledボタン理由表示

* 非活性理由を画面表示
* 権限不足
* 状態不一致
* 使用停止カテゴリ

---

## 4. Entity設計

### 4-1. Category

* id
* categoryName
* isActive
* createdAt
* updatedAt

### 4-2. User

* id
* userId
* password
* displayName
* role
* isActive
* lastLoginAt
* createdAt
* updatedAt

### 4-3. Manual

* id
* categoryId
* createdByUserId
* title
* content
* status
* createdAt
* updatedAt
* approvedAt

### 4-4. ManualHistory

* id
* manualId
* changeNote
* changedAt

### 4-5. UserOperationHistory

* id
* targetUserId
* operatedByUserId
* operationType
* operationDetail
* createdAt

---

## 5. Service設計

### 5-1. ManualService

主要メソッド

* createManual()
* updateManual()
* submitManual()
* approveManual()
* archiveManual()
* copyManual()
* searchManuals()

責務

* CRUD
* 状態遷移制御
* 権限制御
* changeNote 履歴保存

### 5-2. UserService

* createUser()
* updateUser()
* changeRole()
* deactivateUser()
* activateUser()
* resetPassword()

### 5-3. CategoryService

* createCategory()
* updateCategory()
* deactivateCategory()
* activateCategory()
* findAllActive()

### 5-4. AuthService

* login()
* logout()
* hasPermission()

---

## 6. Controller設計

### 6-1. ManualController

| メソッド | エンドポイント               | 機能   |
| ---- | --------------------- | ---- |
| POST | /manuals              | 新規作成 |
| GET  | /manuals              | 一覧取得 |
| GET  | /manuals/{id}         | 詳細取得 |
| PUT  | /manuals/{id}         | 更新   |
| PUT  | /manuals/{id}/submit  | 申請   |
| PUT  | /manuals/{id}/approve | 承認   |
| PUT  | /manuals/{id}/archive | 旧版化  |
| POST | /manuals/{id}/copy    | 複製   |

### 6-2. UserController

| メソッド | エンドポイント                | 機能     |
| ---- | ---------------------- | ------ |
| GET  | /users                 | 一覧取得   |
| POST | /users                 | ユーザー作成 |
| PUT  | /users/{id}            | 更新     |
| PUT  | /users/{id}/deactivate | 停止     |
| PUT  | /users/{id}/activate   | 再有効化   |

### 6-3. CategoryController

| メソッド | エンドポイント                     | 機能   |
| ---- | --------------------------- | ---- |
| GET  | /categories                 | 一覧取得 |
| POST | /categories                 | 新規作成 |
| PUT  | /categories/{id}            | 更新   |
| PUT  | /categories/{id}/deactivate | 停止   |

### 6-4. AuthController

| メソッド | エンドポイント      | 機能    |
| ---- | ------------ | ----- |
| POST | /auth/login  | ログイン  |
| POST | /auth/logout | ログアウト |

---

## 7. DTO設計

### RequestDto

* ManualRequestDto
* UserRequestDto
* CategoryRequestDto

### ResponseDto

* ManualResponseDto
* UserResponseDto
* CategoryResponseDto

### 一覧用Dto

* ManualListDto
* UserListDto

### 詳細用Dto

* ManualDetailDto
* UserDetailDto

---

## 8. バリデーション仕様

### title

* 必須
* 最大 100 文字

### content

* 必須
* 最大 10000 文字

### changeNote

* 必須
* 最大 100 文字
* trim

### userId

* 必須
* 重複不可

### categoryName

* 必須
* 最大 50 文字
* 重複不可

---

## 9. 例外設計

例外クラス

* ResourceNotFoundException
* ValidationException
* UnauthorizedException
* InvalidStatusTransitionException

共通処理

* @ControllerAdvice
* 統一エラーレスポンス

---

## 10. ログ設計

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

## 11. 実装順

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

## 12. 将来拡張設計

* 関連マニュアル
* 確認チェック
* コメント
* タグ
* お気に入り

---

## 改版履歴

| Version  | 日付         | 更新内容        |
| -------- | ---------- | ----------- |
| 01.01.00 | 2026-03-29 | 初版作成、基本構成定義 |
| 01.02.00 | 2026-03-30 | status仕様・状態遷移・承認履歴仕様追加 |
| 01.03.00 | 2026-03-31 | CRUD仕様確定、検索API追加、approvedAt制御ルール確定 |
| 01.04.00 | 2026-04-04 | 詳細設計資料統合版作成 |
| 01.05.00 | 2026-04-05 | Manual複製仕様の詳細化 |
