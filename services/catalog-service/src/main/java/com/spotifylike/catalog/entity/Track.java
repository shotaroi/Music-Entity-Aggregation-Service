package com.spotifylike.catalog.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "tracks", schema = "catalog")
public class Track {

    @Id
    private String id;

    @Column(name = "album_id", nullable = false)
    private String albumId;

    @Column(nullable = false)
    private String title;

    @Column(name = "duration_ms", nullable = false)
    private Integer durationMs;

    @Column(name = "track_number", nullable = false)
    private Integer trackNumber;

    @Column(name = "created_at")
    private Instant createdAt;

    public Track() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Integer durationMs) {
        this.durationMs = durationMs;
    }

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(Integer trackNumber) {
        this.trackNumber = trackNumber;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
