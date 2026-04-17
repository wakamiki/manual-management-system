# 01_project-overview-and-basic-design.md

Version: 01.03.04
更新日: 2026-04-17

---

## 1. プロジェクト概要

### 1-1. システム名
業務マニュアル管理システム
Manual Management System

### 1-2. 目的
現場で日々更新される業務マニュアルを一元管理し、承認フロー、履歴管理、通知確認まで含めて運用できるようにする。

### 1-3. 想定利用者
- USER(一般利用者)
- APPROVER（マニュアル承認者）
- ADMIN（管理者）
- GUEST（閲覧専用）

### 1-4. 解決したい課題
- 業務知識の属人化
- 最新版マニュアルの把握しにくさ
- 変更履歴の追跡しづらさ
- 承認フローの運用負荷
- 通知の見逃し
- マニュアル保管場所の分かりにくさ

---

## 2. システムコンセプト
- 現場業務で継続利用できる業務システム
- 承認フローと履歴管理を備えたマニュアル管理
- 説明しやすく、保守しやすい設計
- 新しい情報が常に反映され抜けのないマニュアル作成

---

## 3. 主要機能

### 3-1. マニュアル機能
- 一覧表示
- 詳細表示
- 入力画面
- 新規作成
- 編集
- 更新
- 申請
- 承認
- 差し戻し
- アーカイブ
- 復帰
- 複製

### 3-2. 管理機能
- ユーザー管理
- カテゴリ管理

### 3-3. 個人機能
- 通知確認
- マイページ
- 自分作成のマニュアル確認
- 承認待ちマニュアル確認
- 閲覧専用ログイン（GUEST）

---

## 4. 主要データ
- users
- manuals
- manual_histories
- categories
- notifications

---

## 5. 画面方針
- index画面は検索と状態確認の親画面とする
- 詳細画面は別タブで開く
- 新規作成画面は専用画面とする
- 編集 / 複製は共通入力画面 `manual-editer` を使う
- 管理画面は別タブで開く
- マイページは個人向けダッシュボードとして扱う

---

## 6. 技術構成
- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security
- Thymeleaf
- Bootstrap
- H2 Database
- Git / GitHub

### 6-1. 開発環境補足
- 開発DBは H2 file DB を使用
- 接続先: `jdbc:h2:file:./data/testdb`
- H2コンソール: `/h2-console`
- 本番移行候補: PostgreSQL
- SecurityConfig を導入し、開発時の H2 コンソールアクセスを許可

---

## 7. 補足
- 詳細仕様は `docs/02_system-specification-and-detailed-design.md` を参照
- 画面仕様は `docs/03_screen-design.md` を参照
- API 設計は `docs/04_api-design.md` を参照
