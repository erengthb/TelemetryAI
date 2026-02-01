CREATE TABLE request_logs (
    id BIGSERIAL PRIMARY KEY,
    ts TIMESTAMPTZ NOT NULL DEFAULT now(),
    method TEXT NOT NULL,
    path TEXT NOT NULL,
    query TEXT,
    status INT NOT NULL,
    duration_ms INT NOT NULL,
    user_id UUID,
    ip TEXT,
    user_agent TEXT,
    error TEXT
);

CREATE INDEX idx_request_logs_ts ON request_logs (ts);
CREATE INDEX idx_request_logs_status ON request_logs (status);
CREATE INDEX idx_request_logs_user_id ON request_logs (user_id);
