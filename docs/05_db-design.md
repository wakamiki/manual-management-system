# 05_db-design.md

Version: 01.10.02
更新日: 2026-05-31

---

## 1. DB設計方針

### 1-1. 利用DB
- ローカル開発DB: H2 file DB
- ローカル接続先: `jdbc:h2:file:./data/testdb`
- 本番DB: Neon PostgreSQL
- 本番公開アプリ基盤: Render Web Service
- Render PostgreSQL から Neon PostgreSQL へ移行済み

### 1-2. 本番DB運用方針
- 本番DB接続情報は環境変数で管理する
- 実際の接続URL、ユーザー名、パスワードはコードや公開docsへ記載しない
- 本番環境の `ddl-auto` は `validate` とする
- 本番DBのテーブル作成は schema SQL を手動投入して管理する
- 初期データは seed SQL を手動投入する
- seed SQL は認証情報などを含む可能性があるため非公開管理とする

### 1-3. 命名方針
- 主キーは `id` に統一する
- 外部キーは `<entity>_id` を基本とする
- テーブル名はスネークケースの複数形を使用する

### 1-4. PostgreSQL運用時の注意
- H2とPostgreSQLでは予約語、boolean、timestamp、文字列長の扱いに差があるため、本番DDLは手動SQLで確認する
- 本番では `ddl-auto=update` を使用しない
- Entityとテーブル定義の差分は、起動時の `validate` で検知する

---

## 2. テーブル関連

```text
categories               1 --- N manuals
users                    1 --- N manuals
manuals                  1 --- N manual_histories
users                    1 --- N manual_histories
users                    1 --- N user_operation_histories (target)
users                    1 --- N user_operation_histories (operatedBy)
users                    1 --- N notifications (target)
manuals                  1 --- N notifications
```

---

## 3. テーブル一覧
- categories
- users
- manuals
- manual_histories
- user_operation_histories
- notifications

---

## 4. categories

| カラム名 | 型 | 制約 | 説明 |
| --- | --- | --- | --- |
| id | BIGSERIAL | PK / NOT NULL | カテゴリID |
| display_order | INTEGER | NOT NULL | 表示順 |
| category_name | VARCHAR(50) | NOT NULL | カテゴリ名 |
| created_at | TIMESTAMP | NOT NULL | 作成日時 |
| is_active | BOOLEAN | NOT NULL | 使用中フラグ |
| updated_at | TIMESTAMP | NOT NULL | 更新日時 |

---

## 5. users

| カラム名 | 型 | 制約 | 説明 |
| --- | --- | --- | --- |
| id | BIGSERIAL | PK / NOT NULL | ユーザーID |
| login_id | VARCHAR(50) | NOT NULL | ログインID |
| password | VARCHAR(255) | NOT NULL | BCryptハッシュ化パスワード |
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
| id | BIGSERIAL | PK / NOT NULL | マニュアルID |
| category_id | BIGINT | FK | カテゴリID |
| operated_by_user_id | BIGINT | FK | 作成/更新ユーザーID |
| title | VARCHAR(100) | NOT NULL | タイトル |
| content | VARCHAR(10000) | NOT NULL | 本文 |
| status | VARCHAR(20) | NULL | DRAFT / PENDING / APPROVED / ARCHIVED |
| created_at | TIMESTAMP | NULL | 作成日時 |
| updated_at | TIMESTAMP | NULL | 更新日時 |
| approved_at | TIMESTAMP | NULL | 承認日時 |
| is_rolled_back | BOOLEAN | NOT NULL | 差し戻し経験フラグ |

---

## 7. manual_histories

| カラム名 | 型 | 制約 | 説明 |
| --- | --- | --- | --- |
| id | BIGSERIAL | PK / NOT NULL | 履歴ID |
| manual_id | BIGINT | FK | マニュアルID |
| change_note | VARCHAR(100) | NOT NULL | 更新履歴コメント |
| changed_at | TIMESTAMP | NOT NULL | 変更日時 |
| change_user_id | BIGINT | FK | 変更ユーザーID |

---

## 8. user_operation_histories

| カラム名 | 型 | 制約 | 説明 |
| --- | --- | --- | --- |
| id | BIGSERIAL | PK / NOT NULL | 操作履歴ID |
| target_user_id | BIGINT | FK | 対象ユーザーID |
| operated_by_user_id | BIGINT | FK | 操作実行ユーザーID |
| operation_type | VARCHAR(30) | NOT NULL | 操作種別 |
| operation_detail | VARCHAR(100) | NOT NULL | 操作詳細 |
| created_at | TIMESTAMP | NOT NULL | 操作日時 |

---

## 9. notifications

| カラム名 | 型 | 制約 | 説明 |
| --- | --- | --- | --- |
| id | BIGSERIAL | PK / NOT NULL | 通知ID |
| target_user_id | BIGINT | FK | 通知対象ユーザーID |
| manual_id | BIGINT | FK | 対象マニュアルID |
| type | VARCHAR(30) | NULL | 通知種別 |
| message | TEXT | NULL | 通知メッセージ |
| created_at | TIMESTAMP | NULL | 通知作成日時 |

### 9-1. 実装メモ
- 現行Entityには `is_read` は実装しない
- 通知は既読状態を持たず、削除運用とする
- テーブル名は `notifications` を使用する

---

## 10. 本番DB移行メモ
- 本番DBは Render PostgreSQL から Neon PostgreSQL へ移行済み
- 本番アプリは Render 上で公開継続する
- 本番DBの schema SQL は `docs/db-migration/V1__create_tables.sql` を手動投入する
- 初期データSQLは機密情報を含むため非公開管理とする
- 本番DB接続情報は環境変数で管理する
- Northflank は検証したが、無料枠では CPU/メモリ不足により正式採用しない
