# 04_api-design.md

Version: 01.02.00
最終更新日: 2026-04-07

---

## 1. API一覧

### 1-1. Manual API

* GET /api/manuals
* GET /api/manuals/{manualId}
* GET /api/manuals/{manualId}/histories
* POST /api/manuals
* PUT /api/manuals/{manualId}
* POST /api/manuals/{manualId}/actions/copy
* POST /api/manuals/{manualId}/actions/submit
* POST /api/manuals/{manualId}/actions/approve
* POST /api/manuals/{manualId}/actions/rollback
* POST /api/manuals/{manualId}/actions/archive
* POST /api/manuals/{manualId}/actions/restore

### 1-2. Category API

* GET /api/categories
* POST /api/categories
* PUT /api/categories/{id}
* PUT /api/categories/{id}/deactivate
* PUT /api/categories/{id}/activate

### 1-3. User API

* GET /api/users
* GET /api/users/{id}
* POST /api/users
* PUT /api/users/{id}
* PUT /api/users/{id}/deactivate
* PUT /api/users/{id}/activate
* PUT /api/users/{id}/reset-password
* GET /api/users/{id}/operation-histories

### 1-4. Auth API

* POST /api/auth/login
* POST /api/auth/logout

---

## 2. 共通仕様

### 2-1. 認証

* 全APIログイン必須
* セッション認証
* userId + password

### 2-2. 共通レスポンス形式

### 成功時

```json
{
  "message": "成功メッセージ",
  "data": {}
}
```

### エラー時

```json
{
  "message": "エラーメッセージ",
  "errorCode": "ERROR_CODE",
  "timestamp": "2026-04-04T10:00:00"
}
```

### 2-3. ステータスコード

* 200 OK
* 201 Created
* 400 Bad Request
* 401 Unauthorized
* 403 Forbidden
* 404 Not Found
* 409 Conflict
* 500 Internal Server Error

### 2-4. API設計方針

* Controller は thin controller とし、入力受付とレスポンス返却に責務を限定する
* 入力は Request DTO、返却は Response DTO または List / Detail DTO を基本とする
* 状態変更系 API は対象 ID を URL で受け、業務操作は action で表現する
* 一覧 / 詳細 / 履歴取得は参照系 API、作成 / 更新 / 状態変更は操作系 API として整理する

### 2-5. 例外ハンドリング方針

* DTO の形式不正は `@Valid` による入力バリデーションで処理する
* 業務ルール違反は Service で例外を送出する
* ControllerAdvice で共通エラーレスポンスへ変換する

主な例外分類

* `ResourceNotFoundException` : 404
* `ValidationException` : 400
* `UnauthorizedException` : 403
* `InvalidStatusTransitionException` : 409
* `MethodArgumentNotValidException` : 400
* `Exception` : 500

---

## 3. Manual API 詳細

### 3-1. マニュアル新規作成

**POST /api/manuals**

#### Request

```json
{
  "title": "発注業務の流れ",
  "content": "本文",
  "categoryId": 1,
  "changeNote": "新規作成"
}
```

#### Response

```json
{
  "message": "新しいマニュアルを作成しました",
  "data": {
    "id": 1
  }
}
```

#### 成功メッセージ

* 新しいマニュアルを作成しました 

#### 主なエラー

* タイトルを入力してください
* 本文を入力してください
* カテゴリを選択してください

#### Status Code

* 201
* 400
* 401

---

### 3-2. マニュアル一覧取得

**GET /api/manuals**

#### Query Parameter

* keyword
* categoryId
* includeArchived

#### Response

```json
{
  "data": [
    {
      "id": 1,
      "title": "発注業務の流れ",
      "status": "APPROVED",
      "updatedAt": "2026-04-04T10:00:00"
    }
  ]
}
```

#### 0件時メッセージ

* 検索条件に一致するマニュアルはありません 

---

### 3-3. マニュアル詳細取得

**GET /api/manuals/{manualId}**

#### Response

```json
{
  "data": {
    "id": 1,
    "title": "発注業務の流れ",
    "content": "本文",
    "status": "APPROVED",
    "category": "営業部"
  }
}
```

#### 主なエラー

* 指定されたマニュアルは存在しません 

---

### 3-4. マニュアル更新

**PUT /api/manuals/{manualId}**

#### Request

```json
{
  "title": "更新後タイトル",
  "content": "更新本文",
  "changeNote": "手順修正"
}
```

#### 成功メッセージ

* マニュアルを更新しました 

---

### 3-5. マニュアル申請

**POST /api/manuals/{manualId}/actions/submit**

#### 成功メッセージ

* 申請を受け付けました

#### 主なエラー

* この状態では申請できません 

---

### 3-6. マニュアル承認

**POST /api/manuals/{manualId}/actions/approve**

#### 成功メッセージ

* マニュアルを承認しました 

#### 主なエラー

* 承認できるのは承認者または管理者のみです
* 作成者本人は自分のマニュアルを承認できません 

#### Status Code

* 200
* 403
* 409

---

### 3-7. マニュアル差し戻し

**POST /api/manuals/{manualId}/actions/rollback**

#### Request

```json
{
  "changeNote": "修正依頼"
}
```

#### 成功メッセージ

* 修正依頼として差し戻しました 

---

### 3-8. マニュアル旧版化

**POST /api/manuals/{manualId}/actions/archive**

#### 成功メッセージ

* マニュアルをアーカイブしました 

---

### 3-9. マニュアル復帰

**POST /api/manuals/{manualId}/actions/restore**

#### 成功メッセージ

* マニュアルを復帰しました 

---

### 3-10. マニュアル複製

**POST /api/manuals/{manualId}/actions/copy**

#### Request

```json
{
  "categoryId": 2,
  "changeNote": "別部署向け複製"
}
```

#### 成功メッセージ

* マニュアルを複製しました 

---

#### 複製業務ルール

* 複製先ステータスは DRAFT
* approvedAt は null
* createdAt / updatedAt は新規採番
* 作成者は複製実行ユーザー
* categoryId 指定必須
* changeNote 必須

### 3-11. マニュアル履歴取得

**GET /api/manuals/{manualId}/histories**

#### Response

```json
{
  "data": [
    {
      "changeNote": "本文修正",
      "changedAt": "2026-04-04T10:00:00"
    }
  ]
}
```

---

## 4. Category API 詳細

### 4-1. カテゴリ一覧取得

**GET /api/categories**

### 4-2. カテゴリ作成

**POST /api/categories**

#### 成功メッセージ

* カテゴリを作成しました

### 4-3. カテゴリ更新

**PUT /api/categories/{id}**

### 4-4. カテゴリ停止

**PUT /api/categories/{id}/deactivate**

#### 成功メッセージ

* カテゴリを使用停止にしました 

### 4-5. カテゴリ再有効化

**PUT /api/categories/{id}/activate**

#### 成功メッセージ

* カテゴリを再有効化しました 

---

## 5. User API 詳細

### 5-1. ユーザー一覧取得

**GET /api/users**

### 5-2. ユーザー詳細取得

**GET /api/users/{id}**

### 5-3. ユーザー作成

**POST /api/users**

### 5-4. ユーザー更新

**PUT /api/users/{id}**

### 5-5. ユーザー停止

**PUT /api/users/{id}/deactivate**

#### 成功メッセージ

* ユーザーアカウントを停止しました 

### 5-6. ユーザー再有効化

**PUT /api/users/{id}/activate**

#### 成功メッセージ

* ユーザーアカウントを再有効化しました 

### 5-7. パスワードリセット

**PUT /api/users/{id}/reset-password**

### 5-8. 操作履歴取得

**GET /api/users/{id}/operation-histories**

---

## 6. Auth API 詳細

### 6-1. ログイン

**POST /api/auth/login**

#### Request

```json
{
  "userId": "admin",
  "password": "password"
}
```

#### 主なエラー

* ユーザーIDまたはパスワードが正しくありません
* このアカウントは利用停止中です 

---

### 6-2. ログアウト

**POST /api/auth/logout**

---

## 7. エラーメッセージ一覧

* 入力バリデーション
* 権限エラー
* 状態遷移エラー
* データ不存在
* 認証エラー 

---

## 8. DTO設計方針

### 8-1. Request DTO

* `ManualDraftRequestDto` : 下書き保存用
* `ManualRequestDto` : 新規作成 / 更新
* `ManualCopyRequestDto` : 複製
* `ManualActionRequestDto` : 差し戻し等の changeNote 入力
* `CategoryRequestDto` : カテゴリ作成 / 更新
* `UserRequestDto` : ユーザー作成 / 更新
* `LoginRequestDto` : ログイン

### 8-2. Response DTO

* `ManualResponseDto` : 作成 / 更新 / 状態変更結果
* `ManualListDto` : 一覧表示
* `ManualDetailDto` : 詳細表示
* `ManualHistoryDto` : 履歴表示
* `CategoryResponseDto` : カテゴリ表示
* `UserResponseDto` : ユーザー表示

### 8-3. 設計ルール

* Request DTO には入力値のみを持たせる
* Entity をそのままレスポンスへ返さない
* displayName や categoryName など表示用データは Response DTO に詰める
* 業務上重要な値（status, approvedAt, createdAt, updatedAt など）は Service / Entity 側で制御する

---

## 9. 今後追加予定API

* 関連マニュアルAPI
* お気に入りAPI
* コメントAPI
* タグAPI
* 確認チェックAPI

---

## 改版履歴

| Version  | 日付         | 更新内容         |
| -------- | ---------- | ------------ |
| 01.00.00 | 2026-04-04 | 初版作成         |
| 01.01.00 | 2026-04-04 | API設計資料統合版作成 |
| 01.02.00 | 2026-04-07 | DTO方針、ControllerAdvice前提の例外方針、API設計ルールを追記 |
