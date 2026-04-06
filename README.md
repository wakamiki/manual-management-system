# Manual Management System

Version: 01.06.00
最終更新日: 2026-04-05

## 概要

日々変化する現場業務に対応するための **業務マニュアル管理システム** です。

正式な冊子マニュアルとは別に、現場で発生する運用変更や補足事項を迅速に共有し、
業務知識の属人化防止・履歴管理・承認状態の可視化を目的としています。

---

## 更新履歴

### 2026-04-05

* フロントエンド実装方針を整理
* モック画面の配置先を `src/main/resources/static/mock` に統一
* HTML + Bootstrap ベースでトップ / 詳細 / 新規作成 / 編集 / ログインのモック作成方針を明確化
* PC業務利用前提のUI方針を整理
* トップ画面を左サイドバー + 右アコーディオン一覧で設計する方針を反映
* 新規作成 / 複製 / 編集を入力専用の独立画面として扱う方針を反映
* 詳細画面を比較参照用の別タブ画面として扱う方針を反映
* 別タブ画面の終了導線を `戻る` ではなく `閉じる` に統一する方針を反映

### 2026-04-04

* 各種資料追加・変更
* Entity整備継続（Manual / User / Category）
* 時間系フィールドを専用メソッド方式へ整理
  * createdAt
  * updatedAt
  * approvedAt
* ManualHistory の履歴取得Repositoryメソッド追加方針確定
  * manual単位履歴取得（更新日時降順）
  * 全件履歴取得（更新日時降順）
* Manual複製機能の業務ルール設計開始
* User.displayName の責務整理

### 2026-04-01

* Category管理機能追加（一覧取得 / 登録）
* Manual と Category の関連付け実装（ManyToOne）
* Manual登録時のカテゴリ存在チェック追加
* トップ画面レイアウトラフ作成
* 左カテゴリナビ / 使用停止カテゴリUI方針確定

### 2026-03-31

* CRUD機能完成
* タイトル検索 / ステータス検索追加
* approvedAt 自動制御実装
* 全API動作確認完了

---

## 主な機能

* マニュアル登録
* マニュアル一覧表示
* マニュアル詳細表示
* カテゴリ分け（部署単位）
* カテゴリ内検索
* 更新履歴
* 承認状態管理
* 旧版アーカイブ管理
* タイトル検索
* ステータス検索
* 承認日時自動制御

---

## ステータス管理

* DRAFT（作成中）
* PENDING（承認前）
* APPROVED（正式版）
* ARCHIVED（旧版）

---

## 使用技術

* Java 17
* Spring Boot
* Spring Data JPA
* H2 Database
* Git / GitHub

---

## フロントエンド方針

* 現在のモック画面は Thymeleaf を考慮せず、HTML + Bootstrap + CSS で作成する
* 画面モックは `src/main/resources/static/mock` 配下に配置する
* CSS は `src/main/resources/static/css` 配下に配置する
* PC 業務利用を前提に、別画面と並べても見やすいコンパクトなUIを優先する
* 一覧・参照画面と入力画面を分離し、役割を明確にする
* トップ画面を親画面とし、詳細 / 新規作成 / 複製 / 編集 / 管理画面は必要に応じて別タブで開く
* 別タブ画面の終了導線は `戻る` ではなく `閉じる` を基本とする

### モック画面の役割

* トップ画面: 探す・絞り込むための親画面
* 詳細画面: 検索中に複数マニュアルを突き合わせて参照するための別タブ画面
* 新規作成 / 複製 / 編集画面: 入力するための別タブ画面
* ユーザー管理 / カテゴリ管理画面: 管理操作を行うための別タブ画面

### トップ画面UI方針

* 左サイドバー + 右メインエリア構成
* 左側にカテゴリ一覧、右側にマニュアル一覧を配置
* マニュアル一覧はカード大表示ではなく、タイトル中心の縦一覧を採用
* 各行はアコーディオン形式で必要時のみ詳細を展開する
* 派手さより一覧性・視認性・業務利用時の使いやすさを優先する

### 別タブ運用方針

* `詳細を見る` は詳細画面を別タブで開く
* 詳細画面は比較参照用のため `閉じる` 導線を持つ
* `新規作成` / `複製` / `編集` は入力専用の独立タブとして扱う
* 管理画面もトップ画面を閉じずに参照・更新できるよう別タブで開く
* モック段階では `a` タグ + `target="_blank"` で別タブ遷移を表現してよい

---

## 現在の開発状況

* HTML + Bootstrap ベースのモック画面作成
  * `src/main/resources/static/mock/login.html`
  * `src/main/resources/static/mock/index.html`
  * `src/main/resources/static/mock/manual-detail.html`
  * `src/main/resources/static/mock/manual-create.html`
  * `src/main/resources/static/mock/manual-edit.html`
  * `src/main/resources/static/mock/user-management.html`
  * `src/main/resources/static/mock/category-management.html`
* トップ画面UI方針整理
  * 左カテゴリサイドバー
  * 右アコーディオン一覧
  * 小さめウィンドウでも見やすいPC業務向けUI
* 入力系画面の独立方針整理
  * 新規作成 / 複製 / 編集は別タブ前提
  * 一覧・詳細を閉じずに参照しながら入力可能
* 詳細画面の別タブ運用方針整理
  * 検索しながら複数マニュアルを比較参照可能
  * `閉じる` ボタンで参照タブを終了する前提
* 管理画面モック作成
  * ユーザー管理画面
  * カテゴリ管理画面
* バックエンド複製機能の設計準備
  * `ManualCopyRequestDto` 着手
  * DTO と Entity の役割整理
* バックエンド設計整理
  * `createDraftManual()` / `createAndSubmitManual()` の責務分離方針整理
  * DTO / Model / Mapper の役割整理
  * `Manual` / `ManualHistory` の責務分離確認
  * `@Valid` による入力検証方針整理
* ログイン画面UIモック作成
* Bootstrapレイアウト学習
  * row / col
  * d-flex
  * justify-content-center
  * align-items-center
* フォームUI基礎学習
  * form
  * label
  * input
  * button

### 実装済み

* Spring Boot環境構築
* Git / GitHub連携
* マニュアル登録機能（POST /manuals）
* 一覧取得機能（GET /manuals）
* 詳細取得機能（GET /manuals/{id}）
* 更新機能（PUT /manuals/{id}）
* 削除機能（DELETE /manuals/{id}）
* タイトル検索機能（GET /manuals/search?keyword=xxx）
* ステータス検索機能（GET /manuals/status?status=DRAFT）
* 更新日時降順表示
* ステータス管理機能
* 承認日時自動制御（approvedAt）
* カテゴリ一覧取得機能（GET /categories）
* カテゴリ登録機能（POST /categories）
* マニュアルとカテゴリの関連付け（ManyToOne）
* カテゴリ存在チェック機能
* トップ画面UIラフ設計

---

## 今後追加予定

* カテゴリ停止 / 再有効化機能
* 使用停止カテゴリ折りたたみUI
* 更新日時表示
* 版管理
* DTO対応
* 命名修正
* `@Valid` バリデーション実装
* create 系メソッド分割
* Mapper 導入検討
* 認証機能
* Web公開

---

## 設計方針

* 現場で日々変化する業務に対応
* 未承認でも閲覧可能
* 承認履歴を保持
* 更新日時による情報鮮度の可視化
* 旧版をアーカイブして参照可能
* DRAFT / PENDING では approvedAt は null
* APPROVED 時に approvedAt を自動設定
* ARCHIVED 時は承認履歴を保持

---

## 状態

開発中

## 改版履歴

| Version | 日付 | 更新内容 |
|---|---|---|
| 01.01.00 | 2026-03-29 | 初版作成、Spring Boot環境構築、Entity / Repository / Controller作成 |
| 01.02.00 | 2026-03-30 | status設計、承認フロー仕様策定 |
| 01.03.00 | 2026-03-31 | CRUD完成、タイトル検索・status検索追加、approvedAt自動制御、全API動作確認完了 |
| 01.04.00 | 2026-04-04 | 資料整備、資料統合・分離、Entity整備、履歴検索Repository設計、トップ画面UIモック・ログイン画面モック作成、Bootstrapレイアウト学習 |
| 01.05.00 | 2026-04-05 | フロントエンド実装方針整理、モック配置を `static/mock` に統一、PC業務向けUI方針を追記 |
| 01.06.00 | 2026-04-05 | 別タブ運用方針、詳細画面の比較参照方針、`閉じる` 導線、管理画面モック追加を反映 |
| 01.07.00 | 2026-04-06 | DTO対応再開、create系責務分離方針、Validation / Mapper / History分離の設計整理を追記 |
