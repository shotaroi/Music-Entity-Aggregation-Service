package com.spotifylike.catalog.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "albums", schema = "catalog")
public class Album {

    @Id
    private String id;

    @Column(name = "artist_id", nullable = false)
    private String artistId;

    @Column(nullable = false)
    private String title;

    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "created_at")
    private Instant createdAt;

    public Album() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
