# 06_test-specification.md

Version: 01.03.15  
更新日: 2026-04-25

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
|guest01|GUEST|ゲストユーザー|

### 2-3. テストカテゴリ
| categoryName | status |
| --- | --- |
| 営業部 | 使用中 |
| 経理部 | 使用中 |
|総務部|使用中|
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
- アコーディオン展開/折りたたみ挙動
- 検索条件の再表示（値保持）
- 管理画面の mode 切替表示（CREATE / EDIT）
- 更新系ボタンの表示条件（targetUser / targetCategory の null ガード）

### 3-4. DB 整合性
- FK 整合性
- approvedAt 制御
- createdAt 保存
- updatedAt 更新
- changeNote 履歴保存
- createdByUser 保存
- `is_rolled_back` と Entity `isRolledBack` の整合

---

## 4. DTO / Validation 観点
- `@NotBlank` / `@NotNull` / `@Size` の動作
- categoryId 未選択時のエラー
- 下書き保存時と申請時の必須差分
- changeNote のケース別必須判定
- 画面ラベル `更新履歴` と内部項目 `changeNote` の対応確認
- 複製時の status 初期化確認
- rollback / archive / restore での changeNote 必須確認
- DTO null 設計ルール確認
  - List が null ではなく空 List で返る
  - 件数が null ではなく 0 で返る
  - 必須項目が null の場合に異常として検知できる

---

## 5. マニュアル機能

### 5-1. 新規作成
| No | 種別 | テスト観点 | 期待結果 |
| --- | --- | --- | --- |
| MAN-001 | 正常 | title / content / category 入力 | DRAFT で保存される |
| MAN-002 | 異常 | category 未選択 | エラー表示 |
| MAN-003 | 異常 | title 100文字超過 | エラー表示 |
| MAN-004 | 正常 | 公開ボタン押下 | PENDING で保存される |
| MAN-005 | 異常 | title 未選択 | エラー表示 |
| MAN-006 | 正常 | 下書き保存ボタン押下 | DRAFT で保存される |
| MAN-007 | 正常 | カテゴリ選択 | アクティブカテゴリのみ表示 |

### 5-2. 更新
| No | 種別 | テスト観点 | 期待結果 |
| --- | --- | --- | --- |
| MAN-008 | 正常 | DRAFT 更新 | 更新成功 |
| MAN-009 | 正常 | changeNote 入力 | 履歴保存 |
| MAN-010 | 正常 | PENDING の下書きに保存 | DRAFT に戻せる |
| MAN-011 | 異常 | APPROVED 編集 | 業務ルールに従い制御(編集出来ない) |

### 5-3. 一覧 / 詳細
| No | 種別 | テスト観点 | 期待結果 |
| --- | --- | --- | --- |
| MAN-012 | 正常 | 一覧取得 | PENDING/APPROVED全件表示 |
| MAN-013 | 正常 | keyword 検索 | 本文title内条件一致のみ表示 |
| MAN-014 | 正常 | category 複数選択 + status 複数選択で絞り込み | 条件一致のみ表示 |
| MAN-015 | 正常 | 0件 | 結果0件表示 |
| MAN-016 | 正常 | 詳細の更新日時表示 | 時間まで表示される |
| MAN-016A | 正常 | 初期表示の status 条件 | PENDING / APPROVED のみ選択済で表示される |
| MAN-016B | 正常 | 一覧の常時表示項目 | manualId / title / status / updatedAt / createdByUser / category が表示される |
| MAN-016C | 正常 | 一覧のアコーディオン展開表示 | content / 更新履歴 が表示される |
| MAN-016D | 正常 | カテゴリ複数選択 | 営業部 / 経理部 / 総務部 を複数同時に絞り込める |
| MAN-016E | 正常 | 使用停止カテゴリ選択 | `使用停止中` 展開後に旧カテゴリで絞り込める |
| MAN-016F | 正常 | ステータス選択肢 | `すべて` を使わず複数選択で絞り込める |
| MAN-016G | 正常 | status 初期条件 | 初期表示は `PENDING` / `APPROVED` のみ対象 |
| MAN-016H | 正常 | keyword 値保持 | 検索後に入力値がフォームへ再表示される |
| MAN-016I | 正常 | categoryIds 値保持 | 検索後にカテゴリチェック状態が保持される |
| MAN-016J | 正常 | statuses 値保持 | 検索後にステータスチェック状態が保持される |
| MAN-016K | 正常 | アコーディオンID一意 | 複数行で各行が個別に開閉できる |
| MAN-016L | 異常 | 表示データnull | Model 未設定時に EL エラーを検知できる |

### 5-4. 複製
| No | 種別 | テスト観点 | 期待結果 |
| --- | --- | --- | --- |
| MAN-017 | 正常 | 複製実行 | DRAFT で新規作成 |
| MAN-018 | 正常 | approvedAt | null になる |
| MAN-019 | 正常 | changeNote | 履歴保存される |
| MAN-020 | 異常 | 使用停止カテゴリ | エラー |
| MAN-021 | 正常 | 複製マニュアル公開 | PENDINGで新規作成 |

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
| AUTH-005 | 正常 | ADMIN/GUEST がユーザー管理画面表示 | 実行可能（GUESTは閲覧のみ） |
| AUTH-006 | 正常 | GUEST の一覧/詳細/検索 | 閲覧操作のみ実行可能 |
| AUTH-007 | 異常 | GUEST の更新系操作 | 非活性または拒否される |
| AUTH-008 | 正常 | 本人がパスワード変更 | 実行可能 |
| AUTH-009 | 異常 | 本人以外のパスワード変更 | 実行不可 |
| AUTH-010 | 正常 | ADMIN が他ユーザーのパスワード初期化 | 実行可能 |
| AUTH-011 | 異常 | ADMIN 以外のパスワード初期化 | 実行不可 |

### 7-2. 認可ルール対応（docs/04 2-2B 同期）
| No | 種別 | テスト観点 | 期待結果 |
| --- | --- | --- | --- |
| AUTH-012 | 正常 | Manual 編集（作成者 + DRAFT） | 実行可能 |
| AUTH-013 | 正常 | Manual 編集（作成者 + PENDING） | 実行可能 |
| AUTH-014 | 異常 | Manual 編集（非作成者） | 実行不可 |
| AUTH-015 | 正常 | Manual 複製（PENDING / APPROVED / ARCHIVED） | 実行可能 |
| AUTH-016 | 異常 | Manual 複製（DRAFT） | 実行不可（編集で対応） |
| AUTH-017 | 正常 | Manual 公開（作成者 + DRAFT） | 実行可能 |
| AUTH-018 | 異常 | Manual 公開（非作成者 または DRAFT以外） | 実行不可 |
| AUTH-019 | 正常 | Manual 承認（ADMIN/APPROVER + 非作成者 + PENDING） | 実行可能 |
| AUTH-020 | 異常 | Manual 承認（作成者本人） | 実行不可 |
| AUTH-021 | 異常 | Manual 承認（PENDING以外） | 実行不可 |
| AUTH-022 | 正常 | Manual 差し戻し（ADMIN/APPROVER + 非作成者 + PENDING） | 実行可能（更新履歴必須） |
| AUTH-023 | 正常 | Manual アーカイブ（ADMIN/APPROVER） | 対象statusで実行可能（更新履歴必須） |
| AUTH-024 | 正常 | Manual 復帰（ADMIN/APPROVER + ARCHIVED） | カテゴリ有効時に実行可能（更新履歴必須） |
| AUTH-025 | 異常 | 無効ユーザー（isActive=false）の操作 | 全操作拒否 |
| AUTH-026 | 正常 | Category 管理画面表示（ADMIN） | 実行可能 |
| AUTH-027 | 正常 | Category 管理画面表示（GUEST） | 実行可能（閲覧のみ） |
| AUTH-028 | 正常 | Category 作成/更新/停止/復帰（ADMIN） | 実行可能 |
| AUTH-029 | 異常 | Category 作成/更新/停止/復帰（ADMIN以外） | 実行不可 |
| AUTH-030 | 正常 | USER/APPROVER の権限外操作UI | ボタン非表示 |
| AUTH-031 | 正常 | GUEST のユーザー管理画面表示制御 | `userId` / `lastLoginAt` / `操作` 列が非表示、更新系ボタンは非活性 |
| AUTH-032 | 正常 | ゲストログイン（環境変数設定あり） | `POST /login/guest` でログインし `/manuals/index` へ遷移する |
| AUTH-033 | 異常 | ゲストログイン（環境変数未設定/不正） | `/login` に戻りエラーメッセージが表示される |

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
| NT-007 | 正常 | マイページの初回取得 | 必要データが一括取得される |
| NT-008 | 正常 | 通知件数 | 未読のみがカウントされる |
| NT-009 | 正常 | 差し戻し一覧 | `isRolledBack = true` の全件が表示される |
| NT-010 | 正常 | 承認待ち一覧 | `PENDING` かつ自分以外の全件が表示される |
| NT-011 | 正常 | 通知の既読化 | 既読ボタン操作で既読になる |
| NT-012 | 正常 | 承認時の通知削除 | 対象マニュアルの `PENDING_APPROVAL` 通知が全削除される |

---

## 8-2. 詳細画面インライン入力の確定操作
| No | 種別 | テスト観点 | 期待結果 |
| --- | --- | --- | --- |
| FORM-001 | 正常 | rollback を詳細画面のインライン入力で確定 | changeNote 入力後に DRAFT へ更新される |
| FORM-002 | 正常 | archive を詳細画面のインライン入力で確定 | changeNote 入力後に ARCHIVED へ更新される |
| FORM-003 | 正常 | restore を詳細画面のインライン入力で確定 | approvedAt を保持した ARCHIVED が APPROVED へ戻る |
| FORM-004 | 正常 | approve で確認ダイアログ `はい` | インライン入力が開き、履歴コメント付きで承認できる |
| FORM-005 | 正常 | approve で確認ダイアログ `いいえ` | コメントなしで承認できる |
| FORM-006 | 異常 | rollback で changeNote 未入力 | エラー表示 |
| FORM-007 | 異常 | archive で changeNote 未入力 | エラー表示 |
| FORM-008 | 異常 | restore で changeNote 未入力 | エラー表示 |

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

### 9-3. 管理画面フォーム連携（2026-04-22）
| No | 種別 | テスト観点 | 期待結果 |
| --- | --- | --- | --- |
| ADM-001 | 正常 | User 一覧の設定ボタン（GET） | `EDIT` モードで user-management が表示される |
| ADM-002 | 正常 | Category 一覧の更新ボタン（GET） | `EDIT` モードで category-management が表示される |
| ADM-003 | 異常 | 更新系URLへ GET 送信 | 405 を検知し、リンク/ボタン定義の不一致を発見できる |
| ADM-004 | 正常 | `th:object` + `th:field="*{...}"` のバインド | 入力値が DTO に正常バインドされる |
| ADM-005 | 異常 | `th:field="${...}"` 記述 | テンプレートエラーとして検知できる |
| ADM-006 | 異常 | `target...` が null の状態で更新系ボタン表示 | null ガードにより表示されない |
| ADM-007 | 異常 | パス不一致（`th:formaction` と `@PostMapping` 不一致） | 400/404/405 として検知できる |

### 9-4. パスワード変更（2026-04-23）
| No | 種別 | テスト観点 | 期待結果 |
| --- | --- | --- | --- |
| PWD-001 | 正常 | 初期パスワード発行時 | ハッシュ値で保存され、平文は保存されない |
| PWD-002 | 正常 | `passwordChangeRequired=true` でログイン | 変更画面へ遷移する |
| PWD-003 | 正常 | パスワード変更成功 | `passwordChangeRequired=false` へ更新される |
| PWD-004 | 異常 | 新規/確認パスワード不一致 | エラー表示され、更新されない |
| PWD-005 | 異常 | 許可文字外の入力 | バリデーションエラー表示 |
| PWD-006 | 異常 | 文字種要件未達（大文字/小文字/数字不足） | バリデーションエラー表示 |
| PWD-007 | 正常 | 初期パスワード通知表示 | 自動消去されず手動で閉じるまで表示される |
| PWD-008 | 正常 | 初期パスワードコピー操作 | クリップボードへコピーされる |
| PWD-009 | 正常 | パスワード変更画面URL遷移（本人） | `GET /users/change-password` で画面表示される |
| PWD-010 | 正常 | パスワード変更実行URL（本人） | `POST /users/action/change-password` で更新される |
| PWD-011 | 正常 | パスワード初期化実行URL（管理者） | `POST /users/{userId}/reset-password` で初期化される |
| PWD-012 | 正常 | 初期化後の再変更確認 | 初期化PWでログイン後に新PWへ変更でき、次回ログインは新PWのみ成功する |

---

## 10. 補足
- 文字サイズや UI トーンの統一確認はモック確認時に実施する
- 新規作成は専用画面、編集 / 複製は `manual-form` を使う
- ルーティング不整合（`/` と `/manuals/index`）はログとアクセスURLをセットで確認する
- Thymeleaf 例外時はテンプレート修正前に Controller と DTO 中身の確認を優先する
- 405 発生時は `a(th:href)` が GET、`button(type=submit + th:formaction)` が POST である点を先に確認する
