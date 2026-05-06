# Portfolio Materials Guide

Version: 01.00.00
更新日: 2026-05-06

---

## 目的
本ディレクトリは、業務マニュアル管理システムを第三者に説明するための提出用資料を管理する。

---

## 推奨閲覧順
1. `01_system-overview.pdf`
   - システムの目的、利用技術、全体構成、設計ポイントを把握する資料。
2. `02_operation-guide.pdf`
   - ロール別の操作、画面構成、主要機能の使い方を確認する資料。
3. `../01_project-overview-and-basic-design.md`
   - プロジェクトの目的と基本設計を確認する資料。
4. `../02_system-specification-and-detailed-design.md`
   - 業務仕様、権限、状態遷移、詳細設計を確認する資料。
5. `../06_test-result.md`
   - 実施済みテストと品質確認結果を確認する資料。

---

## 公開環境
- URL: `https://manual-management-system-1.onrender.com`
- デモ用ログイン情報は機密情報として別管理する。
- 本番DBの接続情報、パスワード、初期データSQLは公開資料に含めない。

---

## 補足資料
- `../03_screen-design.md`
  - 画面遷移、画面ごとの表示項目、操作要件。
- `../04_api-design.md`
  - ControllerのURL、HTTPメソッド、DTO、例外設計。
- `../05_db-design.md`
  - テーブル定義、ER図作成時の基準情報。
- `../08_security-check.md`
  - セキュリティ確認観点と対応状況。
