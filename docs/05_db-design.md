# 05_db-design.md

Version: 01.03.01  
更新日: 2026-04-13

---

## 1. DB 設計方針
### 1-1. 使用 DB
- 開発: H2 Database
- 未来: PostgreSQL

### 1-2. 設計ルール
- Entity のフィールド名をベースにテーブル / カラムを設計する
- 主キーは `id`
- 履歴は `manual_histories` に append-only で保存する
- 操作履歴は `user_operation_histories` に保存する

---

## 2. ER 概要
```text
categories               1 --- N manuals
users                    1 --- N manuals
manuals                  1 --- N manual_histories
users                    1 --- N manual_histories
users                    1 --- N user_operation_histories (target)
```

---

## 3. テーブル一覧
### 3-1. 主要テーブル
- categories
- users
- manuals
- manual_histories
- user_operation_histories

### 3-2. 将来拡張テーブル（予定）
- notifications
- favorite_manuals
- manual_comments
- tags
- manual_tags
- manual_read_checks

---

## 4. categories

| カラム名 | 型 | 制約 | 説明 |
| --- | --- | --- | --- |
| id | BIGINT | PK / NOT NULL | カテゴリID |
| display_order | INTEGER | NOT NULL | 表示順 |
| category_name | VARCHAR(50) | NOT NULL | カテゴリ名 |
| created_at | TIMESTAMP | NOT NULL | 作成日時 |
| is_active | BOOLEAN | NOT NULL | 使用中フラグ |
| updated_at | TIMESTAMP | NOT NULL | 更新日時 |

---

## 5. users

| カラム名 | 型 | 制約 | 説明 |
| --- | --- | --- | --- |
| id | BIGINT | PK / NOT NULL | ユーザーID |
| login_id | VARCHAR(50) | NOT NULL | ログインID |
| password | VARCHAR(255) | NOT NULL | パスワード |
| display_name | VARCHAR(50) | NOT NULL | 表示名 |
| role | VARCHAR(20) | NOT NULL | USER / APPROVER / ADMIN |
| is_active | BOOLEAN | NOT NULL | 有効フラグ |
| last_login_at | TIMESTAMP | NULL | 最終ログイン日時 |
| created_at | TIMESTAMP | NULL | 作成日時 |
| updated_at | TIMESTAMP | NULL | 更新日時 |

---

## 6. manuals

| カラム名 | 型 | 制約 | 説明 |
| --- | --- | --- | --- |
| id | BIGINT | PK / NOT NULL | マニュアルID |
| category_id | BIGINT | FK | カテゴリ |
| operated_by_user_id | BIGINT | FK | 作成者 / 更新者 |
| title | VARCHAR(100) | NOT NULL | タイトル |
| content | VARCHAR(10000) | NOT NULL | 本文 |
| status | VARCHAR(20) | NOT NULL | DRAFT / PENDING / APPROVED / ARCHIVED |
| created_at | TIMESTAMP | NULL | 作成日時 |
| updated_at | TIMESTAMP | NULL | 更新日時 |
| approved_at | TIMESTAMP | NULL | 承認日時 |
| is_rolled_back | BOOLEAN | NOT NULL | 差し戻しフラグ |

### 補足
- `operated_by_user_id` は作成者 / 更新者の参照として運用する
- `is_rolled_back` は差し戻し判定用のフラグ

---

## 7. manual_histories

| カラム名 | 型 | 制約 | 説明 |
| --- | --- | --- | --- |
| id | BIGINT | PK / NOT NULL | 履歴ID |
| manual_id | BIGINT | FK | マニュアル |
| change_note | VARCHAR(100) | NOT NULL | 変更内容 |
| changed_at | TIMESTAMP | NOT NULL | 更新日時 |
| change_user_id | BIGINT | FK | 更新者 |

---

## 8. user_operation_histories

| カラム名 | 型 | 制約 | 説明 |
| --- | --- | --- | --- |
| id | BIGINT | PK / NOT NULL | 操作履歴ID |
| target_user_id | BIGINT | FK | 対象ユーザー |
| operated_by_user | VARCHAR(255) | NOT NULL | 操作者 |
| operation_type | VARCHAR(30) | NOT NULL | 操作種別 |
| operation_detail | VARCHAR(100) | NOT NULL | 操作詳細 |
| created_at | TIMESTAMP | NOT NULL | 操作日時 |

---

## 9. notifications（将来拡張）
- Entity は空実装のため、DB 設計は将来確定
