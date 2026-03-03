package com.spotifylike.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AlbumResponse(
        String id,
        String artistId,
        String title,
        LocalDate releaseDate,
        String imageUrl
) {}
