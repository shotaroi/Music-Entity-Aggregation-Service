package com.spotifylike.entitypage.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CatalogArtistResponse(String id, String name, String imageUrl) {}
