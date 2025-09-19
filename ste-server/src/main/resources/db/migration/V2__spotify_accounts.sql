CREATE TABLE IF NOT EXISTS
  spotify_accounts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    spotify_user_id VARCHAR(64) NOT NULL,
    display_name VARCHAR(255),
    refresh_token TEXT NOT NULL,
    scope TEXT,
    token_obtained_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_in_seconds INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
  );

ALTER TABLE spotify_accounts
ADD CONSTRAINT fk_spotify_accounts_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS idx_spotify_accounts_spotify_user_id ON spotify_accounts (spotify_user_id);
