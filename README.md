# Manual Management System

Version: 01.09.00  
更新日: 2026-04-10

## 概要
日々更新される業務マニュアルを、現場で継続的に管理することを想定したポートフォリオ用システムです。  
マニュアルの作成、更新、承認、履歴管理、カテゴリ管理、通知確認を通じて、業務システム開発の設計力・保守性・説明力を示すことを目的としています。

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

## フロントエンド方針
- モック画面は HTML + Bootstrap + CSS で先に作成する
- モックは `src/main/resources/static/mock` に配置する
- CSS は `src/main/resources/static/css` に配置する
- 入力系画面は `manual-form.html` に統一する
- `manual-form` は新規 / 編集 / 複製で共通利用し、新規時は補助情報を非表示、編集 / 複製時は `マニュアルID / 作成日時 / 更新日時 / 作成者` を表示する
- 詳細画面、入力画面、管理画面、マイページは別タブで開く

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
- 初期表示は `通知` タブ
- タブは以下の3つを使用する
  - `通知`
  - `作成マニュアル`
  - `未承認マニュアル`
- `未承認マニュアル` は APPROVER / ADMIN を対象とする

## 通知方針
- `submit` 時は APPROVER 全員に通知する
- `approve` 時は作成者へ通知する
- `rollback` 時は作成者へ通知する
- ホームアイコン横には通知バッヂを表示する
  - 上段: 差し戻し通知件数
  - 下段: 未承認通知件数
- 承認通知はバッヂ表示対象外とする

## ID 命名方針
- Entity の主キーは `id` に統一する
- Controller / Service / Repository / DTO の引数名は意味付き ID 名を使う
  - `manualId`
  - `categoryId`
  - `notificationId`
- 業務識別子として使う `userId` は将来的に `loginId` への改名も検討する

## 現在の開発状況
- DTO 対応の整理を進行中
- Validation / 例外 / Mapper の設計方針を整理中
- 通知機能とマイページの設計・モック作成を実施済み
- 文字サイズと UI トーンの統一を実施中
- Spring Security 導入と標準ログイン画面の起動確認を実施済み
- ログイン中ユーザー情報取得の流れを整理中
- Lombok 依存を減らし、明示 getter / setter への移行を進行中

## 今後の予定
- 検索系メソッドの完成
- DTO と Service シグネチャの整合
- Notification Entity / Service 実装
- MyPage DTO 実装
- Validation と例外ハンドリングの整備
- `loginId` を起点にしたユーザー取得処理の整理

## 更新履歴
| Version | Date | 内容 |
| --- | --- | --- |
| 01.09.00 | 2026-04-10 | Spring Security 導入確認、Lombok 依存見直し、認証情報取得方針を追記 |
| 01.08.01 | 2026-04-08 | README の文字化け解消、マイページ / 通知 / manual-form 方針を再整理 |
| 01.08.00 | 2026-04-08 | 通知機能、マイページ、ホーム通知バッヂ、ID 命名方針を整理 |
| 01.07.00 | 2026-04-06 | DTO 対応、Validation / Mapper / History 分離の設計整理 |
| 01.06.00 | 2026-04-05 | 画面モック整理、別タブ運用、入力画面統合の前提整理 |
