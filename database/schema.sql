-- JavaChatApp Database Schema
-- Run with: sqlite3 chat.db < schema.sql

CREATE TABLE IF NOT EXISTS users (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    username     TEXT    NOT NULL UNIQUE,
    password_hash TEXT   NOT NULL,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_seen    DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS messages (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    sender     TEXT NOT NULL,
    recipient  TEXT,                       -- NULL = public broadcast
    content    TEXT NOT NULL,
    type       TEXT NOT NULL DEFAULT 'PUBLIC',
    sent_at    DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Sample data
INSERT OR IGNORE INTO users (username, password_hash)
VALUES ('admin', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3');

-- Index for faster message history queries
CREATE INDEX IF NOT EXISTS idx_messages_sender   ON messages(sender);
CREATE INDEX IF NOT EXISTS idx_messages_sent_at  ON messages(sent_at);
