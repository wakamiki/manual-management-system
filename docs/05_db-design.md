# 05_db-design.md

Version: 01.03.00  
更新日: 2026-04-11

---

## 1. DB 設計概要

### 1-1. 使用 DB
- 開発環境: H2 Database
- 将来拡張: PostgreSQL

### 1-2. 設計方針
- Entity 実装を基準に現在のテーブル構成を整理する
- 主キーは `id` に統一する
- ステータス遷移は DB 制約ではなく Service / Entity で制御する
- 変更履歴は `manual_histories` に append-only で保持する
- 通知は今後拡張予定のため、現時点では参考情報として分離して扱う

---

## 2. ER 関係

### 2-1. 現在の関係概要
```text
categories               1 --- N manuals
users                    1 --- N manuals
manuals                  1 --- N manual_histories
users                    1 --- N manual_histories
users                    1 --- N user_operation_histories (target)
```

### 2-2. 主な関連
- categories 1 : N manuals
- users 1 : N manuals
- manuals 1 : N manual_histories
- users 1 : N manual_histories
- users 1 : N user_operation_histories

---

## 3. テーブル一覧

### 3-1. 現在の実装対象
- categories
- users
- manuals
- manual_histories
- user_operation_histories

### 3-2. 参考扱い / 今後拡張
- notifications
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
| display_order | INTEGER | NOT NULL | 表示順 |
| category_name | VARCHAR(50) | NOT NULL | カテゴリ名 |
| created_at | TIMESTAMP | NOT NULL | 作成日時 |
| is_active | BOOLEAN | NOT NULL | 使用可否 |
| updated_at | TIMESTAMP | NOT NULL | 更新日時 |

### 4-3. 補足
- 現在の Entity 実装では `category_name` に UNIQUE 制約は未付与
- 使用停止カテゴリは `is_active = false` で表現する

---

## 5. users

### 5-1. 用途
ログイン、権限管理、利用状態管理

### 5-2. カラム
| カラム名 | 型 | 制約 | 用途 |
| --- | --- | --- | --- |
| id | BIGINT | PK / NOT NULL | ユーザーID |
| login_id | VARCHAR(50) | NOT NULL | ログインID |
| password | VARCHAR(255) | NOT NULL | パスワード |
| display_name | VARCHAR(50) | NOT NULL | 表示名 |
| role | VARCHAR(20) | NOT NULL | USER / APPROVER / ADMIN |
| is_active | BOOLEAN | NOT NULL | 利用可否 |
| last_login_at | TIMESTAMP | NULL | 最終ログイン日時 |
| created_at | TIMESTAMP | NULL | 作成日時 |
| updated_at | TIMESTAMP | NULL | 更新日時 |

### 5-3. 補足
- docs 上の論理名は `userId` を使うことがあるが、DB / Entity の実カラムは `login_id`
- 現在の Entity 実装では `created_at` / `updated_at` に NOT NULL 制約は未付与

---

## 6. manuals

### 6-1. 用途
マニュアル本体、最新状態、カテゴリ、作成者情報の保持

### 6-2. カラム
| カラム名 | 型 | 制約 | 用途 |
| --- | --- | --- | --- |
| id | BIGINT | PK / NOT NULL | マニュアルID |
| category_id | BIGINT | FK | 所属カテゴリ |
| operated_by_user_id | BIGINT | FK | 作成者 / 更新者として扱うユーザー |
| title | VARCHAR(100) | NOT NULL | タイトル |
| content | VARCHAR(10000) | NOT NULL | 本文 |
| status | VARCHAR(20) | NOT NULL | DRAFT / PENDING / APPROVED / ARCHIVED |
| created_at | TIMESTAMP | NULL | 作成日時 |
| updated_at | TIMESTAMP | NULL | 更新日時 |
| approved_at | TIMESTAMP | NULL | 承認日時 |

### 6-3. 外部キー
- category_id → categories.id
- operated_by_user_id → users.id

### 6-4. 補足
- 現在の Entity 名は `user` だが、JoinColumn は `operated_by_user_id`
- docs 上では `createdByUser` として説明している箇所があるため、後続実装で名称整理の余地あり
- 本文カラムは Entity 実装上 `VARCHAR(10000)` 相当で扱っている

---

## 7. manual_histories

### 7-1. 用途
更新履歴、更新履歴コメント、更新者の記録

### 7-2. カラム
| カラム名 | 型 | 制約 | 用途 |
| --- | --- | --- | --- |
| id | BIGINT | PK / NOT NULL | 履歴ID |
| manual_id | BIGINT | FK | 対象マニュアル |
| change_note | VARCHAR(100) | NOT NULL | 更新履歴コメント |
| changed_at | TIMESTAMP | NOT NULL | 更新日時 |
| change_user_id | BIGINT | FK | 更新者 |

### 7-3. 外部キー
- manual_id → manuals.id
- change_user_id → users.id

### 7-4. 補足
- 画面表示ラベルは `更新履歴` だが、内部項目名は `changeNote`
- 更新履歴は一覧 / 詳細 / 状態変更時の監査情報として利用する

---

## 8. user_operation_histories

### 8-1. 用途
管理画面での操作記録、監査用ログ

### 8-2. カラム
| カラム名 | 型 | 制約 | 用途 |
| --- | --- | --- | --- |
| id | BIGINT | PK / NOT NULL | 操作履歴ID |
| target_user_id | BIGINT | FK | 操作対象ユーザー |
| operated_by_user | VARCHAR(255) | NOT NULL | 操作実行者 |
| operation_type | VARCHAR(30) | NOT NULL | 操作種別 |
| operation_detail | VARCHAR(100) | NOT NULL | 操作詳細 |
| created_at | TIMESTAMP | NOT NULL | 記録日時 |

### 8-3. 外部キー
- target_user_id → users.id

### 8-4. 補足
- 現在の Entity 実装では `operated_by_user` は `User` 関連ではなく文字列保持
- 監査強化時は `operated_by_user_id` へ変更する余地がある

---

## 9. notifications（参考）

### 9-1. 現在の状態
- `Notification` Entity は存在するが、実装はまだ空クラス
- 現時点では DB 設計の確定対象ではなく、参考扱いとする

### 9-2. 将来想定カラム
| カラム名 | 型 | 制約 | 用途 |
| --- | --- | --- | --- |
| id | BIGINT | PK / NOT NULL | 通知ID |
| target_user_id | BIGINT | FK / NOT NULL | 通知対象ユーザー |
| manual_id | BIGINT | FK / NOT NULL | 対象マニュアル |
| type | VARCHAR(30) | NOT NULL | 通知種別 |
| message | VARCHAR(255) | NOT NULL | 表示文言 |
| is_read | BOOLEAN | NOT NULL | 既読フラグ |
| created_at | TIMESTAMP | NOT NULL | 通知作成日時 |

### 9-3. 補足
- 実装再開時は Entity / Repository / Service と合わせて再定義する

---

## 10. 主要な差分メモ
- `users.user_id` ではなく、現在実装は `login_id`
- `manuals.created_by_user_id` ではなく、現在実装は `operated_by_user_id`
- `manual_histories.changed_by_user_id` ではなく、現在実装は `change_user_id`
- `notifications` は docs 先行ではなく、現在は未実装扱い
