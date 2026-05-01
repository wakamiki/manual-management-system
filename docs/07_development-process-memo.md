# 07_development-process-memo.md

Version: 01.10.02  
更新日: 2026-05-01

---

## 2026-05-01（セキュリティ強化）

### 作業概要
本番公開後のセキュリティ面を見直し、H2コンソール露出リスクの抑止とHTTPレスポンスヘッダ強化を実施した。

### 実施内容
- `application-local.yml` / `application-prod.yml` を追加し、環境別設定を分離
  - `local`: H2コンソール有効
  - `prod`: H2コンソール無効
- `SecurityConfig` を修正
  - `h2-console` の公開許可設定を除去
  - セキュリティヘッダを追加
    - `X-Content-Type-Options`
    - `Referrer-Policy`
    - `Permissions-Policy`
    - `Strict-Transport-Security`
    - `Content-Security-Policy`
- SQLインジェクション観点の全件棚卸しを実施し、危険な生SQL実装がないことを確認

### 学び・改善点
- 本番運用を想定した設定は `application.yml` 直書きではなく profile 分離で管理する方が安全
- セキュリティヘッダは「段階導入 + 画面動作確認」で進めると破壊的変更を避けやすい

---

## 2026-04-30（Render公開・公開後不具合修正）

### 作業概要
Render に本番DBを作成し、Webサービスの公開まで完了した。  
公開後は、操作ガイドと全体説明資料の作成フェーズへ移行する計画とした。

### 実施内容
- Render PostgreSQL の作成と初期データ投入
- Webアプリのデプロイと公開確認
- 公開後のログイン不具合を調査し、接続先DB設定の不整合を修正
- ユーザー管理画面（更新モード）で「新規作成」ボタンが表示される不具合を修正
  - `CREATE` モード時のみ表示する条件へ修正
- マニュアル承認モーダルの「そのまま承認」で更新履歴に `,` が残る不具合を修正
  - `changeNote` の二重送信を解消
  - 「そのまま承認」時は空文字送信のみとするよう調整

### 影響範囲
- 画面テンプレートのみ
  - `src/main/resources/templates/user-management.html`
  - `src/main/resources/templates/manual-detail.html`

### うまくいったこと
- Web公開は1日想定だったが、半日程度で公開完了できた
- DB準備から公開までの主要手順を一通り通せたことで、次回以降の再現性が上がった

### 詰まったこと・苦労したこと
- デプロイ後にログインできない不具合が発生し、原因特定に時間を要した
- 接続先が旧DB設定のままになっていることを特定後は、動作が安定しスムーズに復旧できた

### 新しく理解したこと
- Web公開とDB作成は、手順を分解して進めれば十分に実行可能であることを体感した
- 難しそうに見える作業でも、前提確認と段階的検証を行えば対応できる
- 今後も複雑そうな領域に段階的に挑戦する方針を持てた

### 次回不安なこと・改善したいこと
- Render上での体感速度低下が、インフラ要因かアプリ実装要因かを切り分けたい
- 比較対象を増やすため、別アプリも開発してみたい

### 再発防止メモ
- モード分岐の表示条件は `or` ではなく `mode` 明示条件で記述する
- 同一 name パラメータの hidden 追加は、既存フォーム入力との二重送信にならないか確認する

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
## 作業記録（2026/04/14）

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


作業記録（2026-04-15）

## 本日の作業テーマ
- index画面のThymeleaf表示確認
- 通知機能関連のRepository / Service整理
- Spring Boot起動エラーの解消
- H2ファイルDB環境の確認
- Spring Security設定追加
- H2コンソール接続確認

────────────────
## 実施内容

【Repository / JPA修正】
- NotificationRepositoryのメソッド名をEntityフィールド名に合わせて修正
  - notificationType → type
- ManualRepositoryのプロパティ名不一致を修正
- JPAのcountBy / deleteBy命名規則の理解を整理

【DB / Entity修正】
- User Entityのテーブル名を user → users に変更
- H2で予約語によるDDLエラーを解消
- H2ファイルDB接続確認
  - jdbc:h2:file:./data/testdb

【起動エラー対応】
- ポート競合（8080使用中）を解消
- Spring Boot起動成功
- Tomcat起動確認
- Whitelabel Error 500 の原因を切り分け

【Security対応】
- config配下に SecurityConfig.java を新規作成
- H2コンソール用のSecurity設定追加
  - /h2-console/** permitAll
  - csrf除外
  - frameOptions sameOrigin

【画面確認】
- index画面の表示成功
- H2コンソールアクセス準備完了
- DB未登録のため一覧未表示であることを確認

────────────────
## 現在の到達点
- アプリ起動成功
- index画面表示成功
- H2コンソール確認フェーズ
- 次は初期データ投入と一覧表示確認へ進行

---

## 2026-04-15

### 作業概要
index画面のThymeleaf表示確認、通知機能関連のRepository/Service整理、起動エラー対応、H2 file DB確認、Security設定追加を実施した。

### 実施内容
- NotificationRepository のメソッド名を Entity フィールド名に合わせて修正
  - `notificationType` -> `type`
- ManualRepository のプロパティ名不一致を修正
- JPA の `countBy` / `deleteBy` 命名規則を整理
- User Entity のテーブル名を `user` -> `users` に変更
- H2 の予約語由来DDLエラーを解消
- H2 file DB 接続確認
  - `jdbc:h2:file:./data/testdb`
- ポート競合（8080）を解消し、Spring Boot 起動成功
- Tomcat 起動確認
- Whitelabel Error 500 の原因切り分け
- `SecurityConfig.java` を追加
  - `/h2-console/**` を `permitAll`
  - CSRF 対象外設定
  - `frameOptions().sameOrigin()`
- index 画面表示確認
- H2 コンソールアクセス準備完了
- DB未登録状態では一覧未表示となることを確認

### 現在地
- アプリ起動成功
- index 画面表示成功
- H2 コンソール確認フェーズ
- 次工程は初期データ投入と一覧表示確認

---

## 作業記録（2026-04-16）

### 1. 本日の作業テーマ

* Spring Security 認証機能の安定化
* ログイン後画面表示の不具合修正
* DB確認環境の強化
* 初期データ投入
* Thymeleaf 一覧表示の着手

---

### 2. 実施内容

#### 認証・ログイン

* `CustomUserDetailsService` の実装を完了
* DBの `USERS` テーブルを参照したログイン認証に成功
* ログイン成功後 `/manuals/index` へ遷移確認
* `SecurityConfig` のログイン / ログアウト設定を調整
* `ROLE` / `STATUS` の取り扱いを確認

---

#### 画面表示・CSS

* `index.html` の CSS 読み込み不具合を調査
* `th:href` 記述ミスを修正
* `config` 配下の旧CSS配信設定が `static/css` 配信を阻害していたことを特定
* 旧設定削除により CSS 正常反映を確認
* 静的リソースの標準構成を再確認

  * `resources/static/css`
  * `resources/templates`

---

#### DB / 開発環境

* H2 コンソールで重複テーブルを確認
* 単数形テーブル群が旧試作時の残骸であることを確認
* 重複テーブルを整理し、複数形テーブルへ統一
* `STATUS` 列を `STRING` 保存に修正
* 初期データ投入

  * `CATEGORIES`
  * `MANUALS`
  * `MANUAL_HISTORIES`
* `CURRENT_TIMESTAMP` を用いた日時投入方法を整理
* DBeaver 接続環境を構築し、今後の主力DBツールとして運用開始

---

#### トラブルシュート

* `Port 8080 was already in use` の原因を調査
* `httpd.exe` によるポート競合を特定
* 起動環境確認とポート競合時の切り分け方法を整理
* Thymeleaf テンプレートエラーを調査
* `ManualStatus` enum に対する `.status` 二重参照ミスを修正

---

### 3. 学習・理解できたこと

* Thymeleaf の参照起点は DTO名ではなく `model.addAttribute()` の名前
* `th:each` は「1件分のまとまり」に付与する
* enum 型は Thymeleaf 上で二重プロパティ参照しない
* Spring Boot の静的リソースは `static` 配下が標準
* `ddl-auto=update` では不要テーブルは自動削除されない
* DBeaver によるテーブル確認・初期データ投入の効率が高い

---

### 4. 現在地

* 認証機能：基本動作完了
* DB初期データ：投入完了
* 一覧画面：Thymeleaf 反映着手
* 次工程：index画面の一覧表示完成

---

### 5. 次回予定

* `index.html` の Thymeleaf 対応継続
* `th:each` による一覧カード表示
* `title / category / updatedAt / status` 表示
* 状態別ラベル表示
* 検索条件の値保持対応

---

## 2026-04-17

### 作業概要
index 画面の Thymeleaf 反映を進め、DTO の null 設計方針を明確化した。あわせて、SpEL エラーや Controller 経路競合など、表示エラーの根本原因を切り分けた。

### 実施内容
- index 一覧画面の SpEL エラーを修正
- `listDto` 起点の参照へ統一
- `th:object` と `${}` / `*{}` の使い分けを整理
- カテゴリ表示を「1データ = 1行」に修正
- Thymeleaf タグを最小構成から段階的に復元する進め方へ変更
- `summaryDto` / `activeCategories` の null 参照エラーを解消
- HomeController / ManualController の経路競合を整理
- `IS_ROLLEDBACK` 列不一致を特定し、DB と Entity のズレを把握
- index 検索フォームの送信名を DTO と一致させる方針を明確化
  - `keyword`
  - `categoryIds`
  - `statuses`
- アコーディオンの開閉不具合を切り分け
  - `th:each` 行ごとの collapse id 一意化が必要
  - `data-bs-target` / `aria-controls` / `id` の一致が必須
- `/` と `/manuals/index` の到達差で再現有無を確認し、ルーティング起因の不具合を切り分け

### DTO 設計の方針確定
- List 項目は null を使わず空 List で返す
- Service + DTO 初期化の二重防御を採用
- 件数項目は `int` を基本とし、初期値 0 で扱う
- 値の扱いを以下で統一
  - 必須項目: null 禁止
  - 件数: 0
  - List: 空 List
  - 任意項目: null 許可
- Category DTO は `categoryId` を持つ方針
- User DTO は `id` + `displayName` を持つ方針
- DTO は「用途ごとの最小構成」で運用

### 学び
- Thymeleaf エラーは View 側だけでなく Controller やデータ構造起因が多い
- DTO の null 設計は画面安定性に直結する
- `th:each` は「1データ = 1 UI ブロック」で設計すると崩れにくい
- 画面が真っ白になる現象でも、まずはデータ件数（0件除外）を確認すると原因特定が速い
- Thymeleaf の不具合に見えても、Controller の Model 詰め替え不足や URL 経路が原因になりやすい

### 次回着手予定
- `index.html` の Thymeleaf タグを段階復元
  - `listDto -> summary -> category -> manual -> history`
- カテゴリチェックボックスと検索条件の連携実装
- 一覧カード UI の仕上げ
- `docs/02` への設計方針反映

---

## 2026-04-18

### 作業概要
マニュアル詳細画面の実装に向けて、DTO 構成と権限制御の整理を進めた。あわせて、保存結果のメッセージ表示導線と `try-catch` の適用方針を整理した。

### 実施内容
- `ManualDetailDto` の項目整理を実施
- 詳細画面ボタン制御用フラグ（`canEdit` など）の追加を開始
- Service 側で権限共通処理をまとめる方針を整理
- 保存成功/失敗メッセージを画面に返す導線を整理
- Controller での `try-catch` 適用位置を確認
  - 成功時メッセージ
  - 失敗時メッセージ
  - リダイレクト先の統一

### 苦労した点
- DTO 整理
- メッセージ追加
- `try-catch` の責務切り分け

### 学び
- 画面と通信しながら早い段階で実装確認を進める方が効率が高い
- 全体の流れを早く把握でき、必要機能から順に着手しやすくなる

### 次回着手予定
- 詳細画面の権限判定メソッド整備
- `buildDetailPermissions(manual, currentUser)` の実装
- 詳細画面ボタン表示制御の Thymeleaf 反映

---

## 2026-04-19

### 作業概要
マニュアル詳細画面の実装準備として、Service 内の権限判定メソッド設計と DTO 組み立て方針を整理した。あわせて、詳細画面遷移時の 404 エラー切り分けと画面レイアウト検討を行った。

### 実施内容
- `canApproveManual(...)` の記述簡略化方針を整理
  - ガード節維持と1行 return のトレードオフを確認
- `changeNote` 必須/任意の扱いを整理
  - 引数を統一して必須判定は操作種別で分岐する方針を確認
- `ManualDetailDto` の組み立て方法を整理
  - DTO同士を加算せず、ベースDTO作成後に権限フラグを適用する流れを確認
- 変数命名方針を整理
  - フラグ適用前 DTO は `baseDetailDto` を候補として採用
- 詳細画面遷移の 404 を切り分け
  - 静的リンク参照 (`manual-detail.html`) と Controller 経路参照の差を確認
  - 再起動/ハードリロード/リンク先確認で再現確認手順を整理
- 詳細画面レイアウト方針を検討
  - ヘッダーは情報領域、本文カードはタイトル+本文に分離する案を整理
- テキスト左揃えの実装方法を確認
  - `text-start` と `text-align: left;` の使い分けを確認

### 学び
- 画面遷移不具合は、テンプレート記述だけでなく実行中プロセスのキャッシュ影響も確認する必要がある
- DTO組み立ては「生成」と「権限フラグ適用」を分離すると責務が明確になりやすい

### 次回着手予定
- `buildDetailPermissions` の実装着手
- `manual-detail` の表示制御（ボタン表示/非表示）を Thymeleaf に反映
- 詳細画面の UI 役割分離（ヘッダー情報・本文・履歴）を具体化

## 2026-04-20

### 作業概要
Service の責務整理を実施し、`ManualService` を読み込み系・更新系・権限判定系の3つに分離する方針を確定した。あわせて、マニュアルエディターを編集モードと複製モードで明確に扱う方針を確定した。

### 実施内容
- Service 分離方針を確定
  - 読み込み系: `ManualQueryService`
  - 更新系: `ManualCommandService`
  - 権限判定系: `ManualPermissionService`
- `manual-form` の mode 運用方針を再整理
  - `edit` と `copy` を別ユースケースとして扱う
  - 画面は共通、保存処理はモード別に分ける
- DTO責務整理を実施
  - Form DTO は入力 + 初期表示用として扱う
  - 表示用レスポンスDTOと分ける方針を維持
- フォーム送信設計を整理
  - `<form th:action>` を基本とする
  - ボタン別送信先は `th:formaction` を使用可能
  - mode により送信先を切り替える方針を明確化
- Thymeleaf / UI の調整
  - mode バッジ表示調整
  - レイアウト調整（上下構造の分離）
- 関連仕様を docs に反映
  - `docs/02`
  - `docs/03`
  - `docs/04`

### 反省点
- Thymeleaf を一気に変更しようとして手戻りが増えた
- 現状は一括変更より、1変更ごとの確認を優先するべきだった

### 学び
- Service は画面やボタン単位ではなく、責務単位で分けると見通しが良くなる
- `ManualService` の肥大化は探索時間を増やすため、責務分離の効果が大きい
- Service の責務境界を先に決めると、Controller と DTO の設計が進めやすくなる

### 次回着手予定
- Controller 呼び出し先の整理
- mode 別 submit 先と Service 呼び出しの整合確認
- my-page の Thymeleaf 適用

---

## 2026-04-21

### 作業概要
ユーザー管理画面とカテゴリ管理画面で `CREATE / EDIT` の mode 切り替えを実装し、作成と更新の文脈を明確に分離した。あわせて、カテゴリ同名時の確認フロー設計を進めた。

### 実施内容
- ユーザー管理画面を mode 切り替え対応へ変更
  - 初期表示を `CREATE` に統一
  - 一覧の設定操作で `EDIT` へ遷移する方針を確定
- カテゴリ管理画面も同様に mode 切り替え方針を適用
- 資料03の userId 方針を更新
  - `更新時変更不可` から `変更可` へ変更
- `formDto` に `UserViewMode` が入らない問題を調査
  - 原因: `ViewMode` のクラス名リネーム不整合
  - 対応: enum ファイル名/参照名を整えて解消
- カテゴリ重複時確認フローを整理
  - 重複時のみ confirm 表示
  - 確認後に同一導線で再送する実装方針を確認

### うまくいったこと
- 断片的なヒントでも、次にやる作業を自分で判断できる場面が増えた

### 詰まったこと・苦労したこと
- `formDto` への mode 反映不具合の原因特定に時間がかかった

### 新しく理解したこと
- 重複チェックで確認ダイアログを出した後、同一導線で処理を継続する組み立て方を理解した

### 次回不安なこと・改善したいこと
- User 系のパスワードリセットとハッシュ化実装は時間がかかる見込みのため、先にタスク分解してから着手する

---


## 2026-04-22

### 作業概要
user-management / category-management のモード切替・フォーム送信まわりを進め、テンプレートエラーの切り分けと修正を実施した。あわせて、CSS調整を DevTools で確認しながら進めた。

### 実施内容
- user-management 画面でボタン送信先（`th:formaction`）と条件式の見直し
- category-management 画面で `th:object` / `th:field` / `${}` と `*{}` の使い分け修正
- GET/POST の責務を整理し、405/500系エラーの原因を特定・修正
- パス不一致（`@PostMapping` と `th:formaction` の不一致）を修正
- テンプレート内の null 参照（`targetCategory` / `targetUser`）対策を実施
- モード表示のある画面の見た目を調整（DevToolsで最終適用CSSを確認しながら修正）

### うまくいったこと
- DevTools を使って CSS の適用元と上書き関係を確認でき、モード表示画面の見た目調整を進めやすくなった。

### 詰まったこと・苦労したこと
- 長時間作業でエラー切り分けの精度が落ち、似たミスを繰り返した。

### 新しく理解したこと
- `redirect:/...` と `"user-management"` のようなビュー名返却の違いを理解できた。
- 画面遷移（GET）と更新処理（POST）の分離が重要だと再確認できた。

### 次回やること
- パスワード関連機能（リセット処理・後続のハッシュ化準備）に着手する。

---

## 2026-04-23

### 作業概要
ユーザーのパスワード運用を強化し、ハッシュ保存と初回変更導線を実装した。あわせて、初期パスワード表示をフラッシュメッセージ運用で整理した。

### 実施内容
- `PasswordEncoder`（BCrypt）を導入し、保存時にハッシュ化する実装へ移行
- 初期パスワード生成処理を整理し、ユーザー作成/リセット時に発行する導線を実装
- 初回ログイン時の変更導線を追加
  - `passwordChangeRequired=true` のユーザーを変更画面へ遷移
- `PasswordChangeRequestDto` を追加し、パスワード入力専用DTOへ分離
- パスワード制約を整理
  - 文字数: 8〜32
  - 大文字/小文字/数字を各1文字以上
  - 許可文字: `A-Za-z0-9!@#$%^&*()_-+=`
- 初期パスワード通知をフラッシュメッセージで実装
  - 通常メッセージは自動消去
  - 資格情報通知は手動で閉じるまで表示
  - コピーボタンでクリップボードへコピー可能にした
- 起動時エラーを切り分け
  - `password_change_required` 列追加時の NOT NULL 問題
  - `customLoginSuccessHandler / userService / securityConfig` の循環参照
  - 依存関係とDB列の整合を調整して起動確認

### 学び
- パスワード処理は「保存時エンコード」「認証時はSpring Securityへ委譲」で整理すると実装が安定する
- リダイレクト後に値を渡す場合はフラッシュメッセージが扱いやすい
- 初期表示通知は業務上、短時間自動消去より手動クローズ運用が実用的

### 次回やること
- パスワード変更画面の権限/例外メッセージの最終調整
- 監査ログ（パスワードリセット/変更操作）の実装着手

### 追加実施内容（同日）
- ページネーション実装方針の整理を実施
  - Service で `PageRequest.of(page, 10, ...)` を生成する流れを確認
  - Repository 側で `OrderBy...` を固定している場合は Service 側 `Sort` を省略できる方針を確認
  - `Page` / `Pageable` の import は Spring Data の型を使う前提を整理
- ページング共通化の方針を整理
  - `PageRequest.of(page, 10)` の生成を共通メソッド化する設計案を確認
  - 0未満ページ番号の補正（`Math.max(page, 0)`）を共通処理に寄せる方針を整理

### 振り返り
password関係がもっと時間がかかるかと思ったがspringbootが優秀なおかげで簡単だった。  
passwordの文字数制限や必須項目の指定もとても簡単。苦手意識がなくなった。  
次回はページネーション機能を完成させ業務メソッドのテストをしていきたい。

---

## 2026-04-24

### 作業概要
例外ハンドリング運用の整理、認可ルールの資料反映、テスト仕様との整合、テンプレート点検を進めた。  
あわせて、テスト結果記録ファイルの整備と DB 設計書の実装準拠反映を実施した。

### 実施内容
- `GlobalExceptionHandler` の運用方針を整理
  - 失敗メッセージは例外ハンドラへ集約
  - 成功メッセージは Controller で扱う方針を確定
- 認可ルールを確定し、`docs/04` と `docs/06` を同期
  - パスワード変更は本人のみ
  - パスワード初期化は管理者のみ
- `password-change.html` / `index.html` を中心に Thymeleaf の怪しい箇所を調査し、修正方針を整理
- `docs/05` を実装準拠で更新（文字化け確認できない点は保留）
- `docs/06_test-result.md` をテスト記録用テンプレートとして整理し、翌日実施項目を追加
- スクリーンショット資産を更新し、コミット・プッシュを実施

### 振り返り
変更に伴いエラーが頻発している状態でクイック修正を通したい時、  
エラー部分を一回コメントアウトしてからクイック修正をすると通ることに気づいた。  
Dtoは初めに大体の感覚で作っておくよりも必要な時にファイルを増やす方が管理しやすい。  
業務メソッド全完成。  
残作業　画面レイアウト調整、ゲストユーザー動線

### 新しく理解したこと
- エラーハンドリングの流れを理解した。


---


## 2026-04-25

### 作業概要
ゲストログイン導線の実装を進め、環境変数連携・認証処理・画面フォーム連携を調整した。  
あわせて、Thymeleaf のフォームバインド不整合（`name` / `th:field` / `th:object`）起因のエラー切り分けを重点的に実施した。

### 実施内容
- ゲストログイン機能を追加
  - `POST /login/guest` 導線を実装
  - `login.html` にゲストログインフォームを追加
  - CSRF hidden を追加し POST 送信を成立
- 環境変数運用を整備
  - `application.yml` に `app.guest-login.login-id/password` を定義
  - 設定ファイルを `yml` 運用へ統一し `application.properties` を削除
  - 実行環境（OS / Run Configuration）での値注入手順を確認
- ゲスト認証失敗の原因切り分けを実施
  - `BadCredentialsException` を確認
  - DB上のユーザー存在確認と平文/ハッシュ不一致を特定
  - 環境変数値とDBの認証情報整合を確認してログイン成功
- LoginController を調整
  - 失敗時メッセージと例外処理を整理
  - 認証後の遷移動線を調整
- テスト仕様書を更新
  - `docs/06_test-specification.md` にゲストログイン観点と初期化後再変更観点を追加
  - AUTH/PWD 系テストケースを明文化

### うまくいったこと
- ログを使って認証失敗の種類（ユーザー未存在 / パスワード不一致）を分離でき、修正対象を素早く絞れた。
- ゲストログイン導線を最小変更で既存認証フローへ組み込めた。

### 詰まったこと・苦労したこと
- HTML 記述ミス、特に Thymeleaf の `name` 指定名称・`th:field`・`th:object` の不整合でエラーが多発した。
- 画面上は同じ挙動に見えても、DTO バインド先不一致で値が送れていないケースの切り分けに時間がかかった。

### 新しく理解したこと
- 認証失敗時は例外種別とログを先に確認すると、修正箇所の切り分けが速い。
- フォーム送信は `th:object` と `th:field` の対応が最優先で、`name` の手動指定は必要箇所を限定すべき。
- ゲストログインのような設定依存機能は、環境変数とDBデータの整合を前提にテストする必要がある。

### 次回不安なこと・改善したいこと
- パスワード初期化後の変更結果が他アカウントでも正しく反映されるかを横断確認する。
- テスト時にフォームバインド項目の確認手順（送信値/DTO受け取り）を固定し、同種エラーの再発を減らす。

---

## 2026-04-26

### 作業概要
手動テストを継続し、マニュアル公開ボタンの送信不具合と `@Valid` 周辺のエラーを修正した。  
あわせて、本文表示の改行保持（index / manual-detail / my-page）を実装し、テスト資料とAPI資料の整合を更新した。  
可読性改善として Manual 系 Controller の分割作業も完了した。

### 実施内容
- `manual-detail` の「マニュアル公開」ボタンをフォームPOST化し、`/manuals/{manualId}/actions/submit` へ送信するよう修正
- `ManualCommandController` の submit を `POST` 受けに統一し、詳細画面へリダイレクトする導線に修正
- `ManualCommandService` に submit 処理を追加し、DRAFT→PENDING遷移・通知作成を実装
- テスト実施中に発生した valid / null 系エラーを修正
- `@Valid` と `BindingResult` の引数順を見直し、想定どおり画面へ戻る動作へ修正
- 見通しが悪くなっていた Manual Controller を責務単位で分割
- 本文表示に `manual-content-preserve` を適用
  - `manual-detail`
  - `index`（アコーディオン内本文）
  - `my-page`（通知カード・作成/未承認カード）
- `docs/06_test-result.md` の 2026-04-26 ログを一本化し、明日実施項目を更新
- `docs/04_api-design.md` のエンドポイント一覧を現コードに合わせて更新
- `docs/03_screen-design.md` / `docs/06_test-specification.md` に改行表示・参照先の整合を反映
- テスト結果・仕様資料（`docs/06`）の更新内容を整理

### 振り返り
テスト実施→validエラー→修正中→manualコントローラーが見にくい→コントローラー分割作業終了、の流れで進行した。  
`@Valid` の直後に `BindingResult` の引数を置かないと想定どおりの挙動にならないことに気づいた。  
引数の順番は自由だと思っていたが、Spring MVC のバインド処理順に依存することを実作業で理解した。

### 学び・改善点
- `type="submit"` ボタンはフォーム外だと送信されないため、画面ボタン仕様とControllerマッピングをセットで確認する必要がある
- 仕様資料は「機能実装直後」に最小差分で更新すると、テスト時の期待値ズレを減らせる
- `@Valid` 付き引数の直後に `BindingResult` を置く必要がある
- Controller が見にくくなった段階で早めに責務分割したほうが、後続修正の速度と安全性が上がる

### 次回着手予定
- ゲストログイン（環境変数あり/なし）導線の最終テスト
- 承認ダイアログ分岐（コメントあり/なし）の実機確認
- 公開実行時通知（NT-001）の作成確認

---

## 2026-04-27

### 作業概要
手動テストを継続し、Postman での直POST確認を本格運用した。  
あわせて、認可異常系テストの実施手順を整理し、未テスト項目の優先度を見直した。

### 実施内容
- `docs/06_test-result.md` に 2026-04-27 の実施ログを追記・整理
- `docs/06_test-specification.md` の実施済みケースにチェック反映
- Postman を使った直POSTテストを実施
  - 作成者本人承認不可
  - PENDING以外承認不可
  - USER承認不可
  - 直POST時の 200 応答でも、例外で業務拒否されていることをログで確認
- CSRF / セッション前提での検証手順を整理
  - `Login_ユーザー` → `Get_CSRF` → 対象POST の実行順を確立
  - 未テスト項目のうち Postman 優先確認対象を整理
  - 成功系を除外し、失敗/拒否系のみの直POST対象へ再編
- `AUTH-025` の前段として、ADMINで対象ユーザー停止処理を実施し PASS を記録

### 振り返り
細かくエラーメッセージを指定していたことで、バックエンドエラーの特定が進めやすかった。  
手動テスト中心の運用で、ケースの切り分けと結果記録の往復が多く、想定より時間がかかった。

### 新しく理解したこと
- Postman での操作手順（Environment / Cookie / CSRF の扱い）
- 直POSTテストでの判定方法
  - HTTP 200 でも業務例外で拒否されていれば「拒否成功」として評価する

### 次回不安なこと・改善したいこと
- テスト工数が想定より大きいため、次回の開発では早期に自動テストを導入したい
- 異常系を先に自動化し、手動テストは画面統合確認に集中できる運用へ切り替えたい

---

## 2026-04-28

### 作業概要
index更新処理、レイアウト微調整、例外遷移調整を実施し、短時間で反映した。  
あわせて、カテゴリ重複時モーダルと承認時モーダルの実装、関連テスト仕様の追記を進めた。

### 実施内容
- index更新処理を実装し、画面反映タイミングを調整
- 画面レイアウトの微調整を実施
- 例外時の遷移先を `index` 固定から元画面へ戻す運用に修正
  - `manual-detail`
  - `manual-create`
  - `manual-form`
- カテゴリ重複時モーダル（DuplicateStatus連携）を実装
  - ACTIVE重複: エラーメッセージ
  - INACTIVE重複: モーダル確認後続行
- 承認時モーダルを実装
  - 「そのまま承認」/「追加する（更新履歴入力）」分岐
- `06_test-specification.md` / `06_test-result.md` に当日分を反映

### 振り返り
index更新処理、レイアウト微調整、例外遷移調整は手際よく短時間で実装できた。  
一方で異常系テストは、弾いている事実は確認できても「想定どおり拒否」か「想定外エラー」かの判定に時間がかかった。

### 詰まったこと・苦労したこと
- 異常系テストの判定基準が曖昧になりやすく、HTTPステータスだけでは判断できないケースが多かった
- 200応答でも業務例外で拒否されるケースと、500の想定外エラーの切り分けに苦労した

### 学び・改善点
- 異常系は「HTTPコード」だけでなく「サーバログ（例外種別）」と「DB結果」まで確認して判定する必要がある

### 次回不安なこと・改善したいこと
- 異常系テストの試し方と確認方法をさらに整理し、想定どおりの拒否判定を確実にできるようにする
- テスト観点を先に整理し、実施時の判定迷いを減らす

---

## 2026-04-29

### 作業概要
正常系テストを完了させたうえで、正常系で発見したエラーを先に解消し、異常系テストへ段階的に移行した。  
また、ログ出力の見直しと事前準備の整理により、異常系で「想定どおりの拒否」と「想定外エラー」を判定できる状態を整えた。

### 実施内容
- 正常系テストの完了と結果反映（`06_test-specification.md` / `06_test-result.md`）
- 正常系実施中に出た不具合の修正
  - 複製保存時の遷移不整合
  - モーダル関連の画面遷移/送信先調整
  - 例外時の戻り先調整
- 異常系テスト実施前の事前準備を整理
  - 対象ケースの前提データ確認
  - Postman実施手順（セッション/CSRF/URL）再確認
  - 判定観点（画面/ログ/DB）の明確化
- ログの粒度を見直し、保守用ログを再配置
- docs整備
  - `README.md` の導線・開発状況整理
  - `03/04/05` の実装整合更新
  - `TODOメモ` にDB移行チェックリストとRender公開手順を追加

### 振り返り
ログを細かく追加し、正常系を先に完了・修正してから異常系へ入ったことで、テスト判定の精度が上がった。  
事前準備を丁寧に行った結果、想定どおりの異常エラーを再現できるようになった。

### うまくいったこと
- 異常系着手前に正常系を固めたことで、原因切り分けがしやすくなった
- エラー時にアラート表示HTMLを確認する運用を確立でき、どこで何が失敗したかを追跡しやすくなった
- 実装・テスト・docs更新を同日に同期できた

### 詰まったこと・苦労したこと
- 200応答でも業務拒否されるケースの判定が難しく、初動で混乱しやすかった
- 画面エラー、権限エラー、遷移エラーの境界を揃えるまでに時間がかかった

### 新しく理解したこと
- 想定どおりの異常かどうかは、HTTPステータスだけでなく「アラート表示内容」「サーバログ」「DB更新有無」の3点で判定するのが有効
- 異常系テストの再現性は、事前条件（データ状態・ロール・送信経路）の固定で大きく改善する

### 次回不安なこと・改善したいこと
- DB移行・Web公開時に、環境差分で同じエラーが再現しないリスクがあるため、移行チェックリスト運用を徹底する

---
