# 02_system-specification-and-detailed-design.md

Version: 01.06.01  
更新日: 2026-04-08

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
  - createdByUser = current user
  - category = selected category
  - changeNote = required
- 複製時は履歴を必ず1件保存する

### 5-2. 入力画面
- 入力画面は `manual-form` に統一する
- 同じ画面を新規 / 編集 / 複製 / 差し戻し / アーカイブ / 復帰のモードで使い分ける
- 新規モードでは `マニュアルID / 作成日時 / 更新日時 / 作成者` を表示しない
- 編集モードと複製モードでは `マニュアルID / 作成日時 / 更新日時 / 作成者 / 履歴日時 / 履歴作成者 / ステータス` を表示する
- 入力画面の主操作は `下書きに保存` と `マニュアル公開` とする
- 入力画面内には複製ボタンを置かない
- 差し戻し / アーカイブ / 復帰は入力画面で `changeNote` を入力してから確定する
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
- Users
- Manual
- ManualHistory
- Category
- Notification

### 6-2. Manual
- 最新状態を保持する本体
- `status` `createdAt` `updatedAt` `approvedAt` は専用メソッドで更新する
- `createdByUser` を持つ

### 6-3. ManualHistory
- 変更履歴を保持する append-only データ
- `manual`
- `changeNote`
- `changedAt`
- `changedByUser`

### 6-4. Notification
- 通知データ本体
- `targetUser`
- `manual`
- `type`
- `message`
- `isRead`
- `createdAt`

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

---

## 8. 通知仕様

### 8-1. 通知作成ルール
- submit 時: APPROVER 全員へ通知
- approve 時: 作成者へ通知
- rollback 時: 作成者へ通知

### 8-2. ホーム通知バッヂ
- 上段: 差し戻し通知件数
- 下段: 未承認通知件数
- 承認通知はバッヂ表示対象外

---

## 9. DTO 方針
- Request DTO と Response DTO を分ける
- List DTO / Detail DTO / Action DTO を使い分ける
- Notification 表示用に以下を持つ
  - NotificationBadgeDto
  - NotificationItemDto
  - MyPageDto

---

## 10. Validation 方針
- DTO の形式チェックは `@Valid` + Bean Validation で行う
- 業務ルール依存の必須判定は Service で行う
- `changeNote` はケースごとに Service 側で必須判定してよい
