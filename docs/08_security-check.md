# 08_security-check.md

Version: 01.00.00
更新日: 2026-05-06

---

## 1. 目的
本資料は、業務マニュアル管理システムのセキュリティ確認観点と対応状況を整理する。

---

## 2. 認証
- Spring Securityを使用。
- ユーザー情報は `users` テーブルで管理。
- パスワードはBCryptハッシュとして保存。
- 初回パスワード変更フラグ `password_change_required` を保持。
- 無効ユーザーはログイン不可。

---

## 3. 認可
- ロールは `USER / APPROVER / ADMIN / GUEST`。
- 更新系操作はController/Serviceで権限判定を行う。
- GUESTは閲覧専用とし、DB保存を伴う操作を不可とする。
- 業務ルール違反は専用例外で拒否し、画面へエラーメッセージを返す。

---

## 4. SQLインジェクション対策
- RepositoryはSpring Data JPAを使用。
- 検索条件はJPAのメソッド/Criteria/Specification相当の仕組みで処理する。
- ユーザー入力をSQL文字列へ直接連結する実装は採用していない。
- 手動SQLはDB初期構築用に限定し、アプリ実行時のユーザー入力処理には使わない。

---

## 5. CSRF対策
- Spring SecurityのCSRF保護を利用。
- ThymeleafフォームではCSRFトークンを送信する。
- Postman直POSTテストでは、ログイン後にCSRFトークンを取得して送信する運用とした。

---

## 6. 本番設定
- `local` / `prod` プロファイルを分離。
- 本番環境ではH2コンソールを無効化。
- DB接続情報、ゲストログイン情報、初期データSQLは公開資料に含めない。
- Render環境変数で本番接続情報を管理する。

---

## 7. セキュリティヘッダ
- `X-Content-Type-Options`
- `Referrer-Policy`
- `Permissions-Policy`
- `Strict-Transport-Security`
- `Content-Security-Policy`

---

## 8. 今後の改善
- JUnit + MockMvcによる認可・CSRF・異常系テストの自動化。
- 管理者操作ログの検索UI追加。
- 通知既読管理を実装する場合は、既読状態の改ざん対策をあわせて検討する。
