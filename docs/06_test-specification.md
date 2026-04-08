# 06_test-specification.md

Version: 01.03.01  
更新日: 2026-04-08

---

## 1. テスト方針

### 1-1. 基本方針
本システムでは、業務利用を前提として以下を重視して確認する。
- 正常系の動作
- 入力値の境界
- 権限制御
- ステータス遷移
- UI 表示制御
- DB 保存内容の整合性

### 1-2. 対象
- 画面テスト
- API テスト
- 権限 / 認証テスト
- バリデーションテスト
- DB 整合性テスト

---

## 2. テスト環境

### 2-1. 使用環境
- Spring Boot
- H2 Database
- Bootstrap + Thymeleaf
- ローカル環境

### 2-2. テストユーザー
| userId | role | 用途 |
| --- | --- | --- |
| user01 | USER | 一般利用者 |
| approver01 | APPROVER | 承認者 |
| admin01 | ADMIN | 管理者 |

### 2-3. テストカテゴリ
| categoryName | status |
| --- | --- |
| 営業部 | 使用中 |
| 経理部 | 使用中 |
| 旧営業部 | 使用停止 |

---

## 3. 共通観点

### 3-1. 正常系
- 登録
- 更新
- 一覧取得
- 詳細取得
- 検索
- ステータス変更

### 3-2. 異常系
- 必須項目未入力
- 文字数超過
- 不正なステータス遷移
- 権限不足
- 存在しないID
- 使用停止データ指定

### 3-3. UI
- メッセージ表示
- disabled 制御
- 別タブ導線
- レスポンシブ確認

### 3-4. DB 整合性
- FK 整合性
- approvedAt 制御
- createdAt 保存
- updatedAt 更新
- changeNote 履歴保存
- createdByUser 保存

---

## 4. DTO / Validation 観点
- `@NotBlank` / `@NotNull` / `@Size` の動作
- categoryId 未選択時のエラー
- 下書き保存時と申請時の必須差分
- changeNote のケース別必須判定
- 複製時の status 初期化確認
- rollback / archive / restore での changeNote 必須確認

---

## 5. マニュアル機能

### 5-1. 新規作成
| No | 種別 | テスト観点 | 期待結果 |
| --- | --- | --- | --- |
| MAN-001 | 正常 | title / content / category 入力 | DRAFT で保存される |
| MAN-002 | 異常 | category 未選択 | エラー表示 |
| MAN-003 | 異常 | title 100文字超過 | エラー表示 |
| MAN-004 | 正常 | 申請ボタン押下 | PENDING で保存される |

### 5-2. 更新
| No | 種別 | テスト観点 | 期待結果 |
| --- | --- | --- | --- |
| MAN-005 | 正常 | DRAFT 更新 | 更新成功 |
| MAN-006 | 正常 | changeNote 入力 | 履歴保存 |
| MAN-007 | 正常 | PENDING の下書きに保存 | DRAFT に戻せる |
| MAN-008 | 異常 | APPROVED 編集 | 業務ルールに従い制御 |

### 5-3. 一覧 / 詳細
| No | 種別 | テスト観点 | 期待結果 |
| --- | --- | --- | --- |
| MAN-009 | 正常 | 一覧取得 | 全件表示 |
| MAN-010 | 正常 | keyword 検索 | 条件一致のみ表示 |
| MAN-011 | 正常 | category 複数選択 + status 複数選択で絞り込み | 条件一致のみ表示 |
| MAN-012 | 正常 | 0件 | 結果0件表示 |
| MAN-013 | 正常 | 詳細の更新日時表示 | 時間まで表示される |

### 5-4. 複製
| No | 種別 | テスト観点 | 期待結果 |
| --- | --- | --- | --- |
| MAN-014 | 正常 | 複製実行 | DRAFT で新規作成 |
| MAN-015 | 正常 | approvedAt | null になる |
| MAN-016 | 正常 | changeNote | 履歴保存される |
| MAN-017 | 異常 | 使用停止カテゴリ | エラー |

---

## 6. ステータス遷移
| No | 種別 | テスト観点 | 期待結果 |
| --- | --- | --- | --- |
| ST-001 | 正常 | DRAFT → PENDING | 遷移成功 |
| ST-002 | 正常 | DRAFT → ARCHIVED | 遷移成功 |
| ST-003 | 正常 | PENDING → APPROVED | 遷移成功 |
| ST-004 | 正常 | PENDING → DRAFT | 差し戻しまたは下書き保存で成功 |
| ST-005 | 正常 | PENDING → ARCHIVED | 遷移成功 |
| ST-006 | 正常 | APPROVED → ARCHIVED | 遷移成功 |
| ST-007 | 正常 | ARCHIVED → APPROVED | 復帰成功 |
| ST-008 | 異常 | 不正な遷移 | エラー |

---

## 7. 権限
| No | 種別 | テスト観点 | 期待結果 |
| --- | --- | --- | --- |
| AUTH-001 | 正常 | USER が作成 | 実行可能 |
| AUTH-002 | 正常 | APPROVER が承認 | 実行可能 |
| AUTH-003 | 異常 | 作成者本人が承認 | 実行不可 |
| AUTH-004 | 異常 | USER が承認 | 実行不可 |
| AUTH-005 | 異常 | ADMIN 以外がユーザー管理 | 実行不可 |

---

## 8. 通知 / マイページ
| No | 種別 | テスト観点 | 期待結果 |
| --- | --- | --- | --- |
| NT-001 | 正常 | submit 時 | APPROVER 全員に通知作成 |
| NT-002 | 正常 | approve 時 | 作成者へ通知作成 |
| NT-003 | 正常 | rollback 時 | 作成者へ通知作成 |
| NT-004 | 正常 | ホームバッヂ | 上段=差し戻し、下段=未承認 |
| NT-005 | 正常 | マイページ初期表示 | 通知タブが開く |
| NT-006 | 正常 | APPROVER のマイページ | 未承認タブ表示 |

---

## 8-2. manual-form 経由の確定操作
| No | 種別 | テスト観点 | 期待結果 |
| --- | --- | --- | --- |
| FORM-001 | 正常 | rollback を入力画面で確定 | changeNote 入力後に DRAFT へ更新される |
| FORM-002 | 正常 | archive を入力画面で確定 | changeNote 入力後に ARCHIVED へ更新される |
| FORM-003 | 正常 | restore を入力画面で確定 | approvedAt を保持した ARCHIVED が APPROVED へ戻る |
| FORM-004 | 異常 | rollback で changeNote 未入力 | エラー表示 |
| FORM-005 | 異常 | archive で changeNote 未入力 | エラー表示 |
| FORM-006 | 異常 | restore で changeNote 未入力 | エラー表示 |

---

## 9. 管理機能

### 9-1. ユーザー管理
- 新規登録
- 更新
- 停止
- 復帰
- パスワードリセット
- 一覧ページ切り替え

### 9-2. カテゴリ管理
- 新規登録
- 更新
- 使用停止
- 復帰
- 一覧ページ切り替え

---

## 10. 補足
- 文字サイズや UI トーンの統一確認はモック確認時に実施する
- `manual-form` へ統合したため、旧 create / edit 画面の個別テストは不要
