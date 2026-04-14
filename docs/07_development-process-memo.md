# 07_development-process-memo.md

Version: 01.00.08  
更新日: 2026-04-14

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

#### 画面仕様の再整理
- トップ画面の検索 UI を見直し
  - アーカイブ専用チェックを廃止
  - カテゴリ / ステータスの複数選択フィルタへ変更
- ステータス表示文言を画面用日本語に統一
  - `下書き / 申請中 / 承認済 / アーカイブ`
- トップ画面、詳細画面、入力画面、マイページに `マニュアルID` 表示を追加
- トップ画面の更新履歴表示を詳細画面と同じ箇条書きトーンへ統一
- 管理画面
  - ユーザー一覧の下にページ切り替えを追加
  - カテゴリ一覧の下にページ切り替えを追加

#### manual-form / 詳細画面の整理
- `manual-form` のボタン文言を整理
  - `下書きに保存`
  - `マニュアル公開`
- `manual-form` から複製ボタンを削除
- 編集 / 複製時に表示する補助情報を整理
  - `マニュアルID`
  - `作成日時`
  - `更新日時`
  - `作成者`
  - `履歴日時`
  - `履歴作成者`
  - `ステータス`
- 詳細画面のボタン文言と並び順を整理
  - `編集（作成者専用）`
  - `複製` を編集の隣へ移動
  - `申請` を `マニュアル公開` に変更

#### 仕様・資料の再調整
- `AGENTS.md` を最新ルールに更新
  - `DRAFT → ARCHIVED` を許可遷移へ追加
  - rollback / archive / restore は `manual-form` で `changeNote` 入力後に確定する方針を追加
  - `PENDING → DRAFT` を下書き保存でも許可する前提を追加
- `docs/02` `docs/03` `docs/04` `docs/06` を最新仕様へそろえた
  - 一覧表示項目
  - 詳細画面の日時表示
  - `manual-form` のモードと補助情報
  - rollback / archive / restore の扱い
  - テスト観点

#### 開発環境の復旧
- VS Code の Lombok 設定を見直し
- `java.jdt.ls.lombokSupport.enabled` を有効化して、getter / setter / import 補完の安定化を確認

### 学び
- 通知は機能単体ではなく、通知を見る入口と一緒に設計するとまとまりやすい
- 入力画面は画面統合しても、API はユースケースごとに分けてよい
- ボタンの役割、保存内容、画面遷移を先に言語化すると、後から実装を組み立てやすい
- IDE 設定の不調はコード側の赤エラーだけでなく、言語サーバー設定も確認すると復旧が早い

### 次回着手予定
- ManualControllerとManualServiceの復旧
- Notification Entity / Service の中身実装
- MyPage DTO の項目確定
- コンパイルエラー整理
- DTO と Service シグネチャの整合

---

## 2026-04-09

### 作業概要
Spring Security の学習と起動確認を進め、認証基盤の理解を深めた。あわせて、起動失敗の原因になっていた JPA 関連定義や Controller の重複マッピングを整理し、Lombok の getter 不安定対策として明示 getter を追加した。

### 実施内容

#### Spring Security の理解整理
- `Principal` と `Authentication` の役割を整理
  - `Principal`: ログインユーザー識別向け
  - `Authentication`: 認証状態や権限確認向け
- 認証と認可の違いを整理
  - 認証 = ログイン済みか
  - 認可 = 何ができるか
- セッション / クッキー / セッションID の流れを学習
- ログインユーザー情報は画面からではなく Security から取得する方針を確認

#### Spring Security 導入と起動確認
- `spring-boot-starter-security` を追加
- `.\mvnw spring-boot:run` で起動確認を実施
- 標準ログイン画面が localhost に表示されることを確認
- 認証基盤が動作しているところまで到達

#### 起動失敗の原因調査
- `UserOperationHistory` の関連定義を見直し
  - Entity 関連に `@Column` を使っていた箇所を確認
- `CategoryRepository` / `UserRepository` のメソッド名と Entity 項目名のズレを調査
- `ManualController` の重複 GET マッピングを確認
  - 詳細取得と編集用取得が同じ URL になっていた
- `UserController` の `mypage` 重複マッピングを確認
  - `Principal` 版と `Authentication` 版が同じ URL を持っていた

#### ログインユーザー情報取得の整理
- `Principal` は Controller 引数で受けることを確認
- `principal.getName()` でログイン中の `loginId` を取る流れを整理
- `UserService` では `loginId` から `displayName` を返す役割に整理
- ログイン中ユーザーの表示名取得と、作成者 / 更新者表示の取得経路を分けて理解した

#### Lombok / getter 問題への対応
- Maven compile は通るが VS Code では getter が見つからない状況を確認
- `Java Language Server` 側の認識ズレと判断
- `User` に `getDisplayName()` を明示追加
- その後、Lombok 依存で不安定になりやすいクラスへ明示 getter を追加
  - Entity
  - DTO
  - `ErrorResponse`
- `ManualService` などで getter が赤くなりにくい状態へ調整

#### 命名・設計整理
- `UserService` のメソッドは `loginId` 基準で考える方向に整理
- 画面遷移系メソッド名の整理を継続
  - `goToUserManagementPage()`
  - `goToCategoryManagementPage()`
  - `submitManual()`

### 学び
- Spring Security は、まず「起動してログイン画面が出る」ところまで確認すると理解が進みやすい
- `Principal` は Controller で受けるもの、と割り切ると整理しやすい
- Repository のメソッド名は Entity の実フィールド名と一致していないと起動時に落ちる
- Lombok は Maven 上で通っていても IDE 側で不安定になることがある
- 学習段階では、まず最小動作を確認してから権限や画面連携へ進む方が理解しやすい

### 次回着手予定
- `Principal` を使ったログイン中ユーザーID取得の確認
- `UserRepository` / `UserService` を `loginId` 基準で整理
- `createdByUser` 自動設定の検討
- Security 導入後の Controller / Service の役割整理
- 起動時エラーになりやすい Repository / Mapping の最終確認

---

## 2026-04-10

### 作業概要
Lombok 依存を減らしながら getter / setter の不安定さを解消し、Spring Security の認証情報取得の理解を進めた。あわせて、入力画面 DTO の整理、更新履歴や Category 設定の責務整理、下書き保存処理の分岐設計を進めた。今日はこれまでの進みの遅さが嘘のように作業が進み、設計と実装のつながりがかなり見えてきた。

### 実施内容

#### Lombok 廃止対応と手動メソッド整理
- `@Getter` `@Setter` `@NoArgsConstructor` など Lombok 依存を減らす方針を整理
- Entity / DTO に getter / setter を手動追加する方向で作業
- JPA 用空コンストラクタの必要性を再確認
- `ManualService` などで getter が IDE 上で不安定だったため、明示 getter による安定化を進めた

#### Spring Security 認証情報取得整理
- `Authentication` から取得できる情報を整理
  - `authentication.getName()` でログインID
  - `authentication.getAuthorities()` でロール / 権限一覧
- `Principal` と `Authentication` の使い分けを再確認
  - ログインIDだけなら `Principal`
  - ロールや認証状態も見るなら `Authentication`
- ログインユーザー情報は画面入力ではなく Security から取得する前提を改めて整理

#### Controller / Service / DTO 責務整理
- `ManualController` / `UserController` では `Principal` を引数で受ける方針を整理
- 取り出したログインIDは Service へ渡す形に整理
- ロール判定は Service 中心で保持し、入口の粗い制御は Security 側に寄せる方針を確認
- 一覧 DTO / 詳細 DTO / 状態更新用レスポンス DTO の責務分離を継続する方針を確認

#### 入力画面 DTO と複製処理整理
- 複製専用 DTO は増やさず、組み立てメソッド側で差分吸収する考え方を確認

#### Category / Manual / History の責務整理
- DTO では `categoryId`、Entity では `Category` を持つ流れを確認
  - `categoryId`
  - `CategoryService` で `Category` 取得
  - `manual.setCategory(category)`
- 更新履歴一覧は `List<ManualHistory>` で扱う方針を確認
- `Optional<List<...>>` は使わず、0件時は空 List で返す方針を整理
- 更新履歴更新者は `ManualHistory` 側へ持たせる前提が自然だと整理した

#### 下書き保存処理の分岐設計
- 用途別に入口メソッドを分ける案を整理
  - `saveDraftForCreate()`
  - `saveDraftForDraftEdit()`
  - `saveDraftForPendingEdit()`
  - `saveDraftForCopy()`
- 共通処理は private メソッドへ寄せる方針を確認
  - `applyManualInputValues()`
  - `findCategoryOrThrow()`
  - `saveManual()`
- 複製時のみ `changeNote` 必須という業務ルールを再整理

#### エラー切り分けと設計相談
- `ManualResponseDto` と誤記された `ResponseDto` 参照の整理
- setter / getter の使い分けミスの切り分け
- DTO に対して Entity メソッドを呼んでいないか確認
- `CategoryRepository#findById()` のシグネチャ不整合も再確認
- `Optional` は 1件取得結果だけに使う方針を明確化

#### VS Code 学習
- 開発効率化として次の機能を重点的に確認
  - `F12` 定義へ移動
  - `Shift + F12` 参照検索
  - `Alt + F12` Peek
  - `Ctrl + .` クイック修正
  - `Ctrl + Shift + M` Problems
  - `TODO / FIXME / NOTE`
  - `#region`

### 学び
- Lombok に依存しすぎると IDE の不安定さに引きずられやすい
- `Principal` と `Authentication` は用途で分けると理解しやすい
- `categoryId` と `Category` の責務を分けると Entity と DTO の役割が整理しやすい
- 下書き保存は 1 本で考えるより、用途別に入口を分けたほうが設計しやすい
- 今日は、いままでの進みの遅さが嘘のようにはかどった

### 次回着手予定
- 検索系メソッドの完成
- `UserRepository` / `UserService` の `loginId` 基準整理
- `ManualHistory` の更新者記録設計
- 下書き保存分岐の実装着手

---

## 2026-04-11

### 作業概要
編集/複製の共通入力画面方針と Thymeleaf 学習内容を確定し、検索 UI・状態変更・検索実装の土台を整理した。あわせて、モックHTML/CSSと各種資料の更新を進め、詳細画面の状態変更はインライン入力で確定する方針に統一した。

### 実施内容

#### 編集 / 複製 共通入力画面の設計確定
- `manual-form.html` を編集/複製の共通画面として使用する方針を確定
- `mode = edit / copy` で表示切替する設計を整理
- 新規作成は別画面に分離する方針を確定
- 固定タイトルを **マニュアルエディター** に統一
- 入力欄初期値は元マニュアルの内容をそのまま表示する方針
- Controller / Service は編集保存 / 複製保存の入口を分離

#### Thymeleaf 学習整理
- `th:text` `th:each` `th:if` `th:href` `th:action` `th:field` の用途整理
- 既存の `class` 属性と Thymeleaf 属性の併記が可能と確認
- `th:text` は文字を持つ最内タグに付与する理解を整理
- タグ名と属性名の誤記による構文ミス例を整理

#### 検索 UI と状態変更の整理
- トップ画面のステータス初期値を `DRAFT / PENDING / APPROVED` 選択済みに整理
- 使用停止カテゴリを検索フォーム内のミニ開閉へ移動
- 左サイドバーをカテゴリ一覧中心から `クイックビュー + 補助ナビ` へ再設計
  - `自分の作成分`
  - `申請中`
  - `最近更新`
- ホームアイコン横にログイン中ユーザー名を表示する形へ整理
- 差し戻し / アーカイブ / 復帰は詳細画面内のインライン入力で `changeNote` を入力して確定する方針へ変更
- 承認は確認ダイアログを表示し、必要な場合のみインライン入力を開く方針へ整理
- `manual-detail.html` に共通インライン操作パネルのモックを追加
- 画面上のラベルは `changeNote` ではなく `更新履歴` と表示する方針へ統一

#### 検索実装の土台整理
- `ManualSearchConditionDto` を前提に検索条件を整理
- `ManualRepository` で `Specification` を使う方針を確定
- `ManualSpecification`
  - `containsKeyword(...)`
  - `hasCategoryIds(...)`
  - `hasStatuses(...)`
  の形で条件を分離
- `ManualService.searchManuals(...)` を `Specification + Sort` で検索し、`ManualListDto` へ詰め替える形へ整理

#### index 画面の設計準備
- 検索エリア / カテゴリ一覧 / マニュアル一覧の骨組みを整理
- `main / header / section / form / ul / li / article / span / small` の構成を整理

#### モック / CSS / 資料更新
- `manual-create.html`（新規作成）を別画面として整理
- `manual-form.html`（編集/複製）を共通画面として整理
- 文字カウントのリアルタイム表示を入力欄に付与
- `admin.css` の `.admin-status` 系クラスを復元
- `common.css` に日本語フォントスタックを追加
- 未使用CSSの削除と、mock HTML の復元を実施
- Thymeleaf 反映項目一覧を `docs/07_thymeleaf-binding-items.md` に整理
- `docs/02` `docs/03` `docs/04` `docs/05` `docs/06` `README` の差分更新を継続

#### 資料更新（検索・詳細・DB・テスト）
- `docs/03`
  - トップ画面一覧項目
  - 検索条件
  - 詳細画面ボタン表示
  - インライン入力方針
  を更新
- `docs/04`
  - `ManualSearchConditionDto`
  - `ManualListDto`
  - 一覧検索と状態変更の運用補足
  を追記
- `docs/05`
  - 現在の Entity 実装基準で DB 設計を全面更新
  - `login_id`
  - `operated_by_user_id`
  - `change_user_id`
  などの実カラム名へ反映
- `docs/06`
  - 一覧表示 / アコーディオン展開 / 複数選択検索 / インライン入力のテスト観点を追加
- `README`
  - 詳細画面のインライン入力方針
  - `更新履歴` ラベル
  - DB 設計更新の反映

### 学び
- 検索 UI とサイドバーは、役割を分けると画面全体の説明がしやすくなる
- 入力画面の共通化は UI と保存処理を分離すると説明しやすい
- `Specification` は検索条件の追加に強い
- DB 設計書は docs だけでなく Entity 実装と並べて見ないとズレやすい
- `changeNote` のような内部項目名と、画面表示ラベル `更新履歴` は分けて整理すると説明しやすい

### 次回着手予定
- index 画面の Thymeleaf 化
- 検索 API の Controller 接続
- `ManualDetailDto` と履歴表示の整合
- 起動時エラーになりそうな重複マッピング整理

---

## 2026-04-12

### 作業概要
文字化け・import 全赤の復旧対応と、カテゴリ運用ルールの整理、テンプレート配置・画面操作の微調整を進めた。

### 実施内容
- Java Language Server の `jdt_ws` 削除で import 全赤を復旧
- Java/HTML/MD の UTF-8 統一を実施
- `pom.xml` の `java.version` を 17 に戻し、ビルド成功を確認
- `templates` への HTML コピーと CSS 参照調整
- `templates/css` を配信する ResourceHandler を追加
- index サイドバーのレスポンシブ崩れを修正
- カテゴリ管理の displayOrder placeholder / min 値を調整
- マイページのタブがキーボード入力に反応しないように調整
- 未承認マニュアル欄に更新日を表示
- カテゴリ運用方針を整理
  - 復帰時の displayOrder 最終化は廃止
  - ユーザー作成時の初期状態・必須項目・userId 変更不可を整理
  - パスワードリセットの注意事項・監査ログ項目を整理
  - 同名カテゴリは確認フロー、コード導入方針を整理
  - 停止カテゴリの扱いは保持し、同名上書き時に一括アーカイブ

### 学び
- OneDrive 同期が IDE のワークスペース破損を引き起こすため、停止/除外が有効
- 仕様反映は docs 先行にすることで混乱が減る

### 次回着手予定
- カテゴリ更新ロジックの簡素化（displayOrder 割り込み）
- 同名カテゴリ確認フローのUI設計

---

## 2026-04-13

### 作業概要
マイページ画面の取得方式とタブ表示用データ構造を確定し、差し戻し判定方式・一覧取得の Repository/Service の土台を整理した。あわせて、別タブ更新通知の仕様とコード整形ルールを確定した。

### 実施内容

#### マイページ取得方式の確定
- 初回表示時に必要データを一括取得し、タブ切り替えは画面内で表示切替する方式に決定
- 画面表示 Controller は 1 本化し、再リクエストは行わない
- Bootstrap / JavaScript でタブ表示制御を行う方針

#### マイページ DTO の整理
- マイページ専用 DTO を作成し、タブ表示用の一覧をまとめて保持する方針に決定
- `rollbackManualList` / `createdManualList` / `draftManualList` の 3 つを保持

#### 差し戻し判定方式の整理
- 履歴検索ではなく、`manual` に差し戻しフラグを持たせる方針に決定
- `rollback_flg BOOLEAN NOT NULL DEFAULT FALSE`
- 状態遷移ルール
  - 新規作成時: `false`
  - 差し戻し時: `true`
  - 再申請時: `false`
- getter / setter 名を確定
  - `isRollbackFlg()`
  - `setRollbackFlg(boolean rollbackFlg)`

#### 別タブ更新通知の確定
- 通知キー名: `manualListNeedsRefresh`
- 表示文言: `別タブで更新が行われました。最新状態に更新します。`
- 共通メッセージ領域に表示し、3秒後に現在URLを再読込
- 検索条件・ページ番号を保持

#### Repository / Service の土台実装
- Repository に以下の検索メソッドを追加
  - `findByCreatedByUserOrderByCreatedAtDesc(User createdByUser)`
  - `findByCreatedByUserNotAndStatusOrderByUpdatedAtDesc(User createdByUser, ManualStatus status)`
  - `findByRollbackFlgTrueAndCreatedByUserOrderByUpdatedAtDesc(User createdByUser)`
- `MyPageService` で一覧取得処理を実装開始
  - `List<Manual>` 取得
  - `List<ManualListDto>` への詰め替え準備
- メソッド呼び出し時の型名誤記を修正

#### 学習・整理したこと
- JPA 命名ルール: `True` / `Not` / `OrderBy` / `Desc` の使い方
- 長い代入文・メソッド呼び出しの整形ルールを整理

### 学び
- 画面初回取得で一覧をまとめると、タブ表示が安定する
- 差し戻し判定は履歴よりフラグのほうが説明しやすい
- Repository 命名ルールの理解が実装スピードに直結する

### 仕様追記
- 通知件数は未読のみを対象とする
- 一覧は状態条件で全件表示する
  - 差し戻し一覧: `isRolledBack = true`
  - 承認待ち一覧: `status = PENDING` かつ `createdByUser != current user`
- 通知は一覧表示だけで既読化せず、既読ボタン操作で更新する

### 本日の相談・確認
- Notification の目的と項目の整理
- 通知タイプの命名候補（`ROLLBACK` / `PENDING_APPROVAL`）
- 未読件数は `targetUser + isRead = false` で取得する方針
- 承認待ち通知の対象取得は `APPROVER / ADMIN` を除外条件付きで取得する方針
- 承認完了時に `PENDING_APPROVAL` 通知を全削除する方針を確定

### 次回着手予定
- マイページ DTO への詰め替え完了
- 差し戻しフラグの永続化と状態遷移連携
- タブ表示の Thymeleaf 反映設計

---

## 2026-04-14

### 作業概要
トップ画面クイックビューと検索フォームの仕様を整理し、Controller / バリデーション責務の方針を docs に反映した。

### 実施内容
- クイックビュー「最近更新（直近7日）」の仕様を追記
- 検索フォームの複数選択・選択保持・Thymeleaf 再表示を追記
- Controller 方針（GET/POSTの責務分離）を追記
- バリデーション責務（DTO/Service分担）を明文化
- 件数取得は `countBy...` 系を使う方針を追記
- README に進捗を追記

### 学び
- 仕様は UI と実装指針の両方を揃えると迷いが減る

### 次回着手予定
- クイックビュー件数取得の実装整理
- 検索フォームの Thymeleaf 反映設計

---
# 作業記録（2026/04/14）

## 作業概要
本日はトップ画面（index）の検索フォームとクイックビューを中心に、Thymeleaf と Spring MVC のフォーム連携理解を進めた。  
あわせて MyPage / 通知機能の整理、件数取得方式の見直し、Controller 設計の責務整理を行い、画面表示系と更新系の実装方針を固めた。

---

## 本日の作業テーマ
- MyPage機能の整理
- 通知機能の進行
- index検索欄のThymeleaf反映
- 最近更新件数の取得方式整理
- ManualController設計見直し
- Spring MVC フォーム連携理解
- バリデーション責務整理

---

## 実施内容

### MyPage / 通知機能整理
- MyPage と通知機能を継続して進行
- 初回表示時に必要データを一括取得する方針を継続
- 通知件数は **未読のみを対象** とする方針を確認
- 差し戻しフラグと既読状態は別管理に整理

---

### index検索欄の Thymeleaf 反映整理
トップ画面の検索フォームについて、Thymeleaf バインディング構成を整理した。

#### 確認したポイント
- `th:object`
- `th:field`

#### DTO設計
- 検索条件 DTO と一覧表示 DTO の役割分離を整理
- `ManualSearchConditionDto` を画面と紐づける流れを確認

#### 保持方針
- `categoryIds`
- `statuses`

検索後も選択状態を維持する考え方を整理した。

#### 重要な学び
Thymeleaf は

- 候補一覧
- 現在の選択値

を分けて考えると設計しやすいことを理解した。

---

### クイックビュー件数の仕様確定
トップ画面のクイックビュー仕様を整理した。

#### 最近更新
- 表示内容：直近7日以内に更新されたマニュアル件数
- 判定基準：`updatedAt >= 今日 - 7日`
- 表示例：`最近更新 5`

---

### 件数取得方式の見直し
件数だけ欲しい場合の取得方式を見直した。

#### 方針変更
一覧取得 + `size()` ではなく  
**JPA の countBy を利用する方針** に変更した。

#### Repositoryメソッド
- `countByUpdatedAtAfter()`
- `countByCreatedByUserAndStatus()`

#### 学び
件数のみ必要な場合は `countBy` の方が自然で効率的であることを理解した。

---

### Spring MVC フォーム連携学習

#### `@ModelAttribute`
フォーム入力値を DTO へ自動で詰める役割を整理した。

#### 理解したこと
- `@ModelAttribute` = DTOへ詰める
- `th:object` / `th:field` と連携する

---

#### `@Valid`
DTO に対する入力値チェックの役割を整理した。

#### 役割整理
- `@ModelAttribute` = DTOへ詰める
- `@Valid` = DTOの中身を検証する

両者は実務でセット利用が多いことを理解した。

---

### Bean Validation 整理
代表的なバリデーションアノテーションを整理した。

#### 確認項目
- `@NotBlank`
- `@NotNull`
- `@NotEmpty`
- `@Size`
- `@Min`
- `@Max`
- `@Pattern`
- `@Email`

#### 今回使用想定
##### title
- `@NotBlank`
- `@Size(max = 100)`

##### content
- `@NotBlank`

##### categoryId
- `@NotNull`

##### changeNote
- `@Size(max = 100)`

---

### バリデーション責務整理
責務を明確に整理した。

#### DTO側
形式チェック
- 必須
- 最大文字数
- 空白不可

#### Service側
業務ルール依存
- 複製時のみ必須
- 差し戻し時のみ必須

---

### Controller 設計見直し
ManualController の責務を再整理した。

#### 方針
- 画面表示系 = GET
- 更新処理系 = POST

#### Controller責務
- 画面表示
- DTO受け取り
- Service呼び出し
- redirect

thin controller 方針を再確認した。

---

## 本日の学び
- 件数のみ取得する場合は `countBy` が自然
- `@ModelAttribute` と `@Valid` は役割が異なる
- DTO と Service でバリデーション責務を分けると整理しやすい
- Thymeleaf は候補一覧と現在値を分けると設計しやすい
- GET / POST の責務分離で Controller が見やすくなる

---

## 次回着手予定
- `index` 検索欄のカテゴリ / ステータス反映完成
- `ManualController` の GET / POST / redirect 最終整理
- 最近更新件数の画面反映
