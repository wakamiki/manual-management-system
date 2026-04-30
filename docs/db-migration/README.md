# DB Migration Guide (Render PostgreSQL)

Version: 01.10.01  
更新日: 2026-04-30

---

## 1. 目的
- 本番DB（Render PostgreSQL）を手動SQLで初期構築するための手順。
- 自動DDLに依存せず、再現可能な形でテーブルと初期データを管理する。

---

## 2. 対象ファイル
- `V1__create_tables.sql`
  - テーブル作成 + 外部キー制約作成
- `V2__seed_initial_data.sql`
  - 初期管理者ユーザー + 初期カテゴリ投入

---

## 3. 実行順
1. `V1__create_tables.sql`
2. `V2__seed_initial_data.sql`

---

## 4. 事前確認
- 実行先DBが本番 `manual_management_prod` であること
- DBeaver接続がRender PostgreSQLを向いていること
- `V2__seed_initial_data.sql` のBCryptハッシュ置換が完了していること

---

## 5. 実行後確認SQL

```sql
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;
```

```sql
SELECT id, login_id, role, is_active, password_change_required
FROM users
ORDER BY id;
```

```sql
SELECT id, category_name, display_order, is_active
FROM categories
ORDER BY display_order;
```

---

## 6. 注意事項
- `V1` は初回構築向け。既存環境で再実行すると制約追加で失敗する可能性がある。
- 認証情報（DB URL / Password）はdocsに平文で残さない。
- 初期管理者の平文パスワードは保存せず、必要時に再発行する。
---

## Production Apply Note (2026-04-30)
- Applied to Render PostgreSQL (manual_management_prod)
- Applied SQL files:
  - `V1__create_tables.sql`
  - `V2__seed_initial_data.sql`
- Web URL:
  - `https://manual-management-system-1.onrender.com`
