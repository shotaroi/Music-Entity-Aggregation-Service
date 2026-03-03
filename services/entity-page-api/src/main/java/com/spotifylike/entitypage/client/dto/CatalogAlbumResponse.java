package com.spotifylike.entitypage.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CatalogAlbumResponse(
        String id,
        String artistId,
        String title,
        LocalDate releaseDate,
        String imageUrl
) {}
