package com.spotifylike.entitypage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArtistPageResponse(
        String pageType,
        String entityId,
        String displayName,
        HeroImage heroImage,
        List<PageSection> sections,
        Map<String, String> experiments,
        Instant generatedAt,
        String traceId
) {}
