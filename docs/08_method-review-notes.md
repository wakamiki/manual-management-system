# 08_method-review-notes.md

更新日: 2026-04-13

---

## 目的
メソッド名・呼び出し位置・責務が分かりにくい箇所を洗い出し、将来の整理対象を明確化する。

---

## 気になる点（要整理候補）

### 1. MyPageDto の命名ゆれ
対象: `src/main/java/com/example/manual/dto/MyPageDto.java`
- `setPendeingManualList(...)` のスペルが誤り
  - 意図: `setPendingManualList(...)`
- `pendingManualList` と `rollbackManualList` の命名は OK

### 2. MyPageService のメソッド名
対象: `src/main/java/com/example/manual/service/MyPageService.java`
- `getPendingManual(...)` / `getRollbackManual(...)` は「一覧取得」なので
  - `getPendingManualList(...)`
  - `getRollbackManualList(...)`
  の方が意図が伝わりやすい
- `canGetMyPageData(...)` / `canGetPendingManual(...)` は
  - `validateMyPageAccess(...)`
  - `validatePendingViewAccess(...)`
  のような動詞にすると責務が明確

### 3. Manual Entity の命名ゆれ
対象: `src/main/java/com/example/manual/entity/Manual.java`
- `submitPENDING()` / `markStatusDRAFT()` の命名が不統一
  - 例: `submitPending()` / `markDraft()` のように統一した方が読みやすい

### 4. User Entity の役割不明なメソッド
対象: `src/main/java/com/example/manual/entity/User.java`
- `applyRole(...)` が static で新規 User を作成して role を返す形になっており用途が不明
  - 役割が「変更」なら instance メソッドに寄せる方が自然

### 5. Controller のパス重複
対象: `src/main/java/com/example/manual/controller/CategoryController.java`
- `@GetMapping` が同一パスで2つ存在
  - エンドポイントの重複で起動時エラー要因になる

---

## 参考（現状の混在は許容だが整理したい）
対象: `src/main/java/com/example/manual/service/ManualService.java`
- 一覧検索・状態変更・権限判定・DTO詰め替えが混在
- 将来的には
  - DTO詰め替えの切り出し
  - 状態遷移の整理
  - 検索専用 service への分離
  を検討

