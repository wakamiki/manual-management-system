# 04_api-design.md

Version: 01.03.08  
更新日: 2026-04-13

---

## 1. API 一覧

### 1-1. Manual API
- GET `/api/manuals`
- GET `/api/manuals/{manualId}`
- GET `/api/manuals/{manualId}/histories`
- POST `/api/manuals`
- PUT `/api/manuals/{manualId}`
- POST `/api/manuals/{manualId}/actions/copy`
- POST `/api/manuals/{manualId}/actions/submit`
- POST `/api/manuals/{manualId}/actions/approve`
- POST `/api/manuals/{manualId}/actions/rollback`
- POST `/api/manuals/{manualId}/actions/archive`
- POST `/api/manuals/{manualId}/actions/restore`

### 1-2. Category API
- GET `/api/categories`
- POST `/api/categories`
- PUT `/api/categories/{categoryId}`
- PUT `/api/categories/{categoryId}/deactivate`
- PUT `/api/categories/{categoryId}/activate`

### 1-3. User API
- GET `/api/users`
- GET `/api/users/{userId}`
- POST `/api/users`
- PUT `/api/users/{userId}`
- PUT `/api/users/{userId}/deactivate`
- PUT `/api/users/{userId}/activate`
- PUT `/api/users/{userId}/reset-password`
- GET `/api/users/{userId}/operation-histories`

### 1-4. Auth API
- POST `/api/auth/login`
- POST `/api/auth/logout`

### 1-5. My Page API
- GET `/my-page`

---

## 2. 共通方針

### 2-1. Controller 方針
- Controller は thin controller とする
- 業務ロジックは Service に集約する
- Request DTO / Response DTO を使い分ける

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
- GET `/api/manuals`
- 条件例
  - keyword
  - categoryIds
  - statuses
- 初期表示では `statuses = [DRAFT, PENDING, APPROVED]` を前提としてよい
- 検索条件は `ManualSearchConditionDto` にまとめて扱ってよい
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

### 3-2. 詳細取得
- GET `/api/manuals/{manualId}`
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
- GET `/api/manuals/{manualId}/histories`

### 3-4. 作成
- POST `/api/manuals`
- 画面は新規作成専用画面を使う
- 編集 / 複製は `manual-form` を使う
- API はユースケースごとに分けてよい
- 新規作成では changeNote は任意とする

### 3-5. 更新
- PUT `/api/manuals/{manualId}`

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
- POST `/api/manuals/{manualId}/actions/copy`

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
