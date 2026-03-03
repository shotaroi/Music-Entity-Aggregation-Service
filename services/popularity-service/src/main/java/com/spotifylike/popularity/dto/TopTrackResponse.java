package com.spotifylike.popularity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TopTrackResponse(
        String trackId,
        String title,
        Integer durationMs,
        Integer popularityScore
) {}
