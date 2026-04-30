# 04_api-design.md

Version: 01.10.00
更新日: 2026-04-30

---

## 1. API 一覧

### 1-1. Manual API
- GET `/manuals/index`
- GET `/manuals/index/my-created`
- GET `/manuals/index/my-pending`
- GET `/manuals/index/new-updatedAt`
- GET `/manuals/{manualId}/detail`
- GET `/manuals/create`
- GET `/manuals/{manualId}/actions/copy`
- GET `/manuals/{manualId}/actions/edit`
- POST `/manuals/create/draft`
- POST `/manuals/create/pending`
- POST `/manuals/{manualId}/actions/save-draft-copy`
- POST `/manuals/{manualId}/actions/save-pending-copy`
- POST `/manuals/{manualId}/actions/edit-toDraft`
- POST `/manuals/{manualId}/actions/save-draft-edit`
- POST `/manuals/{manualId}/actions/edit-to-pending`
- POST `/manuals/{manualId}/actions/save-pending-edit`
- POST `/manuals/{manualId}/actions/submit`
- POST `/manuals/{manualId}/actions/approve`
- POST `/manuals/{manualId}/actions/rollback`
- POST `/manuals/{manualId}/actions/archive`
- POST `/manuals/{manualId}/actions/restore`

### 1-2. Category API
- GET `/categories`
- GET `/categories/{categoryId}/action`
- POST `/categories/create`
- POST `/categories/{categoryId}/update`
- POST `/categories/{categoryId}/deactivate`
- POST `/categories/{categoryId}/activate`

### 1-3. User API
- GET `/users`
- GET `/users/{userId}/action`
- POST `/users/create`
- POST `/users/{userId}/update`
- POST `/users/{userId}/deactivate`
- POST `/users/{userId}/activate`
- POST `/users/{userId}/reset-password`
- GET `/users/change-password`
- POST `/users/action/change-password`
- GET `/users/{userId}/operation-histories`

### 1-4. Auth API
- GET `/`
- GET `/login`
- POST `/login`（Spring Security）
- POST `/login/guest`
- POST `/logout`（Spring Security）

### 1-5. My Page API
- GET `/my-page`

## ■ ヘルスチェックAPI

### GET /health

#### 概要
アプリケーションの起動確認用エンドポイント。
外部サービス（Renderなど）からの稼働監視に使用する。

#### 認証
不要

#### レスポンス
- ステータスコード: 200 OK
- レスポンスボディ: "OK"

#### 備考
- DB接続状態や内部情報は返却しない（セキュリティ考慮）
- 本番環境での起動確認用として使用する

---

## 2. 共通方針

### 2-1. Controller 方針
- Controller は thin controller とする
- 業務ロジックは Service に集約する
- Request DTO / Response DTO を使い分ける
- View エンドポイントと JSON API を混在させない
- 画面表示では `Model` に詰める属性名を固定し、テンプレート参照と一致させる
- 一覧からのモード切替は GET、保存系は POST に分離する
- 保存系は PRG（Post/Redirect/Get）を前提に `redirect:/...` を使用する
- 認証成功後の遷移は `CustomLoginSuccessHandler` で制御する

### 2-1A. Service 分離方針
- 読み込み系ユースケースは `ManualQueryService` へ集約する
- 更新系ユースケースは `ManualCommandService` へ集約する
- 権限判定は `ManualPermissionService` へ集約する
- Controller からは用途に応じて Query / Command を呼び分ける

### 2-2. バリデーション
- DTO の形式チェックは `@Valid` で行う
- 業務ルール依存の判定は Service で行う
- パスワード変更入力は専用DTO（`PasswordChangeRequestDto`）で受ける
- 主要DTOの扱い
  - `ManualActionRequestDto.changeNote` は必須（`@NotBlank`）
  - `ApproveRequestDto.changeNote` は任意（`@Size(max=100)`）
  - `ManualSearchConditionDto` は現時点で制約なし（将来必要に応じて追加）

### 2-2A. パスワード操作の権限制御
- パスワード変更画面表示（`GET /users/change-password`）は本人のみ実行可能とする
- パスワード変更実行（`POST /users/action/change-password`）は本人のみ実行可能とする
- パスワード初期化（`POST /users/{userId}/reset-password`）は管理者のみ実行可能とする
- 変更対象ユーザーは画面入力値ではなく認証情報（`Principal`）基準で判定する

### 2-2B. 認可ルール表（確定版 / 2026-04-24）

#### 共通前提
- 無効ユーザー（`isActive=false`）は全画面で拒否する
- 権限NG時はメッセージ付きで詳細画面へリダイレクトする
- 判定は Service で実施し、画面表示制御は補助として扱う

#### Manual 系
| 操作 | role | isOwner | status | 追加条件 | 判定 |
| --- | --- | --- | --- | --- | --- |
| 一覧 / 検索 / 詳細閲覧 | 全ロール | 不問 | 不問 | 有効ユーザー | 許可 |
| 編集 | 全ロール | 作成者のみ | DRAFT / PENDING | - | 許可 |
| 複製 | 全ロール | 不問 | PENDING / APPROVED / ARCHIVED | DRAFT は不可（編集で対応） | 許可 |
| マニュアル公開（申請） | 全ロール | 作成者のみ | DRAFT | - | 許可 |
| 承認 | ADMIN / APPROVER | 作成者以外 | PENDING | - | 許可 |
| 差し戻し | ADMIN / APPROVER | 作成者以外 | PENDING | 更新履歴必須 | 許可 |
| アーカイブ | ADMIN / APPROVER | 不問 | APPROVED / PENDING / DRAFT | 更新履歴必須 | 許可 |
| 復帰 | ADMIN / APPROVER | 不問 | ARCHIVED | カテゴリ有効のみ確認（作成者一致不要） / 更新履歴必須 | 許可 |

#### User 系
| 操作 | role | 追加条件 | 判定 |
| --- | --- | --- | --- |
| ユーザー管理画面表示 | ADMIN / GUEST | 有効ユーザー | 許可（GUESTは閲覧のみ） |
| ユーザー作成 / 更新 / 停止 / 復帰 | ADMIN | 有効ユーザー | 許可 |
| パスワード初期化（reset-password） | ADMIN | 対象 userId 指定 | 許可 |
| パスワード変更（change-password） | 本人のみ（全ロール共通） | Principal と変更対象が一致 | 許可 |

#### Category 系
| 操作 | role | 追加条件 | 判定 |
| --- | --- | --- | --- |
| カテゴリ管理画面表示 | ADMIN / GUEST | 有効ユーザー | 許可（GUESTは閲覧のみ） |
| カテゴリ作成 / 更新 / 停止 / 復帰 | ADMIN | 有効ユーザー | 許可 |
| 同名カテゴリ作成 / 更新 | ADMIN | 重複時は confirm 表示後に続行可 | 許可（確認付き） |

#### 画面表示制御方針（補助）
| 利用者 | 方針 |
| --- | --- |
| USER / APPROVER | 作業を迷わせないため、権限外ボタンは非表示 |
| GUEST | 理由を伝えるため、権限外ボタンは非活性 + 理由表示 |

### 2-3. 例外ハンドリング
- `@ControllerAdvice`（`GlobalExceptionHandler`）で画面系例外を集約処理する
- 画面系はフラッシュメッセージ（`message` / `messageType`）を付与してリダイレクトする
- Controller 側での個別 `try-catch` は最小化し、Service で throw した例外を共通処理へ委譲する
- URIプレフィックス別の戻り先
  - `/users` -> `redirect:/users`
  - `/categories` -> `redirect:/categories`
  - `/my-page` -> `redirect:/my-page`
  - `/login` -> `redirect:/login`
  - その他 -> `redirect:/manuals/index`
- 主な例外候補
  - `NotFoundException`
  - `UnauthorizedException`
  - `InvalidStateException`

### 2-4. ID 命名方針
- Entity 主キーは `id`
- API パス、Service 引数、DTO では意味付き ID 名を使う
  - `manualId`
  - `categoryId`
  - `notificationId`

---

## 3. Manual API 詳細

### 3-1. 一覧取得
- GET `/manuals/index`
- 条件例
  - keyword
  - categoryIds
  - statuses
- 初期表示では `statuses = [PENDING, APPROVED]` を前提とする
- 検索条件は `ManualSearchConditionDto` にまとめて扱ってよい
- 検索条件のフォーム送信名は以下に統一する
  - `keyword`
  - `categoryIds`
  - `statuses`
- 一覧返却項目は画面要件に合わせて以下を含める
  - manualId
  - title
  - status
  - updatedAt
  - createdByUser
- アコーディオン展開時に以下を返却してよい
  - categoryName
  - content
  - histories
- 一覧のアコーディオン collapse は行ごとに id をユニーク化する

### 3-2. 詳細取得
- GET `/manuals/{manualId}/detail`
- 詳細返却項目は以下を含めてよい
  - manualId
  - categoryName
  - title
  - content
  - status
  - createdAt
  - updatedAt
  - createdByUser
  - histories
- histories の各要素には以下を含めてよい
  - changedAt
  - changeNote
  - changedByUser

### 3-3. 履歴取得
- GET `/manuals/{manualId}/histories`（将来 API）

### 3-4. 作成
- POST `/manuals`（将来 API）
- 画面は新規作成専用画面を使う
- 編集 / 複製は `manual-form` を使う
- API はユースケースごとに分けてよい
- 新規作成では changeNote は任意とする

### 3-5. 更新
- PUT `/manuals/{manualId}`（将来 API）

### 3-6. 状態変更
- submit
- approve
- rollback
- archive
- restore
- 実行エンドポイントは以下を使用する
  - `POST /manuals/{manualId}/actions/submit`
  - `POST /manuals/{manualId}/actions/approve`
  - `POST /manuals/{manualId}/actions/rollback`
  - `POST /manuals/{manualId}/actions/archive`
  - `POST /manuals/{manualId}/actions/restore`
- 互換URL（旧）を当面受け付ける
  - `POST /manuals/{manualId}/actions/edit-toDraft`（旧）
  - `POST /manuals/{manualId}/actions/edit-to-pending`（旧）
- UI 上は rollback / archive / restore を詳細画面のインライン入力で `changeNote` 入力後に確定してよい
- approve は確認ダイアログを出し、必要に応じてインライン入力で履歴コメントを受け取ってよい
- restore 対象は `approvedAt` を保持した `ARCHIVED` マニュアルとする
- 詳細画面のボタン表示はモックに合わせて一時的に共通表示としてよい

### 3-7. 複製
- POST `/manuals/{manualId}/actions/copy`（将来 API）

---

## 4. DTO 方針

### 4-1. Request DTO
- `ManualDraftDto`
- `ManualEditFormDto`
- `ManualActionRequestDto`
- `ApproveRequestDto`
- `ManualSearchConditionDto`
- `CategoryFormDto`
- `UserFormDto`
- `PasswordChangeRequestDto`

### 4-2. Response DTO
- `ManualResponseDto`
- `ManualDetailDto`
- `CategoryResponseDto`
- `UserResponseDto`
- `CategoryViewDto`
- `UserViewDto`
- `ManualIndexDto`
- `MyPageDto`

### 4-2A. 管理画面フォーム mode
- `ViewMode` を `CREATE / EDIT` で扱う
- フォームDTOに mode を保持する
- 画面表示時は mode により見出し・ボタン・送信先を切り替える
- mode 未設定による null 参照を避けるため、DTO初期値を `CREATE` にする

### 4-3. 一覧検索 DTO 補足
- `ManualSearchConditionDto`
  - keyword
  - categoryIds
  - statuses
- `ManualResponseDto`
  - manualId
  - title
  - status
  - updatedAt
  - createdByName
  - categoryName
- 一覧取得では `Specification` を用いて `ManualSearchConditionDto` の条件を組み合わせてよい
- 内部項目名は `changeNote` のままでよいが、画面上の入力ラベルは `更新履歴` と表示してよい
- DTO の null 設計は以下で統一する
  - List: null 禁止、空 List を返す
  - 件数: `int` + 0 初期値
  - 必須項目: null 禁止
  - 任意項目: null 許可

### 4-4. 通知 / マイページ DTO
- `NotificationBadgeDto`
- `NotificationItemDto`
- `MyPageDto`
  - rollbackManualList
  - createdManualList
  - draftManualList

### 4-5. PasswordChangeRequestDto
- `currentPassword`
- `newPassword`
- `confirmPassword`
- 文字数: 8〜32
- 文字種: 大文字 / 小文字 / 数字を各1文字以上
- 許可文字: `A-Za-z0-9!@#$%^&*()_-+=`

### 4-6. 資格情報通知（フラッシュ）
- `reset-password` / `create` 成功時はフラッシュメッセージで一時パスワードを通知する
- 通常メッセージは自動消去、資格情報通知は手動クローズまで保持する
- 資格情報通知にはコピー操作を付与する

---

## 5. 通知ルール
- submit 時は APPROVER 全員に通知
- approve 時は作成者へ通知
- rollback 時は作成者へ通知
- 通知件数は未読のみを対象とする
- 一覧は状態条件で全件表示する
- 通知は一覧表示だけで既読化しない
- 承認完了時に対象マニュアルの `PENDING_APPROVAL` 通知を全削除する
- ホーム通知バッヂは
  - 上段: 差し戻し件数
  - 下段: 未承認件数

---

## 6. My Page API 補足
- `GET /my-page`
- USER の返却対象
  - 通知
  - 作成マニュアル
- APPROVER / ADMIN の返却対象
  - 通知
  - 作成マニュアル
  - 未承認マニュアル
- 初回表示時に必要な一覧を全取得し、タブ切替は画面内で表示切替する

---

## 7. Thymeleaf 連携実装メモ
- `listDto` は一覧表示データの参照起点
- `manualSearchConditionDto` は検索条件の保持参照起点
- `th:each` で複製される UI 要素に static id を使わない
- `th:object` 配下では `th:field="*{...}"` を使う
- `th:field="${...}"` は使わない
- `th:object` 配下で `formDto.` を重ねて参照しない
- `EL1007E` 発生時は以下の順に確認する
  1. Controller が対象 Model 属性を追加しているか
  2. DTO の中身が null ではないか
  3. テンプレート参照名と DTO プロパティ名が一致しているか
