# 05_db-design.md

Version: 01.02.01  
更新日: 2026-04-08

---

## 1. DB 設計概要

### 1-1. 使用 DB
- 開発環境: H2 Database
- 将来拡張: PostgreSQL

### 1-2. 設計方針
- 第3正規形を意識した設計とする
- 履歴は append-only で管理する
- 状態遷移はテーブル制約ではなく業務ロジックで制御する
- 主キーは `id` に統一する

---

## 2. ER 関係

### 2-1. 関係概要
```text
categories 1 --- N manuals
users      1 --- N manuals
manuals    1 --- N manual_histories
users      1 --- N notifications (target)
manuals    1 --- N notifications
users      1 --- N user_operation_histories
```

### 2-2. 主な関連
- categories 1 : N manuals
- users 1 : N manuals
- manuals 1 : N manual_histories
- users 1 : N notifications
- manuals 1 : N notifications
- users 1 : N user_operation_histories

---

## 3. テーブル一覧

### 3-1. 採用テーブル
- categories
- users
- manuals
- manual_histories
- notifications
- user_operation_histories

### 3-2. 将来拡張候補
- favorite_manuals
- manual_comments
- tags
- manual_tags
- manual_read_checks

---

## 4. categories

### 4-1. 用途
カテゴリ管理、表示順、使用可否の管理

### 4-2. カラム
| カラム名 | 型 | 制約 | 用途 |
| --- | --- | --- | --- |
| id | BIGINT | PK / NOT NULL | カテゴリID |
| category_name | VARCHAR(50) | NOT NULL / UNIQUE | カテゴリ名 |
| display_order | INTEGER | NOT NULL | 表示順 |
| is_active | BOOLEAN | NOT NULL / default true | 使用可否 |
| created_at | TIMESTAMP | NOT NULL | 作成日時 |
| updated_at | TIMESTAMP | NOT NULL | 更新日時 |

---

## 5. users

### 5-1. 用途
ログイン、権限管理、利用状態管理

### 5-2. カラム
| カラム名 | 型 | 制約 | 用途 |
| --- | --- | --- | --- |
| id | BIGINT | PK / NOT NULL | ユーザーID |
| user_id | VARCHAR(50) | NOT NULL / UNIQUE | ログインID |
| password | VARCHAR(255) | NOT NULL | ハッシュ化パスワード |
| display_name | VARCHAR(50) | NOT NULL | 表示名 |
| role | VARCHAR(20) | NOT NULL | USER / APPROVER / ADMIN |
| is_active | BOOLEAN | NOT NULL / default true | 利用可否 |
| last_login_at | TIMESTAMP | NULL | 最終ログイン日時 |
| created_at | TIMESTAMP | NOT NULL | 作成日時 |
| updated_at | TIMESTAMP | NOT NULL | 更新日時 |

### 5-3. CHECK
```sql
CHECK (role IN ('USER', 'APPROVER', 'ADMIN'))
```

---

## 6. manuals

### 6-1. 用途
マニュアル本体、最新状態、作成者情報の保持

### 6-2. カラム
| カラム名 | 型 | 制約 | 用途 |
| --- | --- | --- | --- |
| id | BIGINT | PK / NOT NULL | マニュアルID |
| category_id | BIGINT | FK / NOT NULL | 所属カテゴリ |
| created_by_user_id | BIGINT | FK / NOT NULL | 作成者 |
| title | VARCHAR(100) | NOT NULL | タイトル |
| content | TEXT | NOT NULL | 本文 |
| status | VARCHAR(20) | NOT NULL | ステータス |
| created_at | TIMESTAMP | NOT NULL | 作成日時 |
| updated_at | TIMESTAMP | NOT NULL | 更新日時 |
| approved_at | TIMESTAMP | NULL | 承認日時 |

### 6-3. 外部キー
- category_id → categories.id
- created_by_user_id → users.id

### 6-4. CHECK
```sql
CHECK (status IN ('DRAFT', 'PENDING', 'APPROVED', 'ARCHIVED'))
```

---

## 7. manual_histories

### 7-1. 用途
変更履歴、changeNote、変更者の記録

### 7-2. カラム
| カラム名 | 型 | 制約 | 用途 |
| --- | --- | --- | --- |
| id | BIGINT | PK / NOT NULL | 履歴ID |
| manual_id | BIGINT | FK / NOT NULL | 対象マニュアル |
| changed_by_user_id | BIGINT | FK / NOT NULL | 変更者 |
| change_note | VARCHAR(100) | NOT NULL | 変更理由 |
| changed_at | TIMESTAMP | NOT NULL | 変更日時 |

### 7-3. 外部キー
- manual_id → manuals.id
- changed_by_user_id → users.id

---

## 8. notifications

### 8-1. 用途
申請、承認、差し戻しに関する通知の保持

### 8-2. カラム
| カラム名 | 型 | 制約 | 用途 |
| --- | --- | --- | --- |
| id | BIGINT | PK / NOT NULL | 通知ID |
| target_user_id | BIGINT | FK / NOT NULL | 通知対象ユーザー |
| manual_id | BIGINT | FK / NOT NULL | 対象マニュアル |
| type | VARCHAR(30) | NOT NULL | 通知種別 |
| message | VARCHAR(255) | NOT NULL | 表示文言 |
| is_read | BOOLEAN | NOT NULL / default false | 既読フラグ |
| created_at | TIMESTAMP | NOT NULL | 通知作成日時 |

### 8-3. 外部キー
- target_user_id → users.id
- manual_id → manuals.id

### 8-4. 想定 type
- MANUAL_SUBMITTED
- MANUAL_APPROVED
- MANUAL_ROLLED_BACK

### 8-5. インデックス候補
- target_user_id
- is_read
- created_at

---

## 9. user_operation_histories

### 9-1. 用途
管理画面での操作記録や監査用ログ

### 9-2. 補足
- 本ポートフォリオでは最小構成で保持する
- 詳細カラムは将来拡張対象とする
