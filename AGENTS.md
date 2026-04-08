# AGENTS.md

# Project Overview

This project is a portfolio application for SCSK Nearshore Systems.

System name:
**Manual Management System**

Tech stack:
- Java 17
- Spring Boot
- Spring Data JPA
- Thymeleaf
- Bootstrap
- H2 Database
- PostgreSQL (future)
- Git / GitHub

Purpose:
Demonstrate business system development skills, design skills, maintainability, and explanation ability.

System concept:
A knowledge-sharing system for business manuals that are updated daily in real operations.

Main goals:
- Prevent knowledge silos
- Manage approval flow
- Keep change history
- Improve maintainability
- Make the design easy to explain in interviews

---

# Architecture Rules

Use the following layered structure:

- Controller
- Service
- Repository
- Entity
- DTO

Architecture:
**MVC + Service + Repository**

Responsibilities:

## Controller
- Keep thin
- Only receive requests / return responses
- No business logic

## Service
- Centralize all business logic
- Status transition rules
- Permission checks
- Validation flow
- Copy / archive / approval logic

## Repository
- Data access only
- Query methods
- Sorting / search

## Entity
- Keep entity consistency
- Prevent invalid state changes
- Use dedicated methods for protected fields

---

# Entity Rules

## Allowed class-level @Getter
Use `@Getter` at class level.

## Setter policy

Simple setter is allowed only for simple text fields.

Examples:
- title
- content
- displayName
- categoryName
- changeNote

Simple setter is NOT allowed for protected business fields.

Protected fields:
- status
- role
- isActive
- createdAt
- updatedAt
- approvedAt
- lastLoginAt

Use dedicated methods instead.

Examples:
- markCreatedNow()
- markUpdatedNow()
- markApprovedNow()
- clearApprovedAt()
- submit()
- approve()
- archive()
- restore()

---

# Business Rules

## Manual Status Flow

Allowed transitions:

- DRAFT → PENDING
- DRAFT → ARCHIVED
- PENDING → APPROVED
- PENDING → DRAFT
- PENDING → ARCHIVED
- APPROVED → ARCHIVED
- ARCHIVED → APPROVED

Never allow invalid transitions.

---

## Permission Rules

Roles:
- USER
- APPROVER
- ADMIN

Rules:
- USER: create / edit / submit
- APPROVER: approve / rollback / archive
- ADMIN: user / category management

Creator must not approve their own manual.

---

# Copy Feature Rules

Manual copy must follow these rules:

Copied fields:
- title
- content

Reset fields:
- id
- createdAt
- updatedAt
- approvedAt

New values:
- status = DRAFT
- createdByUser = current user
- category = selected category
- changeNote = required

Always save history record when copied.

---

# Screen Rules

## Top Page List Display

Top page accordion list should always display:

- manualId
- categoryName
- title
- content
- status
- history date
- updatedAt
- updatedByUser
- changeNote
- createdByUser

Because the list is shown inside an accordion, keeping more information visible is allowed.

## Manual Detail Screen

Display:

- manualId
- categoryName
- title
- content
- status
- createdAt
- updatedAt
- createdByUser
- history list
- history changedAt
- history changeNote
- history changedByUser

Important datetime rule:

- `createdAt` may be displayed without time
- `updatedAt` should display time because ordering within the same day must be understandable

## Manual Form Screen

The input screen is unified as:

- `manual-form`

Modes:

- create
- edit
- copy
- rollback
- archive
- restore

Display rules:

- In create mode, helper information may be hidden
- In edit / copy mode, show:
  - manualId
  - createdAt
  - updatedAt
  - createdByUser
  - history changedAt
  - history changedByUser
  - status

Button rules:

- `下書きに保存`
- `マニュアル公開`
- `承認`
- `差し戻し`
- `アーカイブ`
- `復帰`

Do not place a copy button inside `manual-form`.

Rollback / archive / restore should be finalized from the input screen after entering `changeNote`.

Special note:

- `下書きに保存` may move `PENDING → DRAFT`
- This is allowed when the creator needs to reopen and revise unpublished content

Restore rule:

- Restore target is an archived manual that still has `approvedAt`

# Coding Style Rules

Very important:

Do NOT output full implementation immediately.

Always respond in this order:

1. Current feature
2. Feature to build
3. Files to edit
4. Design points
5. Task breakdown
6. Hints

Focus on:
- self implementation skill
- maintainability
- reviewability
- easy explanation in interview

Prefer hints and design guidance over full code.

Only output minimal code when explicitly requested.

---

# Frontend Development Order

Always follow this order:

1. Static HTML mock
2. Bootstrap layout
3. Thymeleaf conversion
4. Backend integration

Avoid generating full HTTP communication code unless explicitly requested.

---

# Documentation Priority

Always prioritize these files:

1. docs/
2. README.md
3. AGENTS.md

If specifications conflict,
**docs are the source of truth**

---

# Current Development Priority

1. Login / Authorization
2. Manual list + detail
3. Create + update
4. Submit + approve + rollback
5. Category management
6. Notification / logs
7. Dashboard

Current phase:
**Entity maintenance and business logic design**
