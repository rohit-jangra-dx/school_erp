ALTER TABLE users
ADD COLUMN username VARCHAR(255) NOT NULL;

CREATE TABLE IF NOT EXISTS auth_accounts (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    failed_attempts INTEGER NOT NULL DEFAULT 0,
    locked_until TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_auth_accounts_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);
