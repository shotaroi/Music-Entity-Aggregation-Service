package com.spotifylike.popularity.controller;

import com.spotifylike.popularity.dto.TopTrackResponse;
import com.spotifylike.popularity.service.PopularityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/artists")
public class ArtistTopTracksController {

    private final PopularityService popularityService;

    public ArtistTopTracksController(PopularityService popularityService) {
        this.popularityService = popularityService;
    }

    @GetMapping("/{artistId}/top-tracks")
    public ResponseEntity<List<TopTrackResponse>> getTopTracks(@PathVariable String artistId) {
        List<TopTrackResponse> tracks = popularityService.getTopTracks(artistId);
        return ResponseEntity.ok(tracks);
    }
}
