package com.spotifylike.catalog.repository;

import com.spotifylike.catalog.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackRepository extends JpaRepository<Track, String> {

    List<Track> findByAlbumIdOrderByTrackNumberAsc(String albumId);
}
