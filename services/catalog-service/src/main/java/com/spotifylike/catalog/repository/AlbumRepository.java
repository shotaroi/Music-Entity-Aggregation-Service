package com.spotifylike.catalog.repository;

import com.spotifylike.catalog.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, String> {

    List<Album> findByArtistIdOrderByReleaseDateDesc(String artistId);
}
