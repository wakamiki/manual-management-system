-- Manual Management System
-- V1: create tables for PostgreSQL (manual execution)

BEGIN;

CREATE TABLE IF NOT EXISTS categories (
  id BIGSERIAL PRIMARY KEY,
  display_order INTEGER NOT NULL,
  category_name VARCHAR(50) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  is_active BOOLEAN NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  login_id VARCHAR(50) NOT NULL,
  password VARCHAR(255) NOT NULL,
  display_name VARCHAR(50) NOT NULL,
  role VARCHAR(20) NOT NULL,
  is_active BOOLEAN NOT NULL,
  last_login_at TIMESTAMP NULL,
  created_at TIMESTAMP NULL,
  updated_at TIMESTAMP NULL,
  password_change_required BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS manuals (
  id BIGSERIAL PRIMARY KEY,
  category_id BIGINT NULL,
  operated_by_user_id BIGINT NULL,
  title VARCHAR(100) NOT NULL,
  content VARCHAR(10000) NOT NULL,
  status VARCHAR(20) NULL,
  created_at TIMESTAMP NULL,
  updated_at TIMESTAMP NULL,
  approved_at TIMESTAMP NULL,
  is_rolled_back BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS manual_histories (
  id BIGSERIAL PRIMARY KEY,
  manual_id BIGINT NULL,
  change_note VARCHAR(100) NOT NULL,
  changed_at TIMESTAMP NOT NULL,
  change_user_id BIGINT NULL
);

CREATE TABLE IF NOT EXISTS user_operation_histories (
  id BIGSERIAL PRIMARY KEY,
  target_user_id BIGINT NULL,
  operated_by_user_id BIGINT NULL,
  operation_type VARCHAR(30) NOT NULL,
  operation_detail VARCHAR(100) NOT NULL,
  created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS notifications (
  id BIGSERIAL PRIMARY KEY,
  target_user_id BIGINT NULL,
  manual_id BIGINT NULL,
  type VARCHAR(30) NULL,
  message TEXT NULL,
  created_at TIMESTAMP NULL
);

ALTER TABLE manuals
  ADD CONSTRAINT fk_manuals_category
    FOREIGN KEY (category_id) REFERENCES categories(id);

ALTER TABLE manuals
  ADD CONSTRAINT fk_manuals_operated_by_user
    FOREIGN KEY (operated_by_user_id) REFERENCES users(id);

ALTER TABLE manual_histories
  ADD CONSTRAINT fk_manual_histories_manual
    FOREIGN KEY (manual_id) REFERENCES manuals(id);

ALTER TABLE manual_histories
  ADD CONSTRAINT fk_manual_histories_change_user
    FOREIGN KEY (change_user_id) REFERENCES users(id);

ALTER TABLE user_operation_histories
  ADD CONSTRAINT fk_user_operation_histories_target_user
    FOREIGN KEY (target_user_id) REFERENCES users(id);

ALTER TABLE user_operation_histories
  ADD CONSTRAINT fk_user_operation_histories_operated_by_user
    FOREIGN KEY (operated_by_user_id) REFERENCES users(id);

ALTER TABLE notifications
  ADD CONSTRAINT fk_notifications_target_user
    FOREIGN KEY (target_user_id) REFERENCES users(id);

ALTER TABLE notifications
  ADD CONSTRAINT fk_notifications_manual
    FOREIGN KEY (manual_id) REFERENCES manuals(id);

COMMIT;
