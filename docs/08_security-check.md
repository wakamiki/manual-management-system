# 08_security-check.md

Version: 01.01.00
更新日: 2026-05-31

---

## 1. 目的
本資料は、業務マニュアル管理システムのセキュリティ確認観点と対応状況を整理する。

---

## 2. 認証
- Spring Securityを使用する。
- ユーザー情報は `users` テーブルで管理する。
- パスワードはBCryptハッシュとして保存する。
- 初回パスワード変更フラグ `password_change_required` を保持する。
- 無効ユーザーはログイン不可とする。

---

## 3. 認可
- ロールは `USER / APPROVER / ADMIN / GUEST`。
- 更新系操作はController/Serviceで権限判定を行う。
- GUESTは閲覧専用とし、DB保存を伴う操作を不可とする。
- 業務ルール違反は専用例外で拒否し、画面へエラーメッセージを返す。

---

## 4. SQLインジェクション対策
- RepositoryはSpring Data JPAを使用する。
- ユーザー入力をSQL文字列へ直接連結する実装は採用していない。
- 手動SQLはDB初期構築用に限定し、アプリ実行時のユーザー入力処理には使わない。

---

## 5. CSRF対策
- Spring SecurityのCSRF保護を利用する。
- ThymeleafフォームではCSRFトークンを送信する。
- Postman直POSTテストでは、ログイン後にCSRFトークンを取得して送信する運用とした。

---

## 6. 本番設定
- `local` / `prod` プロファイルを分離する。
- ローカルではH2 file DBを使用する。
- 本番ではNeon PostgreSQLを使用する。
- 本番DB接続情報は環境変数で管理する。
- 本番環境ではH2コンソールを無効化する。
- DB接続情報、ゲストログイン情報、初期データSQLは公開資料に含めない。

---

## 7. H2コンソール制御
- `spring.h2.console.enabled=true` のときだけ `/h2-console/**` を許可する。
- `prod` では `spring.h2.console.enabled=false` とし、`/h2-console/**` を公開しない。
- H2コンソール用のCSRF除外もlocal時のみ有効にする。

---

## 8. セキュリティヘッダ
- `X-Content-Type-Options`
- `Referrer-Policy`
- `Permissions-Policy`
- `Strict-Transport-Security`
- `Content-Security-Policy`

---

## 9. 今後の改善
- JUnit + MockMvcによる認可・CSRF・異常系テストの自動化。
- 管理者操作ログの検索UI追加。
- 通知既読管理を実装する場合は、既読状態の改ざん対策をあわせて検討する。
