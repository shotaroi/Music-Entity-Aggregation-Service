CREATE SCHEMA IF NOT EXISTS analytics;

CREATE TABLE analytics.page_views (
    id          BIGSERIAL PRIMARY KEY,
    event_type  VARCHAR(64) NOT NULL,
    page_type   VARCHAR(64) NOT NULL,
    entity_id   VARCHAR(128) NOT NULL,
    user_id     VARCHAR(128),
    region      VARCHAR(32),
    lang        VARCHAR(16),
    latency_ms  BIGINT,
    cache_hit   BOOLEAN,
    trace_id    VARCHAR(128),
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_page_views_entity_id ON analytics.page_views(entity_id);
CREATE INDEX idx_page_views_created_at ON analytics.page_views(created_at);
