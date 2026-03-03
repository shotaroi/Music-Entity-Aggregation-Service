package com.spotifylike.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArtistResponse(
        String id,
        String name,
        String imageUrl
) {}
