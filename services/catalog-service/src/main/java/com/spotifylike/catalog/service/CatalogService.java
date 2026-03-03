package com.spotifylike.catalog.service;

import com.spotifylike.catalog.dto.AlbumResponse;
import com.spotifylike.catalog.dto.ArtistResponse;
import com.spotifylike.catalog.dto.TrackResponse;
import com.spotifylike.catalog.entity.Album;
import com.spotifylike.catalog.entity.Artist;
import com.spotifylike.catalog.entity.Track;
import com.spotifylike.catalog.repository.AlbumRepository;
import com.spotifylike.catalog.repository.ArtistRepository;
import com.spotifylike.catalog.repository.TrackRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;

    public CatalogService(ArtistRepository artistRepository,
                          AlbumRepository albumRepository,
                          TrackRepository trackRepository) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.trackRepository = trackRepository;
    }

    public Optional<ArtistResponse> getArtist(String artistId) {
        return artistRepository.findById(artistId)
                .map(this::toArtistResponse);
    }

    public List<AlbumResponse> getArtistAlbums(String artistId) {
        return albumRepository.findByArtistIdOrderByReleaseDateDesc(artistId)
                .stream()
                .map(this::toAlbumResponse)
                .collect(Collectors.toList());
    }

    public Optional<TrackResponse> getTrack(String trackId) {
        return trackRepository.findById(trackId)
                .map(this::toTrackResponse);
    }

    private ArtistResponse toArtistResponse(Artist a) {
        return new ArtistResponse(a.getId(), a.getName(), a.getImageUrl());
    }

    private AlbumResponse toAlbumResponse(Album a) {
        return new AlbumResponse(
                a.getId(),
                a.getArtistId(),
                a.getTitle(),
                a.getReleaseDate(),
                a.getImageUrl()
        );
    }

    private TrackResponse toTrackResponse(Track t) {
        return new TrackResponse(
                t.getId(),
                t.getAlbumId(),
                t.getTitle(),
                t.getDurationMs(),
                t.getTrackNumber()
        );
    }
}
