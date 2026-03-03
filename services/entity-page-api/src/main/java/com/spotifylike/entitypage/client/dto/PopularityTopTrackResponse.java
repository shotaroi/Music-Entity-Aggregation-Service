package com.spotifylike.entitypage.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PopularityTopTrackResponse(
        String trackId,
        String title,
        Integer durationMs,
        Integer popularityScore
) {}
