# Manual Management System

Version: 01.10.04
更新日: 2026-05-06

## 概要
日々更新される業務マニュアルを、現場で継続的に管理することを想定したポートフォリオ用システムです。
マニュアルの作成、更新、承認、履歴管理、カテゴリ管理、通知確認を通じて、業務システム開発の設計力・保守性・説明力を示すことを目的としています。

## 資料一覧
- システム概要スライド
  - `docs/portfolio/01_system-overview.pdf`
- 操作説明スライド
  - `docs/portfolio/02_operation-guide.pdf`
- ポートフォリオ資料の読み方
  - `docs/portfolio/README.md`
- 仕様・設計ドキュメント
  - `docs/01_project-overview-and-basic-design.md`
    - プロジェクトの目的、全体方針、基本設計の概要。
  - `docs/02_system-specification-and-detailed-design.md`
    - 業務仕様、権限/状態遷移、詳細設計の基準。
  - `docs/03_screen-design.md`
    - 画面一覧、画面遷移、各画面の表示・操作要件。
  - `docs/04_api-design.md`
    - エンドポイント一覧、DTO方針、例外/認可のAPI設計。
  - `docs/05_db-design.md`
    - テーブル定義、カラム制約、エンティティとの対応。
  - `docs/06_test-specification.md`
    - テスト観点・ケース定義・実施基準。
  - `docs/06_test-result.md`
    - 実施済みテスト結果、判定、補足メモ。
  - `docs/08_security-check.md`
    - 認証/認可、CSRF、SQLインジェクション、本番設定の確認記録。

## 主な機能
- マニュアル一覧表示
- マニュアル詳細表示
- マニュアル入力画面
- マニュアルの申請 / 承認 / 差し戻し / アーカイブ / 復帰
- マニュアル複製
- カテゴリ管理
- ユーザー管理
- 通知機能
- マイページ
- ゲスト閲覧ログイン（閲覧専用）

## ステータス
- DRAFT
- PENDING
- APPROVED
- ARCHIVED

## 利用技術
- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Thymeleaf
- Bootstrap
- H2 Database
- Git / GitHub

## 開発環境
- 開発DBは H2 file DB を使用
- 接続先: `jdbc:h2:file:./data/testdb`
- H2コンソール: `/h2-console`
- Spring Security 導入済み

## 本番環境（Render）
- 公開URL: `https://manual-management-system-1.onrender.com`
- 本番DBは Render PostgreSQL を使用
- アプリとDBは同一リージョン配置を推奨
- 本番DB構築は手動SQL実行で管理（`ddl-auto=update` 任せにしない）
- マイグレーションSQL:
  - `docs/db-migration/V1__create_tables.sql`
  - 初期データSQLは機密情報を含むため非公開管理

## セキュリティ構成
- Spring Security を導入
- `local` / `prod` のプロファイル分離を導入
  - `local`: H2 コンソール有効
  - `prod`: H2 コンソール無効
- `SecurityConfig` で `h2-console` の公開許可を廃止
- セキュリティヘッダを追加
  - `X-Content-Type-Options: nosniff`
  - `Referrer-Policy: strict-origin-when-cross-origin`
  - `Permissions-Policy`
  - `Strict-Transport-Security`
  - `Content-Security-Policy`
- `users` テーブルベースのログイン認証へ移行済み

## ゲスト閲覧機能
- ログイン画面に `閲覧専用でログイン` 導線を設ける
- GUEST 権限は一覧表示、詳細表示、検索、ユーザー管理画面表示、カテゴリ管理画面表示が可能（いずれも閲覧のみ）
- 新規作成、編集、削除、申請、承認、アーカイブ、復帰、管理画面の更新系操作、その他DB保存を伴う操作は不可
- ログイン後は `ゲスト閲覧中` を表示する
- 非活性ボタンには `ログインすると利用できます` などの理由を表示する

## フロントエンド方針
- モック画面は HTML + Bootstrap + CSS で先に作成する
- モックは `src/main/resources/static/mock` に配置する
- CSS は `src/main/resources/static/css` に配置する
- テンプレート用 CSS は `src/main/resources/templates/css` に配置する
- 編集 / 複製の入力画面は `manual-form.html` を共通利用する
- `manual-form` は編集 / 複製で共通利用し、`マニュアルID / 作成日時 / 更新日時 / 作成者` を表示する
- 詳細画面、入力画面、管理画面、マイページは別タブで開く
- `index` の左サイドバーはクイックビューと補助ナビゲーションとして扱う
- `manual-detail` では差し戻し / アーカイブ / 復帰をインライン入力で確定する
- 承認は確認ダイアログを表示し、必要な場合のみインライン入力で更新履歴を残す
- 画面上のラベルは `changeNote` ではなく `更新履歴` と表示する

## 画面構成
- `login.html`
- `index.html`
- `manual-detail.html`
- `manual-form.html`
- `user-management.html`
- `category-management.html`
- `my-page.html`

## マイページ方針
- マイページは個人向けダッシュボードとして扱う
- タブは以下の3つを使用する
  - `通知`（未確定）
  - `作成マニュアル`
  - `未承認マニュアル`
- `未承認マニュアル` は APPROVER / ADMIN を対象とする
- 初回表示時に必要データを一括取得し、タブ切り替えは画面内で表示切替する

## 通知方針
- `submit` 時は APPROVER 全員に通知する
- `approve` 時は作成者へ通知する
- `rollback` 時は作成者へ通知する
- ホームアイコン横には通知バッヂを表示する
  - 上段: 差し戻し通知件数
  - 下段: 未承認通知件数
- 承認通知はバッヂ表示対象外とする
- 通知件数は未読のみを対象とする
- 一覧は状態条件で全件表示する
- 通知は一覧表示だけで既読化しない
- 既読機能は実装しない（通知削除運用）
- 承認完了時に対象マニュアルの `PENDING_APPROVAL` 通知を全削除する

## ID 命名方針
- Entity の主キーは `id` に統一する
- Controller / Service / Repository / DTO の引数名は意味付き ID 名を使う
  - `manualId`
  - `categoryId`
  - `notificationId`
- 業務識別子として使う `userId` は将来的に `loginId` への改名も検討する

## 環境変数一覧

| 変数名 | 用途 | ローカル | 本番 |
| --- | --- | --- | --- |
| PORT | サーバ起動ポート（`server.port`） | 8081（省略時） | Renderが設定 |
| SPRING_DATASOURCE_URL | DB接続URL（JDBC形式） | `jdbc:h2:file:./data/testdb`（省略時） | 必須 |
| SPRING_DATASOURCE_DRIVER_CLASS_NAME | JDBCドライバ | `org.h2.Driver`（省略時） | `org.postgresql.Driver` |
| SPRING_DATASOURCE_USERNAME | DBユーザー名 | `sa`（省略時） | 必須 |
| SPRING_DATASOURCE_PASSWORD | DBパスワード | 空（省略時） | 必須 |
| GUEST_LOGIN_ID | ゲストログインID | 任意 | 必須 |
| GUEST_LOGIN_PASSWORD | ゲストログインPW | 任意 | 必須 |

## 現在の開発状況
- 本番公開
  - Render へデプロイ済み
  - 公開URL: `https://manual-management-system-1.onrender.com`
  - Render PostgreSQL 接続で稼働中
- 実装済み（主要機能）
  - 認証/認可（Spring Security、ロール別制御、GUEST閲覧専用）
  - マニュアル機能（作成/編集/複製/申請/承認/差し戻し/アーカイブ/復帰）
  - 管理機能（ユーザー管理、カテゴリ管理、重複カテゴリ確認フロー）
  - 通知/マイページ（通知作成・削除運用、個人向け一覧表示）
- 実装済み（品質・運用）
  - 例外処理の共通化（`GlobalExceptionHandler`）
  - パスワード運用（BCrypt、初期パスワード、初回変更フラグ）
  - UIメッセージ導線（成功/失敗フラッシュ、資格情報通知）
  - 公開後UI不具合修正（ユーザー更新モードID表示、MyPageアイコン表示）
  - CSP調整（Bootstrap Icons CDN読込許可）
  - テスト運用整備（`06_test-specification.md` / `06_test-result.md`）
- 継続中
  - ポートフォリオ提出用資料（全体説明スライド、操作説明PDF）作成
  - 公開環境の性能観測（Render要因/アプリ要因の切り分け）
  - 運用向け最終ドキュメント整備（デモ手順・操作ガイド）

## テスト運用メモ（現行）
- 直POST系は `Login_ユーザー` → `Get_CSRF` → 対象POST の順で実行
- 認可異常系は「HTTPステータス」だけでなく、例外ログ（`UnauthorizedException` / `InvalidStateException`）で業務拒否を判定する
- 画面で検証可能な入力バリデーションはUIテストを優先し、直POSTは改ざん耐性・状態遷移不正系に集中する

## 将来拡張予定機能
- ユーザー/カテゴリ連携
  - ユーザーとカテゴリの紐づけ管理
  - 担当カテゴリに応じた表示/操作制御
- 通知機能の拡張
  - 通知履歴の保存
  - 既読/未読の状態管理（現行は削除運用）
- マニュアル作成支援
  - 正式マニュアル作成支援機能（テンプレート/入力補助）
- マイページの強化
  - お気に入りマニュアル
  - 並び替え/絞り込みオプション追加
- 管理機能の強化
  - ユーザー管理画面のユーザー検索機能
  - 監査ログの検索UI追加
  - 一括操作（停止/復帰/通知削除）の管理者機能
- テスト運用の高度化
  - JUnit + MockMvc による異常系テスト自動化
  - 回帰テストの定型シナリオ化

## 更新履歴
| Version | Date | 内容 |
| --- | --- | --- |
| 01.10.04 | 2026-05-06 | 提出資料導線、リリース記録、セキュリティ確認資料を追加 |
| 01.10.03 | 2026-05-03 | 公開後UI崩れ修正とCSP調整（Bootstrap Icons表示不具合対応）を反映 |
| 01.10.02 | 2026-05-01 | H2公開抑止（profile分離）とセキュリティヘッダ追加を反映 |
| 01.10.01 | 2026-04-30 | 公開URL追記、環境変数一覧を実装準拠へ修正、開発状況を本番公開後状態へ更新 |
| 01.10.00 | 2026-04-30 | Render本番デプロイ成功に伴い本番運用状態へ更新 |
| 01.09.18 | 2026-04-30 | 本番環境（Render PostgreSQL）運用方針とDBマイグレーション導線を追記 |
| 01.09.17 | 2026-04-29 | ポートフォリオ資料導線（スライド/PDF予定）を追記 |
| 01.09.16 | 2026-04-27 | Postman直POSTテスト運用とテスト反映方針を追記 |
| 01.09.15 | 2026-04-27 | 環境変数一覧追加 |
| 01.09.14 | 2026-04-24 | 認可運用（本人変更/管理者初期化）と例外処理集約、テスト結果記録テンプレート整備を追記 |
| 01.09.13 | 2026-04-23 | パスワードハッシュ化、初期パスワード通知、初回変更導線を追記 |
| 01.09.12 | 2026-04-21 | user/category 管理画面の mode 切替運用、userId更新方針、カテゴリ重複確認フローを追記 |
| 01.09.11 | 2026-04-18 | DTO整理、メッセージ追加、try-catch 方針整理を追記 |
| 01.09.10 | 2026-04-17 | index Thymeleaf 反映進行と DTO null 設計ルールを追記 |
| 01.09.09 | 2026-04-17 | 開発環境（H2 file DB）と GUEST 閲覧仕様を追記 |
| 01.09.08 | 2026-04-14 | クイックビューと検索フォームの進捗を追記 |
| 01.09.07 | 2026-04-13 | 承認完了時の通知一括削除方針を追記 |
| 01.09.06 | 2026-04-13 | 通知の既読は明示操作で行う方針を追記 |
| 01.09.05 | 2026-04-13 | 通知件数と一覧表示の役割分離を反映 |
| 01.09.04 | 2026-04-13 | DB設計書の再整備、マイページ通知タブを未確定に変更 |
| 01.09.03 | 2026-04-13 | マイページ取得方式とロールバック判定方針を追記 |
| 01.09.02 | 2026-04-11 | 詳細画面のインライン入力方針、更新履歴ラベル、DB設計書更新を反映 |
| 01.09.01 | 2026-04-11 | トップ画面の検索初期値、クイックビュー、使用停止カテゴリの表示方針を更新 |
| 01.09.00 | 2026-04-10 | Spring Security 導入確認、Lombok 依存見直し、認証情報取得方針を追記 |
| 01.08.01 | 2026-04-08 | README の文字化け解消、マイページ / 通知 / manual-form 方針を再整理 |
| 01.08.00 | 2026-04-08 | 通知機能、マイページ、ホーム通知バッヂ、ID 命名方針を整理 |
| 01.07.00 | 2026-04-06 | DTO 対応、Validation / Mapper / History 分離の設計整理 |
| 01.06.00 | 2026-04-05 | 画面モック整理、別タブ運用、入力画面統合の前提整理 |
