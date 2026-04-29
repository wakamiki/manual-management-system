# 02_system-specification-and-detailed-design.md

Version: 01.06.18
更新日: 2026-04-23

---

## 1. 対象機能
- マニュアル一覧
- マニュアル詳細
- マニュアル入力
- マニュアル編集
- 申請 / 承認 / 差し戻し / アーカイブ / 復帰
- マニュアル複製
- マニュアル更新履歴
- ユーザー管理
- カテゴリ管理
- 通知機能
- マイページ

---

## 2. アーキテクチャ
- MVC + Service + Repository
- Controller は thin controller を維持する
- 業務ロジックは Service に集約する
- Entity は整合性を守る専用メソッドを持つ
- Service は責務で分離する
  - 読み込み系: Query Service
  - 更新系: Command Service
  - 権限判定系: Permission Service

### 2-1. Controller 方針
- 画面表示系は GET
- 更新処理系は POST
- Controller は thin controller 方針
- DTO 受け取りと Service 呼び出しに責務を限定する
- 保存系の戻り値メッセージは FlashAttribute で渡す
- 保存系は `try-catch` で成功/失敗メッセージを分岐して扱う

---

## 3. 権限

### 3-1. ロール
- USER
- APPROVER
- ADMIN
- GUEST

### 3-2. 権限ルール
- USER: 作成 / 更新 / 申請
- APPROVER: 承認 / 差し戻し / 復帰 / アーカイブ
- ADMIN: ユーザー管理 / カテゴリ管理
- GUEST: 一覧表示 / 詳細表示 / 検索 / 管理画面閲覧（更新不可）

### 3-3. 制約
- 作成者本人による承認は禁止
- マニュアル編集は作成者本人のみ
- 停止中ユーザーは操作不可
- 使用停止カテゴリは新規選択不可
- GUEST は DB保存を伴う操作を実行不可

### 3-4. パスワード運用ルール（2026-04-23）
- パスワードは保存時に `PasswordEncoder`（BCrypt）でハッシュ化する
- ログイン照合は Spring Security 標準フローに委譲する（手動照合しない）
- ユーザー新規作成時は初期パスワードを自動生成する
- 初期パスワードはフラッシュメッセージで一時表示し、一覧や詳細へ常時表示しない
- 初期パスワード通知は手動で閉じるまで表示し、通常メッセージの自動消去対象から除外する
- `passwordChangeRequired` が true のユーザーはログイン成功後に変更画面へ遷移させる
- パスワード変更完了後は `passwordChangeRequired` を false に戻す
---

## 4. ステータス仕様

### 4-1. ステータス一覧
- DRAFT
- PENDING
- APPROVED
- ARCHIVED

### 4-2. 許可遷移
- DRAFT → PENDING
- DRAFT → ARCHIVED
- PENDING → APPROVED
- PENDING → DRAFT
- PENDING → ARCHIVED
- APPROVED → ARCHIVED
- ARCHIVED → APPROVED

### 4-3. 日時ルール
- DRAFT: approvedAt は null
- PENDING: approvedAt は null
- APPROVED: approvedAt を保存する
- ARCHIVED: approvedAt は保持する

---

## 5. 業務ルール

### 5-1. マニュアル複製
- コピー対象
  - title
  - content
- リセット対象
  - id
  - createdAt
  - updatedAt
  - approvedAt
- 複製後の値
  - status = DRAFT/PENDING
  - operatedByUser = current user
  - category = selected category
  - changeNote = required
- 複製時は履歴を必ず1件保存する

### 5-2. 入力画面
- 新規作成は専用画面で行う
- 編集 / 複製は共通入力画面 `manual-form` を使う
- 共通入力画面は `edit` / `copy` の mode で切り替える
- 編集 / 複製では `マニュアルID / 作成日時 / 更新日時 / 作成者` を表示する
- 新規作成では changeNote を必須としない
- 入力画面の主操作は `下書きに保存` と `マニュアル公開` とする
- 入力画面内には複製ボタンを置かない
- 差し戻し / アーカイブ / 復帰は詳細画面のインライン入力で `changeNote` を入力してから確定する
- 承認は確認ダイアログで履歴コメント有無を選択し、必要な場合のみインライン入力を開いてよい
- `下書きに保存` では `PENDING → DRAFT` を許可する
- 復帰対象は `approvedAt` を保持した `ARCHIVED` マニュアルとする
- 画面統合と API 統合は分けて考える

### 5-4. ユーザー管理 / カテゴリ管理モード運用
- 管理画面フォームは `CREATE` / `EDIT` の mode で表示文脈を切り替える
- 初期表示は `CREATE` とする
- 一覧の設定操作から対象データを読み込み `EDIT` に切り替える
- `User` と `Category` は同じ mode 運用方針を採用する
- `userId` は更新時も変更可とする

### 5-5. カテゴリ重複確認フロー
- 同名カテゴリ検知時は即エラーにせず確認対象とする
- 新規作成時は `existsByCategoryName(...)` で判定する
- 更新時は対象IDを除外して `existsByCategoryNameAndIdNot(...)` で判定する
- 重複検知時のみ確認ダイアログ（confirm）を表示し、確認後に同導線で再送する

### 5-6. 管理画面の遷移/送信ルール（2026-04-22）
- 一覧から編集モードへ切り替える操作は GET で扱う
  - User: `GET /users/{userId}/action`
  - Category: `GET /categories/{categoryId}/action`
- 登録/更新/停止/復帰など DB 更新を伴う操作は POST で扱う
- `redirect:/...` は更新処理後に使用し、画面表示（同一リクエスト）はテンプレート名返却で扱う
- 管理画面フォームは `th:object` を起点に `th:field="*{...}"` を使う
- `th:object` 配下で `formDto.` を再指定しない（`${}` と `*{}` の混在を避ける）
- `targetUser` / `targetCategory` を参照する表示条件では null ガードを必須とする

### 5-3. ID 命名方針
- Entity 主キーは `id`
- コード上の引数や DTO では意味付き ID 名を使う
  - `manualId`
  - `categoryId`
  - `notificationId`

---

## 6. Entity 設計

### 6-1. 主要 Entity
- User
- Manual
- ManualHistory
- Category
- Notification
- UserOperationHistory

### 6-2. Manual
- 最新状態を保持する本体
- `status` `createdAt` `updatedAt` `approvedAt` は専用メソッドで更新する
- Entity 上は `user` を持ち、現在の JoinColumn は `operated_by_user_id`
- 画面 / DTO 上では `createdByUser` として扱ってよい
- 差し戻し判定用に `isRolledBack` を保持する
  - 新規作成時: `false`
  - 差し戻し時: `true`
  - 再申請時: `false`

### 6-3. ManualHistory
- 変更履歴を保持する append-only データ
- `manual`
- `changeNote`
- `changedAt`
- `changedByUser`

### 6-4. User
- `loginId`
- `displayName`
- `role`
- `isActive`
- `lastLoginAt`
- `passwordChangeRequired`

### 6-5. Category
- 部署名
- `displayOrder`
- `categoryName`
- `createdAt`
- `isActive`
- `updatedAt`
- 同名カテゴリは原則確認対象とする
- 同名カテゴリはカテゴリIDで区別する
- 名前更新時にマニュアル全文チェックは行わない

### 6-6. Notification
- 通知データ本体
- `targetUser`
- `manual`
- `type`
- `message`
- `isRead`
- `createdAt`
- 現時点では Entity は空実装で、今後拡張対象とする

### 6-7. UserOperationHistory
- 管理操作や監査用の履歴
- `targetUser`
- `operatedByUser`
- `operationType`
- `operationDetail`
- `createdAt`
- 監査ログは user_operation_histories に保存する
- 例: RESET_PASSWORD を記録し、操作者/対象/日時/理由を残す

---

## 7. Service 設計

### 7-1. ManualQueryService（読み込み系）
- 一覧取得
- マニュアル検索
- 詳細取得
- 履歴取得の窓口
- 画面表示用DTO組み立て

### 7-2. ManualCommandService（更新系）
- 新規作成
- 更新
- 編集
- 複製
- 申請
- 承認
- 差し戻し
- アーカイブ
- 復帰
- 更新系の権限チェックと状態遷移チェック
- 更新系の例外方針統一（成功/失敗メッセージ連携を含む）

### 7-3. ManualPermissionService（権限判定系）
- 実行可否判定を責務として扱う
  - `canEdit`
  - `canCopy`
  - `canApprove`
  - `canRollback`
  - `canArchive`
  - `canRestore`
- 共通判定
  - `isUserActive`
  - `isOwner`
  - `isApproverOrAdmin`
- Query / Command の双方から参照し、判定の重複実装を防ぐ

---

## 8. カテゴリ停止・上書き時の扱い
- 停止カテゴリに紐づくマニュアルは原則そのまま保持（非表示扱い）
- 同名カテゴリの上書きで再アクティブ化した場合は、旧カテゴリ配下のマニュアルを一括アーカイブする

### 7-4. ManualHistoryService
- 履歴保存
- 履歴一覧取得

### 7-5. NotificationService
- 承認者全員への通知作成
- 作成者への承認通知作成
- 作成者への差し戻し通知作成

### 7-6. MyPageService
- 通知一覧取得
- 自分の作成マニュアル取得
- 未承認マニュアル取得
- バッヂ表示情報作成
- 初回表示時に必要な一覧を全取得し、タブ切替は画面内で表示切替する

---

## 8. 通知仕様

### 8-1. 通知作成ルール
- submit 時: APPROVER 全員へ通知
- approve 時: 作成者へ通知
- rollback 時: 作成者へ通知

### 8-1A. 通知と一覧の役割分離
- 通知は未読件数の提示に限定する
- 一覧表示は状態条件で全件表示する
  - 差し戻し一覧: `isRolledBack = true`
  - 承認待ち一覧: `status = PENDING` かつ `createdByUser != current user`

### 8-1B. 通知既読運用
- 一覧表示だけでは既読化しない
- 既読機能は実装しない（通知削除運用で扱う）

### 8-1C. 承認時の通知削除
- 通知が既読もしくは通知確認が必要なくなったときに通知を全削除する
- 通知履歴は残さない

### 8-2. ホーム通知バッヂ
- 上段: 差し戻し通知件数
- 下段: 未承認通知件数

---

## 9. DTO 方針
- Request DTO と Response DTO を分ける
- List DTO / Detail DTO / Action DTO を使い分ける
- 一覧検索条件は `ManualSearchConditionDto` へまとめてよい
- `ManualDetailDto` は画面制御フラグを保持してよい
  - `canEdit`
  - `canPending`
  - `canApprove`
  - `canRollback`
  - `canArchive`
  - `canRestore`
  - `canCopy`
- DTO の null 設計は以下で統一する
  - List 項目: null を使わず空 List で返す
  - 件数項目: `int` を基本とし 0 を初期値にする
  - 必須項目: null を許可しない（不整合は早期検知する）
  - 任意項目: null を許可してよい
- Notification 表示用に以下を持つ
  - NotificationBadgeDto
  - NotificationItemDto
  - MyPageDto

---

## 10. Validation 方針
- DTO の形式チェックは `@Valid` + Bean Validation で行う
- 業務ルール依存の必須判定は Service で行う
- `changeNote` はケースごとに Service 側で必須判定してよい
- 画面ラベルは `更新履歴` と表示してよいが、内部項目名は `changeNote` のままでよい

## 11. クイックビュー件数取得方針
- 件数のみ必要な場合は一覧取得ではなく `countBy...` 系 Repository メソッドを使用する

## 12. ゲスト閲覧仕様
- ログイン画面に `閲覧専用でログイン` 導線を設ける
- GUEST は以下を許可
  - 一覧表示
  - 詳細表示
  - 検索
  - ユーザー管理画面表示（閲覧のみ）
  - カテゴリ管理画面表示（閲覧のみ）
- GUEST は以下を禁止
  - 新規作成
  - 編集
  - 削除
  - 申請
  - 承認
  - 差し戻し
  - アーカイブ
  - 復帰
  - カテゴリ管理の更新系操作
  - ユーザー管理の更新系操作
  - その他DB保存処理
- ログイン後に `ゲスト閲覧中` を表示する

## 13. UI 制御方針
- 権限に応じて操作ボタンの活性 / 非活性を制御する
- 非活性時は理由（例: `ログインすると利用できます` / `権限不足`）を表示する
- 機能の存在を明示するため、基本的にボタンは非表示ではなく非活性表示とする

---

## 14. 将来拡張予定
- ユーザー/カテゴリ連携
  - ユーザーとカテゴリの紐づけ管理
  - 担当カテゴリに応じた表示/操作制御
- 通知機能
  - 通知履歴の保存
  - 既読/未読の状態管理（現行は削除運用）
- マニュアル作成支援
  - 正式マニュアル作成支援機能（テンプレート/入力補助）
- マイページ
  - お気に入りマニュアル表示
  - 並び替え/絞り込み条件の追加
- 管理機能
  - ユーザー管理画面のユーザー検索機能
  - 監査ログ検索UI
  - 管理者向け一括操作
- テスト運用
  - JUnit + MockMvc による異常系自動テスト拡充
