# 業務記録（2026/03/29）

## 作業概要
Spring Boot を用いた業務マニュアル管理システムの開発を開始し、バックエンドの基本構成を整備した。
開発環境構築から Git / GitHub 連携、初期API実装までを実施し、今後の機能追加に向けた土台を作成した。

---

## 作業内容
- Spring Boot 開発環境の構築
- Git / GitHub 連携設定
- HomeController 作成
- Manual Entity 作成
- ManualRepository 作成
- ManualController 作成
- 登録機能実装 `POST /manuals`
- 一覧取得機能実装 `GET /manuals`

---

## 実装した内容
- マニュアル情報を登録する API の実装
- マニュアル一覧表示用の取得 API の実装
- `Entity / Repository / Controller` の基本構成作成
- Spring Boot における MVC ベースの初期構成整備

---

## 学習・理解したこと
- Spring Boot の基本構成
- Controller / Entity / Repository の役割分担
- CRUD の全体像
- `POST / GET` の基本実装方法
- API エンドポイントの設計の考え方

---

## 課題・次回予定
- 詳細取得機能 `GET /manuals/{id}` の実装
- Service 層の導入による責務分離
- `Optional` / `findById` の理解を深める
- 例外発生時のレスポンス設計の検討

---

## 進捗メモ
- 工程：バックエンド基盤構築
- 状況：初期API実装完了
- 次工程：CRUD機能拡張（詳細取得・更新・削除）

# 業務記録（2026/03/30）

## 作業概要
バックエンドの CRUD 機能拡張として、詳細取得・更新機能を実装した。
あわせて Service 層を導入し、Controller から業務ロジックを分離する構成へ整理した。
Postman を用いた API 動作確認も実施し、実装内容の検証を行った。

---

## 作業内容
- 詳細取得機能実装 `GET /manuals/{id}`
- Service 層作成 `ManualService`
- `Optional` を用いた存在確認処理実装
- 存在しない ID 指定時の 404 エラー対応
- Lombok 導入（getter / setter 自動生成）
- 更新機能実装 `PUT /manuals/{id}`
- `updatedAt` 自動更新処理追加
- Postman 導入・API 動作確認
- GitHub へ push

---

## 実装した内容

### 詳細取得機能
- 指定 ID による 1 件取得
- 存在しない場合は 404 を返却

### 更新機能
- `title` 更新
- `content` 更新
- `status` 更新
- `updatedAt` 自動更新
- `save()` による DB 反映

### 構成改善
- `ManualService` を追加し、業務ロジックを Service 層へ移行
- Controller はリクエスト受付とレスポンス返却に責務を限定

---

## 動作確認
- 存在する ID で更新成功
- 更新後に `GET` で反映確認
- 存在しない ID で 404 エラー確認
- Postman にて正常系 / 異常系の基本確認完了

---

## 学習・理解したこと
- `Optional` の使い方
- `isPresent()` / `get()` の役割
- getter / setter の役割
- Lombok の利用方法
- Service / Controller の責務分離
- Postman による API テスト手順
- `updatedAt` 自動更新の設計意図

---

## 課題・次回予定
- 削除機能 `DELETE /manuals/{id}` の実装
- POST 処理を Service 経由へ整理
- コード構成整理
- 例外処理の共通化検討

---

## 進捗メモ
- 工程：CRUD機能拡張
- 状況：詳細取得・更新機能完了
- 次工程：削除機能実装 / Service層整理


# 業務記録（2026/03/31）

## 作業概要
業務マニュアル管理システムのバックエンド機能を大きく拡張し、CRUD・検索・ステータス管理の主要機能を一通り完成させた。  
検索APIの追加、Service経由への構成統一、承認日時制御の確認を実施し、第一提出版に近い完成度まで到達した。

---

## 作業内容

### 1. status検索機能実装
**実装箇所**
- `repository / ManualRepository`
- `service / ManualService`
- `controller / ManualController`

**実装内容**
- `findByStatus(ManualStatus status)`
- `searchByStatus()`
- `GET /manuals/status?status=DRAFT`

---

### 2. 一覧取得処理の構成見直し
**対応内容**
- Controller から Repository 直呼びを見直し
- Service 経由に統一
- 依存注入（DI）修正

**対応内容詳細**
- `ManualController` にコンストラクタ注入追加
- `manualService` の `null` による 500 エラー解消

---

### 3. Entity改善
**対応内容**
- `Manual.status` に enum文字列保存設定を追加

```java
@Enumerated(EnumType.STRING)
```

---

### 4. 動作確認（Postman）
以下 API の動作確認を完了

#### CRUD
- `POST /manuals`
- `GET /manuals`
- `GET /manuals/{id}`
- `PUT /manuals/{id}`
- `DELETE /manuals/{id}`

#### 検索
- `GET /manuals/search?keyword=xxx`
- `GET /manuals/status?status=DRAFT`

---

### 5. ステータス遷移確認
以下ステータスの確認完了
- `DRAFT`
- `PENDING`
- `APPROVED`
- `ARCHIVED`

---

### 6. 承認日時制御確認
仕様どおりの動作を確認

- `DRAFT` → `approvedAt = null`
- `PENDING` → `approvedAt = null`
- `APPROVED` → 日時自動設定
- `ARCHIVED` → 承認履歴保持

---

### 7. ドキュメント整備
- README 更新内容整理
- system-specification 更新内容整理
- version 管理ルール確定
- 現在 version `01.03.00`

---

### 8. GitHub作業
本日分コミット完了

**commit message**
```text
feat: implement CRUD, search APIs, and approvedAt status control
```

---

## 学習・理解したこと
- Service 層経由に統一する設計の重要性
- DI（依存注入）によるクラス連携
- enum の DB 保存方法
- ステータス管理と日時制御の設計
- 実装後の API 動作確認フロー

---

## 所感
CRUD と検索機能、承認日時制御まで一通り完成し、  
**第一提出版に近い状態まで到達**。

ポートフォリオとして業務システムらしい構成が見えてきた。

---

## 課題・次回予定
- README / 仕様書反映
- 検索結果並び順改善
- バリデーション追加
- カテゴリ機能設計着手

---

## 進捗メモ
- 工程：バックエンド主要機能実装
- 状況：CRUD + 検索 + ステータス管理完了
- 次工程：カテゴリ機能 / バリデーション強化

# 作業記録（2026/04/02）

## 作業概要
システム全体の仕様・UI / UX・実装ロードマップ・設計資料を大きく前進させた日。
実装前に必要な要件定義・基本設計・詳細設計の骨組みをほぼ完成させ、実装フェーズへ本格的に入れる状態まで整理した。

---

## 実施内容

### 1. システム仕様の大枠確定
マニュアル管理システムの主要仕様をほぼ確定。

#### Manual API
- 更新
- 複製
- アーカイブ
- 復帰
- 申請
- 承認

#### 状態遷移ルール
- `DRAFT`
- `PENDING`
- `APPROVED`
- `ARCHIVED`

#### その他
- 条件付き復帰ルール確定
- 画面遷移ルール確定

---

### 2. UI / UX仕様の大量決定
スマホ環境で仕様検討を進め、画面設計を大きく前進。

#### 決定した主な画面
- トップ画面
- 一覧カード
- 詳細画面
- 新規作成画面
- 確認画面
- ユーザー管理画面
- カテゴリ管理画面

#### 決定した機能
- ヘルプモーダル
- FAQ
- ステータス説明
- 承認フロー説明
- 初回ガイド
- disabled理由表示
- エラー画面
- 空状態画面
- 通知センター
- 管理者ダッシュボード
- PDF / CSV出力
- 監査ログ
- 運用ヘルプ

業務システム寄りの UI / UX に仕上げる方向性を明確化。

---

### 3. バックエンド作業順の確定
開発ロードマップを決定。

#### 優先順
1. ログイン / 権限
2. 一覧 + 詳細
3. 新規作成 + 更新
4. 申請 + 承認 + 差し戻し
5. カテゴリ管理
6. 通知 / ログ
7. 帳票
8. ダッシュボード
9. テスト

---

### 4. バリデーション方針整理
フロント / バック共通で利用するルールを整理。

- title 必須
- title 最大 50 文字
- content 最大 10000 文字
- category 必須
- changeNote 条件付き必須

履歴作成時の `changeNote` 必須ルールまで整理。

---

### 5. セキュリティ設計見直し
Entity 設計の考え方を整理。

#### 決定事項
- `@Getter` はクラス単位
- `@Setter` は必要項目のみ
- 状態変更系は専用メソッド化

#### 注意点確認
- 権限チェック
- 状態遷移チェック
- DTO経由更新
- password の扱い

---

### 6. 学習方針の変更
今後は **フロントエンド学習優先** に変更。

#### 方針
- HTTP完成コードは出さない
- ベタ打ちコードは避ける
- 実装方針中心
- 触るファイル明示
- 考えるポイント整理
- ヒント中心

自力実装力を伸ばす方針へ変更。

---

### 7. 設計資料作成
- 要件定義書 作成
- 基本設計書 作成
- 詳細設計書 作成
- APIごとの入出力一覧 作成
- 画面ごとの表示項目一覧 作成
- 画面ごとの入力項目一覧 作成
- Entityクラス作成タスクリスト 作成
- Serviceクラス メソッド一覧 作成
- Controllerクラス メソッド一覧 作成
- Spring Boot 実装順一覧 作成

---

### 8. 面接説明資料作成
- 1枚目：システム構成図 / 技術スタック
- 2枚目：ER図 / DB設計
- 3枚目：状態遷移 / 承認フロー図
- 4枚目：画面構成 / UI設計
- 5枚目：工夫点 / 技術選定理由
- 6枚目：今後の拡張予定 / 学び

---

### 9. 仕様・機能検討

#### 今後の拡張機能
- 正式版マニュアル作成支援機能
- お気に入り機能
- 関連マニュアル表示機能
  - 管理者手動関連付け
  - 複製時関連付け引き継ぎ
- 確認チェック機能
  - ユーザーごとの確認状態保持
  - 更新後再確認
- コメント機能
- タグ機能

#### 要件追加
背景・課題に以下を追加
- 言った・聞いていない問題
- 共有漏れ防止
- 更新内容の周知課題

---

## 決定事項
- 関連マニュアル機能は手動関連付け方式を採用
- 複製時に関連情報も引き継ぐ
- 確認チェックはユーザー単位で保持
- タグ機能を将来拡張に追加
- コメント機能を将来拡張に追加

---

## 成果物
- docs 資料一式の骨組み完成
- 面接用スライド構成完成
- 実装前設計資料ほぼ完成

---

## 進捗評価
かなり大きく進んだ日。

# 業務記録（2026/04/04）

## 作業概要
Entity整備・設計資料統合・フロントエンドモック作成を並行して進めた日。  
バックエンドでは Entity の責務整理と複製機能設計を進め、フロントエンドでは画面モックと Bootstrap レイアウトの基礎学習を実施した。  
また、設計資料の統合作業を完了し、実装フェーズへスムーズに移行できる状態を整えた。

---

## 実施内容

### 1. Entity整備
- createdAt
- updatedAt
- approvedAt

専用メソッド方針
- markCreatedNow()
- markUpdatedNow()
- markApprovedNow()
- clearApprovedAt()

---

### 2. Service層整理
フィールド直接代入を見直し

修正方針
- Entity 自身に処理を持たせる

例
category.markCreatedNow();

---

### 3. ManualHistory Repository 設計
- findByManualIdOrderByChangedAtDesc
- findAllByOrderByChangedAtDesc

---

### 4. Manual複製機能設計
コピー対象
- title
- content

コピーしない項目
- id
- approvedAt
- createdAt
- updatedAt

新規設定
- status = DRAFT
- category
- createdByUser
- changeNote

---

### 5. User Entity整理
- userId → ログイン用ID
- displayName → 表示用氏名

---

### 6. docs資料統合
統合完了
- 01_project-overview-and-basic-design.md
- 03_screen-design.md
- 04_api-design.md
- 05_db-design.md
- 06_test-specification.md

---

### 7. フロントエンドモック
作成 / 調整
- トップ画面
- ログイン画面
- 詳細画面
- 新規作成
- 編集画面
- 管理画面

---

### 8. Bootstrap学習
学習内容
- row
- col
- d-flex
- justify-content-center
- align-items-center

学んだこと
中央寄せは親箱で制御する

---

## 所感
Entity責務の整理と docs 統合が非常に大きな進捗。  
実装フェーズへかなりスムーズに移行できる状態になった。

---

## 次回予定
- copyManual() 実装
- Controller追加
- Postman確認

# 業務記録（2026/04/05）

## 作業概要
本日はフロントエンドモック画面の主要UI整備、画面遷移方針の見直し、別タブ運用ポリシーの整理、バックエンド複製機能の設計準備を中心に進めた。

トップ画面を基準画面として完成度を高めたことで、その後の画面作成速度が大きく向上した。
また、業務利用シーンをシミュレーションした結果、詳細画面・編集画面・管理画面を別タブで開く設計が実運用に適していると判断した。

---

## 実施内容

### フロントエンドモック整備
- `index.html` をトップ画面として整備
- 左サイドバー + 右アコーディオン一覧レイアウトに調整
- 使用停止カテゴリの開閉 UI を追加
- ページネーションを追加
- 全体配色を `#56969D` ベースに統一
- `index` 画面を一旦確定

### 個別画面調整
- `login.html`
- `manual-create.html`
- `manual-edit.html`
- `manual-detail.html`

配色・メッセージ表示欄・ボタン列を `index` 基準に統一した。

### 管理画面追加
- `user-management.html` 作成
- `category-management.html` 作成
- `admin.css` 追加

### 画面遷移設計
- 詳細画面は比較・参照用途のため別タブ運用
- 新規作成 / 編集 / 複製 / 管理画面も別タブ運用
- 終了導線は「戻る」ではなく「閉じる」を採用
- `target="_blank"` を追加
- `window.close()` を利用する方針を整理

### 設計資料更新
- `docs/02_system-specification-and-detailed-design.md.md` 更新
- `docs/03_screen-design.md` 更新
- `README.md` 更新
- 別タブ運用ポリシーを明文化

### バックエンド準備
- `Manual` 複製機能の実装タスク整理
- DTO の役割整理
- `ManualCopyRequestDto.java` 着手
- `status` は DTO でも enum 型で保持可能と確認

---

## 気づき・学び

### 基準画面の重要性
最初のモック画面 1 つ目の作成に約 12 時間かかったが、レイアウト・配色・部品ルールが固まったことで、その後の画面作成は非常にスムーズに進んだ。

基準になる画面を最初にしっかり作ることの重要性を実感した。

### 業務利用視点でのUI設計
実際の利用シーンをシミュレーションした結果、マニュアルを複数比較・参照しながら業務を進めるためには別タブ運用が最適と判断した。

画面設計は見た目だけでなく、実務利用フローを前提に考える重要性を学んだ。

### DTO の役割理解
DTO は Entity をそのまま受け渡ししないための箱であり、外から受け取る値を絞りつつ、業務上重要な値は Service / Entity 側で制御するという考え方を理解できた。

---

## 更新した主なファイル
- `README.md`
- `docs/02_system-specification-and-detailed-design.md.md`
- `docs/03_screen-design.md`
- `src/main/resources/static/mock/index.html`
- `src/main/resources/static/mock/manual-detail.html`
- `src/main/resources/static/mock/manual-create.html`
- `src/main/resources/static/mock/manual-edit.html`
- `src/main/resources/static/mock/login.html`
- `src/main/resources/static/mock/user-management.html`
- `src/main/resources/static/mock/category-management.html`
- `src/main/resources/static/css/common.css`
- `src/main/resources/static/css/index.css`
- `src/main/resources/static/css/login.css`
- `src/main/resources/static/css/admin.css`
- `src/main/java/com/example/manual/dto/ManualCopyRequestDto.java`

---

## 次回着手予定
- DTO対応
- `copyManual()` Service 実装
- Controller エンドポイント追加
- Postman 動作確認
- 認証 / 権限制御着手

# 作業記録（2026/04/06）

## 作業概要
本日はバックエンド設計整理と学習理解の深化を中心に進めた。  
Codex 側では DTO 対応の再開、命名整理、状態遷移・権限・入力検証方針の整理を行い、実装前の設計判断を固めた。  
また、自身の理解面では MVC / DTO / Model / オブジェクト指向 / Mapper の概念整理が大きく進んだ。

---

## 実施内容

### 1. DTO対応作業の再開
前日中断していた DTO 対応を再開した。

- id の扱い（採番元・DTOとの関係）を整理
- Entity 主キーは DB の IDENTITY 採番であることを確認
- Request DTO / Response DTO / Model の役割を整理
- Controller → Service → Entity → Response DTO の流れを再確認

---

### 2. 命名整理の検討（id と userId）
id（主キー）と userId（業務識別子）が紛らわしいため、命名整理の必要性を確認した。

- userId は loginId 等へ改名する方針を推奨
- `findByUserId(Long id)` のような命名と実装のズレを確認
- Repository / Service / Controller を含めた検索メソッド名の整合性見直しを判断

---

### 3. createManual の責務分離方針整理
「ボタン分岐で状態を決める」よりも、ユースケース別に責務を分ける方針を整理した。

方針案
- `createDraftManual()`
- `createAndSubmitManual()`

処理内容が明確に分かる命名へ分割する方向で整理した。

---

### 4. ステータス定義・遷移ルール再確認
承認状態を再確認した。

- DRAFT
- PENDING
- APPROVED
- ARCHIVED

あわせて、既存の遷移メソッドと例外メッセージ方針をそろえる方向を確認した。

---

### 5. 例外メッセージ方針整理
例外文言は

「許可状態」+「現在状態」

を含める形式が運用しやすいと整理した。

例
- 下書き状態のマニュアルのみ申請できます。現在の状態: {status}

---

### 6. 権限・入力検証整理
以下の観点を整理した。

権限
- isActive
- ロール（USER / APPROVER / ADMIN）
- 自己承認禁止
- 状態 × 権限整合

入力検証
- DTO + `@Valid`（Bean Validation）で簡潔に実装する方針を確認

対象
- title
- content
- categoryId
- changeNote

---

### 7. Manual と ManualHistory の責務整理
Manual と ManualHistory は分離維持が望ましいと整理した。

- 本体 = 最新状態
- 履歴 = append-only

また、ManualHistoryService は単独利用ではなく

`ManualService → ManualHistoryService`

の呼び出し関係で利用する理解を整理した。

---

### 8. フロントエンド / Thymeleaf 学習
以下を学習した。

- Bootstrap レイアウト基礎
- div
- row / col
- CodePen でモック作成
- HTML / Bootstrap / CSS の役割整理
- Thymeleaf 化の概念理解

---

### 9. MVC / DTO / Model 理解整理
理解したこと

`Controller → Model → Thymeleaf → 画面表示`

- Model = 画面表示用の箱
- Request DTO = 入力値受け取り
- Response DTO = API返却
- Model は Thymeleaf へ渡す箱

---

### 10. オブジェクト指向理解の深化
処理を部品単位ではなく、画面操作単位で流れとして考えられるようになった。

例
- 承認ボタン押下後の流れ
- 作成ボタン押下後の流れ

`画面 → Controller → Service → Entity → 結果返却 → 画面表示`

---

### 11. 設計思考の変化
これまでは

- 日時を更新する
- 新規インスタンスを作る
- ステータスを変更する

といった小さい粒度で考えていた。

本日は

- 画面の承認ボタン押下時の挙動
- 作成ボタン押下時の挙動

という大きい粒度（ユースケース単位）で考えられるようになった。

---

### 12. Mapper 概念学習
Mapper は

DTO ⇄ Entity の変換を行う橋渡し役

として理解を進めた。

役割整理
- Service = 業務ルール
- Mapper = データ変換

---

### 13. コードレビュー・問題整理
バックエンド実装に着手する前提で、ManualController / ManualService / ManualHistoryService の責務と整合性を確認した。

- Controller に残す処理と Service に寄せる処理を整理
- DTO と Entity が混在している箇所を洗い出した
- `Optional<Manual>` と `Manual` の使い分け不整合を確認
- 履歴作成メソッドの戻り値と責務のズレを確認
- `ManualHistoryService` は `void createHistory(...)` を基本にする方針を整理

---

## 本日の成果
本日は実装よりも、設計・理解・責務整理が大きく進んだ日となった。

特に以下の理解が深まった。

- DTO
- Model
- Service責務
- History分離
- オブジェクト指向
- Mapper
- ユースケース思考

---

## 更新した主なファイル
- `docs/07_development-process-memo.md`
- `README.md`

---

## 次回着手予定
- DTO対応
- 命名修正
- `@Valid` バリデーション実装
- create 系メソッド分割
- Mapper 導入検討
- `Manual` 複製機能の本実装再開
