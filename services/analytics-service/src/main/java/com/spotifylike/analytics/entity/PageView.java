package com.spotifylike.analytics.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "page_views", schema = "analytics")
public class PageView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "page_type", nullable = false)
    private String pageType;

    @Column(name = "entity_id", nullable = false)
    private String entityId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "region")
    private String region;

    @Column(name = "lang")
    private String lang;

    @Column(name = "latency_ms")
    private Long latencyMs;

    @Column(name = "cache_hit")
    private Boolean cacheHit;

    @Column(name = "trace_id")
    private String traceId;

    @Column(name = "created_at")
    private Instant createdAt;

    public PageView() {}

    public static PageView from(String eventType, String pageType, String entityId, String userId,
                                String region, String lang, Long latencyMs, Boolean cacheHit, String traceId) {
        PageView pv = new PageView();
        pv.eventType = eventType;
        pv.pageType = pageType;
        pv.entityId = entityId;
        pv.userId = userId;
        pv.region = region;
        pv.lang = lang;
        pv.latencyMs = latencyMs;
        pv.cacheHit = cacheHit;
        pv.traceId = traceId;
        pv.createdAt = Instant.now();
        return pv;
    }

    public Long getId() { return id; }
    public String getEventType() { return eventType; }
    public String getPageType() { return pageType; }
    public String getEntityId() { return entityId; }
    public String getUserId() { return userId; }
    public String getRegion() { return region; }
    public String getLang() { return lang; }
    public Long getLatencyMs() { return latencyMs; }
    public Boolean getCacheHit() { return cacheHit; }
    public String getTraceId() { return traceId; }
    public Instant getCreatedAt() { return createdAt; }
}
