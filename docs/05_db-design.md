# 05_db-design.md

Version: 01.03.05
更新日: 2026-04-27

---

## 1. DB設計方針
### 1-1. 利用DB
- 開発DB: H2 file DB
- 接続URL: `jdbc:h2:file:./data/testdb`
- 本番移行先想定: PostgreSQL

### 1-2. 命名方針
- 主キーは `id` に統一
- 外部キーは `<entity>_id` を基本とする

### 1-3. 注意事項
- `users` テーブル名を使用する（`user` は予約語衝突回避）
- 設計書と実装差分がある場合、現時点ではコードを正とする

### 1-4. PostgreSQL移行時の注意

- H2とPostgreSQLでは予約語、boolean、timestamp、enum保存の挙動差に注意する
- Entityの `@Table` 名は小文字スネークケースへ寄せる
- 本番では `ddl-auto=update` を避け、初期化手順を管理する
- users / notifications / user_operation_histories などのテーブル名を設計書と一致させる

---

## 2. テーブル関連

```text
categories               1 --- N manuals
users                    1 --- N manuals
manuals                  1 --- N manualHistories
users                    1 --- N manualHistories
users                    1 --- N user_operation_histories (target)
users                    1 --- N user_operation_histories (operatedBy)
users                    1 --- N Notifications (target)
manuals                  1 --- N Notifications
```

---

## 3. テーブル一覧
- categories
- users
- manuals
- manualHistories
- user_operation_histories
- Notifications

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
| password | VARCHAR(255) | NOT NULL | ハッシュ化パスワード |
| display_name | VARCHAR(50) | NOT NULL | 表示名 |
| role | VARCHAR(20) | NOT NULL | USER / APPROVER / ADMIN / GUEST |
| is_active | BOOLEAN | NOT NULL | 有効フラグ |
| last_login_at | TIMESTAMP | NULL | 最終ログイン日時 |
| created_at | TIMESTAMP | NULL | 作成日時 |
| updated_at | TIMESTAMP | NULL | 更新日時 |
| password_change_required | BOOLEAN | NOT NULL | 初回変更必須フラグ |

---

## 6. manuals

| カラム名 | 型 | 制約 | 説明 |
| --- | --- | --- | --- |
| id | BIGINT | PK / NOT NULL | マニュアルID |
| category_id | BIGINT | FK | カテゴリ |
| operated_by_user_id | BIGINT | FK | 作成/更新ユーザー |
| title | VARCHAR(100) | NOT NULL | タイトル |
| content | VARCHAR(10000) | NOT NULL | 本文 |
| status | VARCHAR(20) | NULL（実装準拠） | DRAFT / PENDING / APPROVED / ARCHIVED |
| created_at | TIMESTAMP | NULL | 作成日時 |
| updated_at | TIMESTAMP | NULL | 更新日時 |
| approved_at | TIMESTAMP | NULL | 承認日時 |
| is_rolled_back | BOOLEAN | NOT NULL | 差し戻しフラグ |

### 6-1. 命名差分メモ
- DBカラム名: `is_rolled_back`
- Entityプロパティ名: `isRolledback`
- 旧誤記の `IS_ROLLEDBACK` は使用しない

---

## 7. manualHistories

| カラム名 | 型 | 制約 | 説明 |
| --- | --- | --- | --- |
| id | BIGINT | PK / NOT NULL | 履歴ID |
| manual_id | BIGINT | FK | マニュアル |
| change_note | VARCHAR(100) | NOT NULL | 更新履歴コメント |
| changed_at | TIMESTAMP | NOT NULL | 変更日時 |
| change_user_id | BIGINT | FK | 変更ユーザー |

---

## 8. user_operation_histories

| カラム名 | 型 | 制約 | 説明 |
| --- | --- | --- | --- |
| id | BIGINT | PK / NOT NULL | 操作履歴ID |
| target_user_id | BIGINT | FK | 対象ユーザー |
| operated_by_user_id | BIGINT | FK | 操作者ユーザー |
| operation_type | VARCHAR(30) | NOT NULL | 操作種別 |
| operation_detail | VARCHAR(100) | NOT NULL | 操作詳細 |
| created_at | TIMESTAMP | NOT NULL | 操作日時 |

---

## 9. Notifications

| カラム名 | 型 | 制約 | 説明 |
| --- | --- | --- | --- |
| id | BIGINT | PK / NOT NULL | 通知ID |
| target_user_id | BIGINT | FK | 通知対象ユーザー |
| manual_id | BIGINT | FK | 対象マニュアル |
| type | VARCHAR(30) | NULL（実装準拠） | 通知種別 |
| message | VARCHAR | NULL | 通知文面 |
| created_at | TIMESTAMP | NULL | 通知作成日時 |

### 9-1. 実装準拠メモ
- 現在のEntityには `is_read` は未実装
- テーブル名は `Notifications`（先頭大文字）を使用
