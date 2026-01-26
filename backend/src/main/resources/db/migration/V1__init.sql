CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE orgs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'active',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE org_members (
    org_id UUID NOT NULL REFERENCES orgs(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role TEXT NOT NULL CHECK (role IN ('admin', 'viewer')),
    PRIMARY KEY (org_id, user_id)
);

CREATE TABLE projects (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    org_id UUID NOT NULL REFERENCES orgs(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE environments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    env_name TEXT NOT NULL CHECK (env_name IN ('dev', 'stage', 'prod')),
    UNIQUE (project_id, env_name)
);

CREATE TABLE api_keys (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    env_name TEXT NOT NULL CHECK (env_name IN ('dev', 'stage', 'prod')),
    key_hash TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    revoked_at TIMESTAMPTZ,
    UNIQUE (project_id, env_name, key_hash)
);

CREATE TABLE schemas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    version INT NOT NULL,
    schema_json JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (project_id, version)
);

CREATE TABLE events (
    id BIGSERIAL,
    org_id UUID NOT NULL REFERENCES orgs(id) ON DELETE CASCADE,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    env_name TEXT NOT NULL CHECK (env_name IN ('dev', 'stage', 'prod')),
    event_id UUID NOT NULL,
    event_name TEXT NOT NULL,
    event_description TEXT,
    ts_client TIMESTAMPTZ NOT NULL,
    ts_server TIMESTAMPTZ NOT NULL DEFAULT now(),
    player_id TEXT NOT NULL,
    session_id TEXT NOT NULL,
    build_version TEXT,
    platform TEXT,
    device JSONB,
    properties JSONB NOT NULL,
    PRIMARY KEY (id, ts_server),
    UNIQUE (project_id, env_name, event_id, ts_server)
) PARTITION BY RANGE (ts_server);

CREATE INDEX idx_events_project_env_ts ON events (project_id, env_name, ts_server);
CREATE INDEX idx_events_project_env_name_ts ON events (project_id, env_name, event_name, ts_server);
CREATE INDEX idx_events_project_env_build_ts ON events (project_id, env_name, build_version, ts_server);
CREATE INDEX idx_events_project_env_player_ts ON events (project_id, env_name, player_id, ts_server);

CREATE TABLE quarantine_events (
    id BIGSERIAL PRIMARY KEY,
    org_id UUID NOT NULL REFERENCES orgs(id) ON DELETE CASCADE,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    env_name TEXT NOT NULL CHECK (env_name IN ('dev', 'stage', 'prod')),
    received_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    event_id UUID,
    event_name TEXT,
    raw_event JSONB NOT NULL,
    reasons JSONB NOT NULL,
    schema_version_at_time INT,
    client_sdk TEXT,
    client_sdk_version TEXT,
    engine TEXT,
    engine_version TEXT,
    build_version TEXT,
    platform TEXT
);

CREATE INDEX idx_quarantine_project_env_received ON quarantine_events (project_id, env_name, received_at);
CREATE INDEX idx_quarantine_project_env_name_received ON quarantine_events (project_id, env_name, event_name, received_at);

CREATE TABLE ai_reports (
    id BIGSERIAL PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    env_name TEXT NOT NULL CHECK (env_name IN ('dev', 'stage', 'prod')),
    report_type TEXT NOT NULL CHECK (report_type IN ('daily', 'weekly')),
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    status TEXT NOT NULL DEFAULT 'completed',
    report_json JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE daily_project_metrics (
    metric_date DATE NOT NULL,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    env_name TEXT NOT NULL CHECK (env_name IN ('dev', 'stage', 'prod')),
    total_events BIGINT NOT NULL,
    unique_players BIGINT NOT NULL,
    sessions_started BIGINT NOT NULL,
    PRIMARY KEY (metric_date, project_id, env_name)
);

CREATE TABLE daily_event_name_metrics (
    metric_date DATE NOT NULL,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    env_name TEXT NOT NULL CHECK (env_name IN ('dev', 'stage', 'prod')),
    event_name TEXT NOT NULL,
    count BIGINT NOT NULL,
    PRIMARY KEY (metric_date, project_id, env_name, event_name)
);

CREATE TABLE daily_level_funnel (
    metric_date DATE NOT NULL,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    env_name TEXT NOT NULL CHECK (env_name IN ('dev', 'stage', 'prod')),
    level_id TEXT NOT NULL,
    starts BIGINT NOT NULL,
    ends_success BIGINT NOT NULL,
    ends_fail BIGINT NOT NULL,
    ends_quit BIGINT NOT NULL,
    avg_duration_sec DOUBLE PRECISION,
    PRIMARY KEY (metric_date, project_id, env_name, level_id)
);
