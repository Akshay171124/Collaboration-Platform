-- V1__init.sql (PostgreSQL)

-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =========================
-- USERS
-- =========================
CREATE TABLE users (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email         VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  full_name     VARCHAR(255) NOT NULL,
  created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- =========================
-- WORKSPACES
-- =========================
CREATE TABLE workspaces (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name        VARCHAR(255) NOT NULL,
  created_by  UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =========================
-- WORKSPACE MEMBERS
-- =========================
CREATE TABLE workspace_members (
  workspace_id UUID NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
  user_id      UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  role         VARCHAR(20) NOT NULL,
  joined_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (workspace_id, user_id),
  CONSTRAINT ck_workspace_members_role CHECK (role IN ('OWNER','ADMIN','EDITOR','VIEWER'))
);

CREATE INDEX idx_workspace_members_user_id ON workspace_members(user_id);

-- =========================
-- PAGES
-- =========================
CREATE TABLE pages (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  workspace_id UUID NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
  title        VARCHAR(255) NOT NULL,
  created_by   UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
  created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_pages_workspace_updated ON pages(workspace_id, updated_at DESC);

-- =========================
-- BLOCKS
-- Ordered by numeric position for MVP
-- =========================
CREATE TABLE blocks (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  page_id     UUID NOT NULL REFERENCES pages(id) ON DELETE CASCADE,
  type        VARCHAR(30) NOT NULL,
  position    NUMERIC(40,20) NOT NULL,
  content     TEXT NOT NULL,
  created_by  UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT ck_blocks_type CHECK (type IN ('PARAGRAPH','HEADING','TODO','BULLET','CODE'))
);

CREATE INDEX idx_blocks_page_position ON blocks(page_id, position);

-- Optional uniqueness. Can be removed if you ever hit collisions.
-- With midpoint logic it's extremely unlikely for MVP.
CREATE UNIQUE INDEX uq_blocks_page_position ON blocks(page_id, position);
