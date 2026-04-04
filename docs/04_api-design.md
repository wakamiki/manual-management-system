# 04_api-design.md

Version: 01.01.00
最終更新日: 2026-04-04

---

## 1. API一覧

### 1-1. Manual API

* POST /manuals
* GET /manuals
* GET /manuals/{id}
* PUT /manuals/{id}
* PUT /manuals/{id}/submit
* PUT /manuals/{id}/approve
* PUT /manuals/{id}/rollback
* PUT /manuals/{id}/archive
* PUT /manuals/{id}/restore
* POST /manuals/{id}/copy
* GET /manuals/{id}/histories

### 1-2. Category API

* GET /categories
* POST /categories
* PUT /categories/{id}
* PUT /categories/{id}/deactivate
* PUT /categories/{id}/activate

### 1-3. User API

* GET /users
* GET /users/{id}
* POST /users
* PUT /users/{id}
* PUT /users/{id}/deactivate
* PUT /users/{id}/activate
* PUT /users/{id}/reset-password
* GET /users/{id}/operation-histories

### 1-4. Auth API

* POST /auth/login
* POST /auth/logout

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

---

## 3. Manual API 詳細

### 3-1. マニュアル新規作成

**POST /manuals**

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

**GET /manuals**

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

**GET /manuals/{id}**

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

**PUT /manuals/{id}**

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

**PUT /manuals/{id}/submit**

#### 成功メッセージ

* 申請を受け付けました

#### 主なエラー

* この状態では申請できません 

---

### 3-6. マニュアル承認

**PUT /manuals/{id}/approve**

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

**PUT /manuals/{id}/rollback**

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

**PUT /manuals/{id}/archive**

#### 成功メッセージ

* マニュアルをアーカイブしました 

---

### 3-9. マニュアル復帰

**PUT /manuals/{id}/restore**

#### 成功メッセージ

* マニュアルを復帰しました 

---

### 3-10. マニュアル複製

**POST /manuals/{id}/copy**

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

**GET /manuals/{id}/histories**

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

**GET /categories**

### 4-2. カテゴリ作成

**POST /categories**

#### 成功メッセージ

* カテゴリを作成しました

### 4-3. カテゴリ更新

**PUT /categories/{id}**

### 4-4. カテゴリ停止

**PUT /categories/{id}/deactivate**

#### 成功メッセージ

* カテゴリを使用停止にしました 

### 4-5. カテゴリ再有効化

**PUT /categories/{id}/activate**

#### 成功メッセージ

* カテゴリを再有効化しました 

---

## 5. User API 詳細

### 5-1. ユーザー一覧取得

**GET /users**

### 5-2. ユーザー詳細取得

**GET /users/{id}**

### 5-3. ユーザー作成

**POST /users**

### 5-4. ユーザー更新

**PUT /users/{id}**

### 5-5. ユーザー停止

**PUT /users/{id}/deactivate**

#### 成功メッセージ

* ユーザーアカウントを停止しました 

### 5-6. ユーザー再有効化

**PUT /users/{id}/activate**

#### 成功メッセージ

* ユーザーアカウントを再有効化しました 

### 5-7. パスワードリセット

**PUT /users/{id}/reset-password**

### 5-8. 操作履歴取得

**GET /users/{id}/operation-histories**

---

## 6. Auth API 詳細

### 6-1. ログイン

**POST /auth/login**

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

**POST /auth/logout**

---

## 7. エラーメッセージ一覧

* 入力バリデーション
* 権限エラー
* 状態遷移エラー
* データ不存在
* 認証エラー 

---

## 8. 今後追加予定API

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
