package com.spotifylike.entitypage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SectionItem(
        String trackId,
        String title,
        Integer durationMs,
        String albumId,
        LocalDate releaseDate
) {
    public static SectionItem forTrack(String trackId, String title, Integer durationMs) {
        return new SectionItem(trackId, title, durationMs, null, null);
    }

    public static SectionItem forAlbum(String albumId, String title, LocalDate releaseDate) {
        return new SectionItem(null, title, null, albumId, releaseDate);
    }
}
