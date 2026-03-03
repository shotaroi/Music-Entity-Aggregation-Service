package com.spotifylike.catalog.repository;

import com.spotifylike.catalog.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, String> {
}
