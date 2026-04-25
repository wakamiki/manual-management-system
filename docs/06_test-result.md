# 06_test-result.md

---

## 1. テスト実施情報

- 実施日（2026-04-25）: 
- 実施者: miki
- 対象機能: 認証 / ユーザー管理 / カテゴリ管理 / マイページ
- 実行環境（local / H2 / PostgreSQL など）: local / H2
- 対象コミット: 

---

## 2. テスト結果一覧

| No | テストID | テスト観点 | 期待結果 | 実際結果 | 判定（PASS/FAIL/BLOCK） | 備考 |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | PWD-008 | 初期パスワードコピー操作 | クリップボードへコピーされる | コピー成功 | PASS | 新規承認ユーザー作成時に確認 |
| 2 | PWD-009 | `GET /users/change-password` | 変更画面が表示される | 初期パスワード発行後に画面遷移成功 | PASS | |
| 3 | AUTH-006 | GUESTの閲覧操作 | 閲覧操作のみ実行可能 | マイページ遷移・閲覧可能 | PASS | 通知/作成済みタブのみ表示 |
| 4 | AUTH-007 | GUESTの更新系操作 | 非活性または拒否される | 変更ボタン非活性、作成ボタン非活性 | PASS | パスワード変更/ユーザー作成で確認 |
| 5 | AUTH-031 | GUEST権限外UI制御 | 非活性表示 + 理由表示 | ユーザー管理の新規作成ボタン非活性 | PASS | |
| 6 | NT-005 | マイページ初期表示 | 通知タブが開く | 正常表示 | PASS | |
| 7 | NT-006 | APPROVER/ADMIN向けタブ表示 | 未承認タブ表示 | GUESTでは未承認タブ非表示 | PASS | 要件どおり |
| 8 | PWD-010 | `POST /users/action/change-password` | 更新成功 | 更新自体は成功するが成功アラートが消えない | FAIL | 自動消去不具合 |
| 9 | ADM-002 | Category更新モード表示 | `EDIT`モードで表示される | 表示はできるが条件不整合で一部エラー発生履歴あり | PASS | `isActive`不整合は修正済み |
| 10 | ADM-006 | nullガード / ボタン表示条件 | 不正表示されない | `or`条件により誤表示したが修正済み | PASS | `or`→`and` |
| 11 | 9-2(カテゴリ管理) | カテゴリ新規作成 | 正常作成 | 正常作成 | PASS | |
| 12 | 9-2(カテゴリ管理) | displayOrder割り込み（新規） | 順序繰り下げが正しく動く | 正常 | PASS | |
| 13 | 9-2(カテゴリ管理) | displayOrder割り込み（更新） | 順序再配置が正しく動く | 同一displayOrderが発生 | FAIL | `shiftDownOrderNumbers` 実装要修正 |
| 14 | ADM-004 | `th:object` + `th:field` バインド | 入力値がDTOにバインド | `id`未バインドでモード崩れ発生→修正後正常 | PASS | `id`に`th:field`追加 |
| 15 | UI-001（追加観点） | 成功アラート自動消去 | 3秒後に消える | password-changeのみ消えない | FAIL | categoryは正常に消える |
| 16 | AUTH-005 | ADMIN以外のユーザー管理画面アクセス | 実行不可 | GUESTは閲覧可に変更して運用 | FAIL | **仕様変更のため06_test-specification側見直し要** |
| 17 | AUTH-027 | ADMIN以外のカテゴリ管理画面アクセス | 実行不可 | GUESTは閲覧可に変更して運用 | FAIL | **仕様変更のため06_test-specification側見直し要** |
| 18 | 9-2(カテゴリ管理) | GUESTでカテゴリ管理閲覧 | 閲覧可（今回要件） | データ不足で未確認 | BLOCK | adminでデータ投入後再試験 |

---

## 3. 失敗ケース一覧（FAILのみ）

| No | テストID | 事象 | 原因仮説 | 対応方針 |
| --- | --- | --- | --- | --- |
| 1 | PWD-010 / UI-001 | パスワード変更成功アラートが3秒で消えない | `password-change.html` の自動消去対象IDやスクリプト適用条件不整合 | `flashMessage` のid/条件とJS対象を再確認し統一 |
| 2 | 9-2(カテゴリ管理) | カテゴリ更新時のdisplayOrder割り込みで重複順序発生 | `shiftDownOrderNumbers` が `-1` ではなく `+1` 更新になっている | `shiftDownOrderNumbers` の更新方向修正後、再テスト実施 |
| 3 | AUTH-005 | 仕様書ではADMIN以外NGだが、実装はGUEST閲覧可 | 要件変更が06_test-specificationへ未反映 | 06_test-specificationの権限期待値を最新仕様へ更新 |
| 4 | AUTH-027 | 仕様書ではADMIN以外NGだが、実装はGUEST閲覧可 | 要件変更が06_test-specificationへ未反映 | 06_test-specificationの権限期待値を最新仕様へ更新 |

---

## 4. 明日実施するテスト（優先順）

- 優先A（不具合修正確認）
  - PWD-010 / UI-001: password-change成功アラート自動消去
  - 9-2(カテゴリ管理): `shiftDownOrderNumbers` 修正後のdisplayOrder再配置

- 優先A（仕様差分解消）
  - AUTH-005 / AUTH-027: GUEST閲覧可の仕様を06_test-specificationへ反映後、期待値再判定

- 優先B（未実施回収）
  - GUESTでカテゴリ管理画面閲覧（データ投入後）

---

## 5. 改修メモ（任意）

- `return "redirect:manuals/index";` は相対パスになりリダイレクト頻発を誘発  
  - `return "redirect:/manuals/index";` に修正済み
- カテゴリ変更モードの `id` バインド不足（`th:field`なし）で `CREATE` モードへ崩れていた  
  - hidden `id` を送信するよう修正済み

---

## 6. 次回への申し送り

- GUESTは「操作不可・閲覧可（ユーザー管理/カテゴリ管理）」が最新要件
- ユーザー管理画面のGUEST表示は `userId / lastLoginAt / 操作` の3列非表示で確定
- GUESTのパスワード変更は本来不可想定だが、初期パスワード導線の都合で画面遷移は発生  
  - 今回は初回対応として非活性で運用

