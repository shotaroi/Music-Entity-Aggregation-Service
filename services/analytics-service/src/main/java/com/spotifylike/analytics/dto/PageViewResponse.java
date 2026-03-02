package com.spotifylike.analytics.dto;

import java.time.Instant;

public record PageViewResponse(
        Long id,
        String eventType,
        String pageType,
        String entityId,
        String userId,
        String region,
        String lang,
        Long latencyMs,
        Boolean cacheHit,
        String traceId,
        Instant createdAt
) {}
