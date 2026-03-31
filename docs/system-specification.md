# 業務マニュアル管理システム 仕様書

Version: 01.03.00 
更新日: 2026-03-31  
更新内容: CRUD機能・検索機能・承認日時制御仕様確定

## 1. システム概要

本システムは、日々変化する現場業務に対応するための **業務ナレッジ共有・管理システム** です。

正式な冊子マニュアルとは別に、現場で発生する運用変更や補足事項を迅速に共有できることを目的としています。

以下の課題解決を目的として設計しています。

* 業務知識の属人化防止
* 最新情報の迅速な共有
* 承認状態の可視化
* 旧版履歴の保持
* 「言った / 言わない」問題の防止

---

## 2. 想定利用シーン

* 部署内での業務手順共有
* 新任担当者への引き継ぎ
* 手順変更時の暫定共有
* 過去運用の参照
* 正式承認前の情報確認

---

## 3. システム構成

* Java 17
* Spring Boot
* Spring Web
* Spring Data JPA
* H2 Database
* Git / GitHub
* VS Code

---

## 4. データ構造

### 4.1 Category（部署カテゴリ）

カテゴリは部署単位で管理します。

初期カテゴリは以下を想定しています。

* 営業部
* 総務部
* 製造部
* 経理部

カテゴリは DB で管理し、追加は管理者のみ可能とします。

---

### 4.2 Manual（マニュアル）

各カテゴリ内に複数のマニュアルを保持します。

| 項目名        | 内容      |
| ---------- | ------- |
| id         | マニュアルID |
| title      | テーマタイトル |
| content    | 本文      |
| status     | 状態      |
| category   | 所属カテゴリ  |
| createdAt  | 作成日時    |
| updatedAt  | 更新日時    |
| approvedAt | 承認日時    |

---

## 5. マニュアル構造

本システムは以下の構造で管理します。

```text
カテゴリ（部署）
 ├ マニュアルタイトル
 ├ マニュアルタイトル
 └ マニュアルタイトル
```

例：

```text
営業部
 ├ 発注業務の流れ
 ├ 仕入先確認方法
 └ 月末締め処理
```

---

## 6. ステータス仕様

### DRAFT

* 作成中
* 作成者のみ閲覧可能

---

### PENDING

* 承認前
* 閲覧可能
* 現場への暫定共有を想定

---

### APPROVED

* 正式版
* 閲覧可能

---

### ARCHIVED

* 旧版
* 一覧上では見えにくくする
* 必要時に参照可能

---

## 7. 状態遷移

通常遷移：

DRAFT → PENDING → APPROVED → ARCHIVED

例外遷移：

PENDING → DRAFT
PENDING → ARCHIVED
ARCHIVED → APPROVED

実装確認済み遷移：

* DRAFT → PENDING
* PENDING → APPROVED
* APPROVED → ARCHIVED
* PENDING → DRAFT
* ARCHIVED → APPROVED

---

### 7.1 状態遷移ルール

#### DRAFT → PENDING

* 作成者のみ実行可能
* title 必須
* content 必須

#### PENDING → APPROVED

* 承認者のみ実行可能
* 作成者本人は承認不可
* 所属カテゴリがアクティブであること
* approved_at に初回承認日時を保存する

#### APPROVED → ARCHIVED

* 管理者または承認者が実行可能
* 物理削除は行わずアーカイブで管理する

#### PENDING → DRAFT

* 差し戻し時に使用
* 作成者による再編集を想定

#### PENDING → ARCHIVED

* 不要となった承認待ちマニュアルを保管する場合に使用

#### ARCHIVED → APPROVED

* 管理者または承認者が実行可能
* 同一アクティブカテゴリ内に限り復帰可能
* 誤アーカイブ時の復旧を想定

---

### 7.2 状態別の編集可否

* DRAFT：自由に編集可能
* PENDING：作成者のみ編集可能
* APPROVED：直接編集不可（複製して修正）
* ARCHIVED：編集不可

---

### 7.3 削除方針

本システムでは物理削除は行わない。

* DRAFT：誤登録時も ARCHIVED へ変更
* PENDING：DRAFT に戻す、または ARCHIVED
* APPROVED：必要時 ARCHIVED
* ARCHIVED：保存継続

---



## 8. 承認履歴

承認済み判定は boolean ではなく、承認日時 `approvedAt` で管理します。

これにより、ARCHIVED 後も

* 過去に正式版だったか
* いつ承認されたか

を確認可能にします。

---

### 承認日時制御ルール

* DRAFT：approvedAt = null
* PENDING：approvedAt = null
* APPROVED：承認日時を自動設定
* ARCHIVED：過去の承認日時を保持

---

## 9. 検索仕様

検索はカテゴリ内でタイトル検索を可能とします。

例：
営業部カテゴリ内で「発注」と検索すると

* 発注業務の流れ
* 発注書修正手順

などが検索対象となります。

---

## 10. 設計意図

本システムは、正式なマニュアル管理ではなく、現場で変化に追従する **カジュアルな業務ナレッジ共有システム** として設計しています。

利用者が迷わず閲覧・更新できることを重視し、保守性と現場運用性の両立を目指しています。

---

## 11. API仕様

| メソッド | エンドポイント | 機能 |
|---|---|---|
| POST | /manuals | マニュアル登録 |
| GET | /manuals | 一覧取得 |
| GET | /manuals/{id} | 詳細取得 |
| PUT | /manuals/{id} | 更新 |
| DELETE | /manuals/{id} | 削除 |
| GET | /manuals/search?keyword=xxx | タイトル検索 |
| GET | /manuals/status?status=DRAFT | ステータス検索 |

---

## 12. 詳細仕様（追加確定）

### 12.1 カテゴリ仕様

#### categories テーブル

| 項目名 | 型 | 制約 |
|---|---|---|
| id | BIGINT | 主キー / 必須 |
| category_name | VARCHAR(50) | 必須 / 重複不可 / 空文字不可 |
| created_at | DATETIME | 必須 |
| updated_at | DATETIME | 必須 |
| is_active | BOOLEAN | 必須 / 初期値 true |

#### カテゴリ運用ルール

- category_name は前後空白を除去して保存する
- 非アクティブカテゴリは削除せず保持する
- 画面上では「使用停止カテゴリ」としてグループ表示する
- DB上で親カテゴリは持たず、表示のみでグループ分けする

---

### 12.2 マニュアル仕様

### 12.4 更新・削除仕様

#### 更新権限

* DRAFT：自由に編集可能
* PENDING：作成者のみ編集可能
* PENDINGで編集後も status は PENDING を維持
* APPROVED：直接編集不可（複製して修正）
* ARCHIVED：編集不可

#### 削除方針

本システムでは物理削除は行わない。

* DRAFT：誤登録時も ARCHIVED へ変更
* PENDING：DRAFT に戻す、または ARCHIVED
* APPROVED：必要時 ARCHIVED
* ARCHIVED：保存継続し、必要時 APPROVED に復帰可能

---

### 12.5 複製仕様

* 他部署への移動は複製扱い
* 元データは ARCHIVED に変更
* 複製先は常に DRAFT
* 承認状態は引き継がない
* approved_at は null
* created_at / updated_at は新規作成
* title はそのまま引き継ぐ
* content はそのまま引き継ぐ
* category_id は指定アクティブカテゴリ
* 複製者を新しい作成者として扱う

---

### 12.6 変更履歴仕様

#### manual_histories テーブル

| 項目名         | 型            | 制約        |
| ----------- | ------------ | --------- |
| id          | BIGINT       | 主キー / 必須  |
| manual_id   | BIGINT       | 必須 / 外部キー |
| change_note | VARCHAR(100) | 必須        |
| changed_at  | DATETIME     | 必須        |

#### リレーション

manuals 1 : N manual_histories

#### 入力ルール

* 空文字禁止
* 前後空白除去
* 100文字超過不可
* title / content 更新時は必須
* 複製時も必須

#### 履歴対象

* title 更新
* content 更新
* 複製時

※ status変更は履歴対象外

#### 表示順

* 最新履歴を上に表示
* changed_at DESC

---

### 12.7 検索仕様（詳細）

* title の部分一致検索
* 大文字小文字を区別しない
* category と title の複合検索は行わない
* ARCHIVED を含める場合はチェックボックスで指定する

---

### 12.8 画面レイアウト方針

フロントエンドは Bootstrap を採用する

#### トップ画面

* 上部：検索フォーム
* 左側：カテゴリ一覧
* 右側：マニュアル一覧（ブログ風表示）

#### 変更履歴

* マニュアル詳細画面内に表示
* またはクリックで別画面表示


#### manuals テーブル

| 項目名 | 型 | 制約 |
|---|---|---|
| id | BIGINT | 主キー / 必須 |
| category_id | BIGINT | 必須 / 外部キー |
| title | VARCHAR(100) | DRAFT時のみ空可 / それ以外必須 |
| content | TEXT | 最大10000文字 / DRAFT時のみ空可 / それ以外必須 |
| status | ENUM | 必須 |
| created_at | DATETIME | 必須 |
| updated_at | DATETIME | 必須 |
| approved_at | DATETIME | 初回承認時のみ保存 / 未承認時 null |

#### 入力制約

## 改版履歴

| Version | 日付 | 更新内容 |
|---|---|---|
| 01.01.00 | 2026-03-29 | 初版作成、基本構成定義 |
| 01.02.00 | 2026-03-30 | status仕様・状態遷移・承認履歴仕様追加 |
| 01.03.00 | 2026-03-31 | CRUD仕様確定、検索API追加、approvedAt制御ルール確定 |