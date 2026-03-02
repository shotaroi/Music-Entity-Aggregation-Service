package com.spotifylike.analytics.controller;

import com.spotifylike.analytics.dto.PageViewResponse;
import com.spotifylike.analytics.entity.PageView;
import com.spotifylike.analytics.repository.PageViewRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/v1/analytics")
public class AnalyticsController {

    private final PageViewRepository repository;

    public AnalyticsController(PageViewRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/page-views")
    public ResponseEntity<List<PageViewResponse>> getPageViews(
            @RequestParam(required = false) String entityId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {

        if (entityId == null || entityId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Instant fromInstant = from != null ? from : Instant.now().minusSeconds(86400); // default 24h
        Instant toInstant = to != null ? to : Instant.now();

        List<PageView> views = repository.findByEntityIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                entityId, fromInstant, toInstant);
        List<PageViewResponse> responses = views.stream()
                .map(v -> new PageViewResponse(
                        v.getId(), v.getEventType(), v.getPageType(), v.getEntityId(),
                        v.getUserId(), v.getRegion(), v.getLang(), v.getLatencyMs(),
                        v.getCacheHit(), v.getTraceId(), v.getCreatedAt()))
                .toList();
        return ResponseEntity.ok(responses);
    }
}
