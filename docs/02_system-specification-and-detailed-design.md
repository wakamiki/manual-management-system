# 02_system-specification-and-detailed-design.md

Version: 01.06.09  
更新日: 2026-04-14

---

## 1. 対象機能
- マニュアル一覧
- マニュアル詳細
- マニュアル入力
- 申請 / 承認 / 差し戻し / アーカイブ / 復帰
- マニュアル複製
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

### 2-1. Controller 方針
- 画面表示系は GET
- 更新処理系は POST
- Controller は thin controller 方針
- DTO 受け取りと Service 呼び出しに責務を限定する

---

## 3. 権限

### 3-1. ロール
- USER
- APPROVER
- ADMIN

### 3-2. 権限ルール
- USER: 作成 / 更新 / 申請
- APPROVER: 承認 / 差し戻し / アーカイブ
- ADMIN: ユーザー管理 / カテゴリ管理

### 3-3. 制約
- 作成者本人による承認は禁止
- 停止中ユーザーは操作不可
- 使用停止カテゴリは新規選択不可

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
  - status = DRAFT
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

### 6-4A. Category 補足
- 同名カテゴリは原則確認対象とする
- カテゴリコードを導入して同名の区別に使う
- 名前更新時にマニュアル全文チェックは行わない

### 6-5. Notification
- 通知データ本体
- `targetUser`
- `manual`
- `type`
- `message`
- `isRead`
- `createdAt`
- 現時点では Entity は空実装で、今後拡張対象とする

### 6-6. UserOperationHistory
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

### 7-1. ManualService
- 一覧取得
- 詳細取得
- 履歴取得の窓口
- 新規作成
- 更新
- 複製
- 申請
- 承認
- 差し戻し
- アーカイブ
- 復帰

---

## 8. カテゴリ停止・上書き時の扱い
- 停止カテゴリに紐づくマニュアルは原則そのまま保持（非表示扱い）
- 同名カテゴリの上書きで再アクティブ化した場合は、旧カテゴリ配下のマニュアルを一括アーカイブする

### 7-2. ManualHistoryService
- 履歴保存
- 履歴一覧取得

### 7-3. NotificationService
- 承認者全員への通知作成
- 作成者への承認通知作成
- 作成者への差し戻し通知作成

### 7-4. MyPageService
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
- 既読はユーザー操作（既読ボタン）で明示的に更新する

### 8-1C. 承認時の通知削除
- 承認完了時に対象マニュアルの `PENDING_APPROVAL` 通知を全削除する
- 通知履歴は残さない

### 8-2. ホーム通知バッヂ
- 上段: 差し戻し通知件数
- 下段: 未承認通知件数
- 承認通知はバッヂ表示対象外

---

## 9. DTO 方針
- Request DTO と Response DTO を分ける
- List DTO / Detail DTO / Action DTO を使い分ける
- 一覧検索条件は `ManualSearchConditionDto` へまとめてよい
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
