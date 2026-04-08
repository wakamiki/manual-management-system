# 04_api-design.md

Version: 01.03.01  
更新日: 2026-04-08

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
- GET `/api/my-page`

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
  - `ResourceNotFoundException`
  - `ValidationException`
  - `UnauthorizedException`
  - `InvalidStatusTransitionException`

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
  - categoryId
  - statuses

### 3-2. 詳細取得
- GET `/api/manuals/{manualId}`

### 3-3. 履歴取得
- GET `/api/manuals/{manualId}/histories`

### 3-4. 作成
- POST `/api/manuals`
- 画面は `manual-form` を使うが、API はユースケースごとに分けてよい

### 3-5. 更新
- PUT `/api/manuals/{manualId}`

### 3-6. 状態変更
- submit
- approve
- rollback
- archive
- restore

### 3-7. 複製
- POST `/api/manuals/{manualId}/actions/copy`

---

## 4. DTO 方針

### 4-1. Request DTO
- `ManualRequestDto`
- `ManualDraftRequestDto`
- `ManualCopyRequestDto`
- `CategoryRequestDto`
- `UserRequestDto`

### 4-2. Response DTO
- `ManualResponseDto`
- `ManualDetailDto`
- `CategoryResponseDto`
- `UserResponseDto`

### 4-3. 通知 / マイページ DTO
- `NotificationBadgeDto`
- `NotificationItemDto`
- `MyPageDto`

---

## 5. 通知ルール
- submit 時は APPROVER 全員に通知
- approve 時は作成者へ通知
- rollback 時は作成者へ通知
- ホーム通知バッヂは
  - 上段: 差し戻し件数
  - 下段: 未承認件数

---

## 6. My Page API 補足
- `GET /api/my-page`
- USER の返却対象
  - 通知
  - 作成マニュアル
- APPROVER / ADMIN の返却対象
  - 通知
  - 作成マニュアル
  - 未承認マニュアル
