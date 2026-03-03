package com.spotifylike.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TrackResponse(
        String id,
        String albumId,
        String title,
        Integer durationMs,
        Integer trackNumber
) {}
