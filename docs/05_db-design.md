# 05_db-design.md

Version: 01.01.00
最終更新日: 2026-04-04

---

## 1. データベース設計概要

### 1-1. 使用DB

* 開発環境：H2 Database
* 本番環境：PostgreSQL（Supabase）

### 1-2. 設計方針

* 第3正規形を意識した分割
* 履歴系は別テーブル管理
* 物理削除は原則行わない
* 状態 / 有効フラグによる論理管理
* 将来拡張を考慮した中間テーブル設計

---

## 2. ER図

### 2-1. ER図（テキスト版）

```text
categories
   1
   │
   └── N manuals
            │
            └── N manual_histories

users
   1
   ├── N manuals
   ├── N user_operation_histories (target)
   └── N user_operation_histories (operated_by)
```

### 2-2. リレーション概要

* categories 1 : N manuals
* users 1 : N manuals
* manuals 1 : N manual_histories
* users 1 : N user_operation_histories

---

## 3. テーブル一覧

### 3-1. 基本テーブル

* categories
* users
* manuals
* manual_histories
* user_operation_histories

### 3-2. 将来拡張テーブル

* manual_relations
* manual_read_checks
* manual_comments
* tags
* manual_tags
* favorite_manuals 

---

## 4. categories テーブル設計

### 4-1. 用途

カテゴリ管理 / 表示順 / 使用停止管理

### 4-2. カラム定義

| カラム名          | 型           | 制約                      | 用途     |
| ------------- | ----------- | ----------------------- | ------ |
| id            | BIGINT      | PK / NOT NULL           | カテゴリID |
| category_name | VARCHAR(50) | NOT NULL / UNIQUE       | カテゴリ名  |
| display_order | INTEGER     | NOT NULL                | 表示順    |
| is_active     | BOOLEAN     | NOT NULL / default true | 使用可否   |
| created_at    | TIMESTAMP   | NOT NULL                | 作成日時   |
| updated_at    | TIMESTAMP   | NOT NULL                | 更新日時   |

### 4-3. 制約

* PK: id
* UNIQUE: category_name
* NOT NULL: category_name, display_order

---

## 5. users テーブル設計

### 5-1. 用途

ログイン / 権限管理 / アカウント状態管理

### 5-2. カラム定義

| カラム名          | 型            | 制約                      | 用途                      |
| ------------- | ------------ | ----------------------- | ----------------------- |
| id            | BIGINT       | PK / NOT NULL           | 内部ID                    |
| user_id       | VARCHAR(50)  | NOT NULL / UNIQUE       | ログインID                  |
| password      | VARCHAR(255) | NOT NULL                | ハッシュ化PW                 |
| display_name  | VARCHAR(50)  | NOT NULL                | 表示名                     |
| role          | VARCHAR(20)  | NOT NULL                | USER / APPROVER / ADMIN |
| is_active     | BOOLEAN      | NOT NULL / default true | 利用可否                    |
| last_login_at | TIMESTAMP    | NULL可                   | 最終ログイン                  |
| created_at    | TIMESTAMP    | NOT NULL                | 作成日時                    |
| updated_at    | TIMESTAMP    | NOT NULL                | 更新日時                    |

### 5-3. CHECK制約

```sql
CHECK (role IN ('USER', 'APPROVER', 'ADMIN'))
```

---

## 6. manuals テーブル設計

### 6-1. 用途

マニュアル本体 / 状態管理 / 作成者管理

### 6-2. カラム定義

| カラム名               | 型            | 制約            | 用途      |
| ------------------ | ------------ | ------------- | ------- |
| id                 | BIGINT       | PK / NOT NULL | マニュアルID |
| category_id        | BIGINT       | FK / NOT NULL | 所属カテゴリ  |
| created_by_user_id | BIGINT       | FK / NOT NULL | 作成者     |
| title              | VARCHAR(100) | NOT NULL      | タイトル    |
| content            | TEXT         | NOT NULL      | 本文      |
| status             | VARCHAR(20)  | NOT NULL      | 状態      |
| created_at         | TIMESTAMP    | NOT NULL      | 作成日時    |
| updated_at         | TIMESTAMP    | NOT NULL      | 更新日時    |
| approved_at        | TIMESTAMP    | NULL可         | 承認日時    |

### 6-3. 外部キー

* category_id → categories.id
* created_by_user_id → users.id

### 6-4. CHECK制約

```sql
CHECK (status IN ('DRAFT','PENDING','APPROVED','ARCHIVED'))
```

---

## 7. manual_histories テーブル設計

### 7-1. 用途

変更履歴 / changeNote保存

### 7-2. カラム定義

| カラム名        | 型            | 制約            |
| ----------- | ------------ | ------------- |
| id          | BIGINT       | PK            |
| manual_id   | BIGINT       | FK / NOT NULL |
| change_note | VARCHAR(100) | NOT NULL      |
| changed_at  | TIMESTAMP    | NOT NULL      |

### 7-3. 外部キー

* manual_id → manuals.id

---

### 7-4. Repository検索方針

履歴表示は changed_at の降順を標準とする。

主な取得方法

* manual_id 指定 + changed_at DESC
* 全件 + changed_at DESC

## 8. user_operation_histories テーブル設計

### 8-1. 用途

ユーザー管理操作履歴 / 監査ログ

### 8-2. カラム定義

| カラム名                | 型            | 制約            |
| ------------------- | ------------ | ------------- |
| id                  | BIGINT       | PK            |
| target_user_id      | BIGINT       | FK / NOT NULL |
| operated_by_user_id | BIGINT       | FK / NOT NULL |
| operation_type      | VARCHAR(30)  | NOT NULL      |
| operation_detail    | VARCHAR(100) | NOT NULL      |
| created_at          | TIMESTAMP    | NOT NULL      |

### 8-3. 外部キー

* target_user_id → users.id
* operated_by_user_id → users.id

### 8-4. operation_type 候補

* CREATE_USER
* UPDATE_USER
* CHANGE_ROLE
* DEACTIVATE_USER
* ACTIVATE_USER
* RESET_PASSWORD 

---

## 9. インデックス設計

### 9-1. 検索性能向上

```sql
CREATE INDEX idx_manuals_title ON manuals(title);
CREATE INDEX idx_manuals_status ON manuals(status);
CREATE INDEX idx_manuals_category_id ON manuals(category_id);
CREATE INDEX idx_users_user_id ON users(user_id);
```



---

## 10. 更新・削除ルール

### 10-1. 論理削除方針

* manuals：ARCHIVED
* users：is_active
* categories：is_active

### 10-2. 物理削除

原則禁止

### 10-3. 運用ルール

* 作成者本人承認禁止
* PENDING は作成者のみ編集可
* APPROVED 直接編集禁止
* 複製時は作成者を複製者へ変更

---

## 11. 将来追加予定テーブル

### 11-1. favorite_manuals

お気に入り機能

### 11-2. manual_read_checks

確認チェック機能

### 11-3. manual_tags

タグ中間テーブル

### 11-4. manual_comments

コメント機能

### 11-5. manual_relations

関連マニュアル

---

## 12. 将来のDB拡張方針

* 添付ファイル
* 通知
* AI生成履歴
* 操作ログ強化

---

## 改版履歴

| Version  | 日付         | 更新内容        |
| -------- | ---------- | ----------- |
| 01.00.00 | 2026-04-04 | 初版作成        |
| 01.01.00 | 2026-04-04 | DB設計資料統合版作成 |
