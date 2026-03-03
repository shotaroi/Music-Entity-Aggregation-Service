package com.spotifylike.entitypage.event;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PageViewedEvent(
        String eventType,
        String pageType,
        String entityId,
        String userId,
        String region,
        String lang,
        Long latencyMs,
        Boolean cacheHit,
        Instant timestamp,
        String traceId
) {
    public static PageViewedEvent of(String entityId, String userId, String region, String lang,
                                     long latencyMs, boolean cacheHit, String traceId) {
        return new PageViewedEvent(
                "PageViewed",
                "ARTIST",
                entityId,
                userId,
                region,
                lang,
                latencyMs,
                cacheHit,
                Instant.now(),
                traceId
        );
    }
}
