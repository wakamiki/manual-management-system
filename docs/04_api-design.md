# 04_api-design.md

Version: 01.03.11  
更新日: 2026-04-17

---

## 1. API 一覧

### 1-1. Manual API
- GET `/manuals/index`
- GET `/manuals/{manualId}/edit`
- GET `/manuals/{manualId}/create`
- GET `/manuals/{manualId}/actions/copy`
- GET `/manuals/{manualId}/actions/edit`
- POST `/manuals/{manualId}/actions/save-draft`
- POST `/manuals/{manualId}/actions/save-draft-copy`
- POST `/manuals/{manualId}/actions/submit-pending`
- POST `/manuals/{manualId}/actions/edit-to-pending`
- POST `/manuals/{manualId}/actions/submit`
- POST `/manuals/{manualId}/actions/approve`
- POST `/manuals/{manualId}/actions/approve-with-comment`
- POST `/manuals/{manualId}/actions/rollback`
- POST `/manuals/{manualId}/actions/archive`
- POST `/manuals/{manualId}/actions/restore`

### 1-2. Category API
- GET `/categories/category-management`
- POST `/categories`
- PUT `/categories/{categoryId}`
- PUT `/categories/{categoryId}/deactivate`
- PUT `/categories/{categoryId}/activate`

### 1-3. User API
- GET `/users`
- GET `/users/{userId}`
- POST `/users`
- PUT `/users/{userId}`
- PUT `/users/{userId}/deactivate`
- PUT `/users/{userId}/activate`
- PUT `/users/{userId}/reset-password`
- GET `/users/{userId}/operation-histories`

### 1-4. Auth API
- GET `/`
- GET `/login`
- POST `/login`（Spring Security）
- POST `/logout`（Spring Security）

### 1-5. My Page API
- GET `/my-page`

---

## 2. 共通方針

### 2-1. Controller 方針
- Controller は thin controller とする
- 業務ロジックは Service に集約する
- Request DTO / Response DTO を使い分ける
- View エンドポイントと JSON API を混在させない
- 画面表示では `Model` に詰める属性名を固定し、テンプレート参照と一致させる

### 2-2. バリデーション
- DTO の形式チェックは `@Valid` で行う
- 業務ルール依存の判定は Service で行う

### 2-3. 例外ハンドリング
- ControllerAdvice で共通エラーレスポンスへ変換する
- 主な例外候補
  - `NotFoundException`
  - `UnauthorizedException`
  - `InvalidStateException`

### 2-4. ID 命名方針
- Entity 主キーは `id`
- API パス、Service 引数、DTO では意味付き ID 名を使う
  - `manualId`
  - `categoryId`
  - `notificationId`

---

## 3. Manual API 詳細

### 3-1. 一覧取得
- GET `/manuals/index`
- 条件例
  - keyword
  - categoryIds
  - statuses
- 初期表示では `statuses = [PENDING, APPROVED]` を前提とする
- 検索条件は `ManualSearchConditionDto` にまとめて扱ってよい
- 検索条件のフォーム送信名は以下に統一する
  - `keyword`
  - `categoryIds`
  - `statuses`
- 一覧返却項目は画面要件に合わせて以下を含める
  - manualId
  - title
  - status
  - updatedAt
  - createdByUser
- アコーディオン展開時に以下を返却してよい
  - categoryName
  - content
  - histories
- 一覧のアコーディオン collapse は行ごとに id をユニーク化する

### 3-2. 詳細取得
- GET `/manuals/{manualId}`
- 詳細返却項目は以下を含めてよい
  - manualId
  - categoryName
  - title
  - content
  - status
  - createdAt
  - updatedAt
  - createdByUser
  - histories
- histories の各要素には以下を含めてよい
  - changedAt
  - changeNote
  - changedByUser

### 3-3. 履歴取得
- GET `/manuals/{manualId}/histories`（将来 API）

### 3-4. 作成
- POST `/manuals`（将来 API）
- 画面は新規作成専用画面を使う
- 編集 / 複製は `manual-form` を使う
- API はユースケースごとに分けてよい
- 新規作成では changeNote は任意とする

### 3-5. 更新
- PUT `/manuals/{manualId}`（将来 API）

### 3-6. 状態変更
- submit
- approve
- rollback
- archive
- restore
- UI 上は rollback / archive / restore を詳細画面のインライン入力で `changeNote` 入力後に確定してよい
- approve は確認ダイアログを出し、必要に応じてインライン入力で履歴コメントを受け取ってよい
- restore 対象は `approvedAt` を保持した `ARCHIVED` マニュアルとする
- 詳細画面のボタン表示はモックに合わせて一時的に共通表示としてよい

### 3-7. 複製
- POST `/manuals/{manualId}/actions/copy`（将来 API）

---

## 4. DTO 方針

### 4-1. Request DTO
- `ManualRequestDto`
- `ManualDraftRequestDto`
- `ManualCopyRequestDto`
- `ManualSearchConditionDto`
- `CategoryRequestDto`
- `UserRequestDto`

### 4-2. Response DTO
- `ManualResponseDto`
- `ManualListDto`
- `ManualDetailDto`
- `CategoryResponseDto`
- `UserResponseDto`

### 4-3. 一覧検索 DTO 補足
- `ManualSearchConditionDto`
  - keyword
  - categoryIds
  - statuses
- `ManualListDto`
  - manualId
  - title
  - status
  - updatedAt
  - createdByName
  - categoryName
- 一覧取得では `Specification` を用いて `ManualSearchConditionDto` の条件を組み合わせてよい
- 内部項目名は `changeNote` のままでよいが、画面上の入力ラベルは `更新履歴` と表示してよい
- DTO の null 設計は以下で統一する
  - List: null 禁止、空 List を返す
  - 件数: `int` + 0 初期値
  - 必須項目: null 禁止
  - 任意項目: null 許可

### 4-4. 通知 / マイページ DTO
- `NotificationBadgeDto`
- `NotificationItemDto`
- `MyPageDto`
  - rollbackManualList
  - createdManualList
  - draftManualList

---

## 5. 通知ルール
- submit 時は APPROVER 全員に通知
- approve 時は作成者へ通知
- rollback 時は作成者へ通知
- 通知件数は未読のみを対象とする
- 一覧は状態条件で全件表示する
- 通知は一覧表示だけで既読化しない
- 承認完了時に対象マニュアルの `PENDING_APPROVAL` 通知を全削除する
- ホーム通知バッヂは
  - 上段: 差し戻し件数
  - 下段: 未承認件数

---

## 6. My Page API 補足
- `GET /my-page`
- USER の返却対象
  - 通知
  - 作成マニュアル
- APPROVER / ADMIN の返却対象
  - 通知
  - 作成マニュアル
  - 未承認マニュアル
- 初回表示時に必要な一覧を全取得し、タブ切替は画面内で表示切替する

---

## 7. Thymeleaf 連携実装メモ
- `listDto` は一覧表示データの参照起点
- `manualSearchConditionDto` は検索条件の保持参照起点
- `th:each` で複製される UI 要素に static id を使わない
- `EL1007E` 発生時は以下の順に確認する
  1. Controller が対象 Model 属性を追加しているか
  2. DTO の中身が null ではないか
  3. テンプレート参照名と DTO プロパティ名が一致しているか
