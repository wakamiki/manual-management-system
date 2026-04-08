# 07_development-process-memo.md

Version: 01.00.01  
更新日: 2026-04-08

---

## 2026-03-29

### 作業概要
Spring Boot を用いたプロジェクトの初期構成を作成し、Manual Management System の土台を整備した。

### 実施内容
- Spring Boot 初期構成の作成
- Git / GitHub 連携準備
- HomeController 作成
- Manual Entity / Repository / Controller の初期作成
- `POST /manuals`
- `GET /manuals`

### 学び
- Spring Boot の基本構成
- MVC の基本的な役割分担

---

## 2026-03-30

### 作業概要
詳細取得、更新処理、Service 層導入を進めた。

### 実施内容
- `GET /manuals/{id}` 実装
- `ManualService` 作成
- `Optional` を使った取得処理の整理
- `PUT /manuals/{id}` 実装
- `updatedAt` 更新処理
- Postman による API 確認

### 学び
- `Optional` の使い方
- getter / setter と Lombok の理解
- Controller / Service の責務分離

---

## 2026-03-31

### 作業概要
CRUD と検索、ステータス管理の土台を整えた。

### 実施内容
- status 検索 API の検討
- README / docs 更新
- approvedAt 制御の整理
- ステータス一覧の整理

### 学び
- 業務ルールを API と Entity の両面で考える必要性

---

## 2026-04-04

### 作業概要
Entity 設計、Repository 整理、画面モック作成に着手した。

### 実施内容
- Manual / User / Category の Entity 設計整理
- `createdAt` `updatedAt` `approvedAt` などの protected field 整理
- ManualHistory 設計
- トップ画面、ログイン画面などのモック作成開始
- Bootstrap レイアウトの学習

### 学び
- Entity の責務と DTO の責務の違い
- 画面モックを先に作る進め方

---

## 2026-04-05

### 作業概要
フロントエンドモックの主要 UI 整備と、複製機能のバックエンド準備を進めた。

### 実施内容
- `index.html` をトップ画面として整備
- 左サイドバー + 右アコーディオン一覧へ調整
- 使用停止カテゴリ UI 追加
- ページネーション追加
- `#56969D` ベースの配色に統一
- `login.html`
- `manual-detail.html`
- `manual-form` につながる入力画面系の整理開始
- `user-management.html`
- `category-management.html`
- 別タブ運用方針の整理
- `ManualCopyRequestDto` 着手
- DTO の役割整理

### 学び
- 基準画面を最初に固めると、その後の画面作成速度が上がる
- 実務利用を前提にした別タブ運用の有効性

---

## 2026-04-06

### 作業概要
バックエンド設計整理と理解の深化を中心に進めた。

### 実施内容
- DTO 対応の再開
- id と userId の命名整理方針を検討
- `createDraftManual()` / `createAndSubmitManual()` の責務分離方針整理
- ステータス定義・遷移ルール再確認
- 例外メッセージ方針整理
- `@Valid` による入力検証方針整理
- Manual と ManualHistory の責務整理
- MVC / DTO / Model / Mapper の理解整理

### 学び
- 小さな処理単位ではなく、ユースケース単位で考える重要性
- Service = 業務ルール、Mapper = データ変換という理解

---

## 2026-04-07

### 作業概要
`docs-local` の整理、共有資料への反映、通知・例外・Validation まわりの理解を進めた。

### 実施内容
- `docs-local` を Markdown へ整理
- 共有資料へ必要事項を反映
- `ManualDraftRequestDto` 追加方針整理
- `@NotNull` と `@NotBlank` の使い分け確認
- `ErrorResponse` / `GlobalExceptionHandler` の役割整理
- 補完が壊れるほどエラーを放置しない方針を明確化

### 学び
- エラー放置が IDE 補完崩れの原因になる
- 一つずつ直すほうが結果的に速い

---

## 2026-04-08

### 作業概要
通知機能、マイページ、入力画面統合、資料整備、UI 文字サイズ統一、文字化け修正を進めた。

### 実施内容

#### 命名整理・調査
- Entity 一覧の参照元を docs から確認
- `ManualRequestDto` の命名衝突を調査
- `id` は Entity、意味付き ID はコード上で使う方針を整理

#### 通知機能・マイページ設計
- 通知ルール整理
  - submit 時は APPROVER 全員に通知
  - approve 時は作成者へ通知
  - rollback 時は作成者へ通知
- マイページ方針整理
  - タブ構成
  - 初期表示は `通知`
  - `通知 / 作成マニュアル / 未承認マニュアル`
- ホーム通知バッヂ方針整理
  - 上段: 差し戻し件数
  - 下段: 未承認件数
  - 承認通知はバッヂ表示しない

#### Java ファイル追加
- Notification 関連の最小構成ファイル追加
- MyPage 関連の最小構成ファイル追加

#### モック作成・更新
- `my-page.html` 作成
- 業務ダッシュボード型レイアウト採用
- タブ構成のモック作成
- `index.html` にホームアイコンと通知バッヂ追加
- 文字だけのラベルをやめて、アイコンのみ導線へ変更

#### 入力画面統合
- 入力画面名を `manual-form` に統一
- `manual-create` / `manual-edit` は廃止
- 編集画面ベースの共通入力画面方針へ整理

#### 文字サイズ統一
- ページタイトル: h4
- セクションタイトル: h5
- カードタイトル / 通知タイトル: 16px
- 本文: 14px
- 補助情報: 12px
- アラート / メッセージ: 13px
- バッヂ数字: 10px
- 小さな補足メモ: 11px

#### 文字化け修正
- mock HTML の日本語本文を復旧
- `README` と主要 docs を日本語で書き直し
- UTF-8 BOM 付きで再保存

### 学び
- 通知は機能単体ではなく、通知を見る入口と一緒に設計するとまとまりやすい
- 入力画面は画面統合しても、API はユースケースごとに分けてよい
- 文字化けは本文破損と文字コードの両面で見直す必要がある

### 次回着手予定
- Notification Entity / Service の中身実装
- MyPage DTO の項目確定
- コンパイルエラー整理
- DTO と Service シグネチャの整合
