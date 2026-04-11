# Thymeleaf 反映項目メモ（モックHTML準拠）

このファイルは `static/mock` のHTMLに合わせて、Thymeleafで差し替える候補の「項目名」を整理したものです。
実装のDTO名・フィールド名は最終的にコード側に合わせて調整してください。

## 共通（ログインユーザー）
- `loginUserDisplayName`
- `loginUserRole`
- `rollbackNoticeCount`
- `pendingNoticeCount`

## ホーム（index）
- `keyword`
- `categoryIds`
- `statuses`
- `activeCategories`
- `inactiveCategories`
- `quickViewMyCreatedCount`
- `quickViewPendingCount`
- `quickViewRecentUpdatedCount`
- `manuals`
- `manual.manualId`
- `manual.title`
- `manual.categoryName`
- `manual.status`
- `manual.content`
- `manual.updatedAt`
- `manual.createdByName`
- `manual.histories`
- `history.changedAt`
- `history.changeNote`

## ログイン
- `userId`
- `password`
- `loginErrorMessage`

## マニュアル新規作成（manual-create）
- `manualInput.title`
- `manualInput.content`
- `manualInput.categoryId`
- `categories`

## マニュアル編集/複製（manual-form）
- `mode`
- `badgeLabel`
- `manualInput.manualId`
- `manualInput.title`
- `manualInput.content`
- `manualInput.categoryId`
- `manualInput.changeNote`
- `manualInput.createdAt`
- `manualInput.updatedAt`
- `manualInput.createdByName`
- `categories`

## マニュアル詳細（manual-detail）
- `manual.manualId`
- `manual.title`
- `manual.categoryName`
- `manual.status`
- `manual.content`
- `manual.createdAt`
- `manual.updatedAt`
- `manual.createdByName`
- `manual.histories`
- `history.changedAt`
- `history.changedByName`
- `history.changeNote`
- `inlineChangeNote`

## マイページ（my-page）
- `loginUserRole`
- `loginUserDisplayName`
- `rollbackNoticeCount`
- `pendingNoticeCount`
- `notifications`
- `notification.type`
- `notification.changedAt`
- `notification.manualId`
- `notification.title`
- `notification.message`
- `createdManuals`
- `created.manualId`
- `created.title`
- `created.status`
- `created.updatedAt`
- `pendingManuals`
- `pending.manualId`
- `pending.title`
- `pending.createdByName`

## ユーザー管理（user-management）
- `userInput.userId`
- `userInput.displayName`
- `userInput.role`
- `users`
- `user.userId`
- `user.displayName`
- `user.role`
- `user.status`
- `user.lastLoginAt`
- `userCount`

## カテゴリ管理（category-management）
- `categoryInput.categoryName`
- `categoryInput.displayOrder`
- `categories`
- `category.categoryName`
- `category.displayOrder`
- `category.status`
