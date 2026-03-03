package com.spotifylike.catalog.controller;

import com.spotifylike.catalog.dto.TrackResponse;
import com.spotifylike.catalog.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/tracks")
public class TrackController {

    private final CatalogService catalogService;

    public TrackController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/{trackId}")
    public ResponseEntity<TrackResponse> getTrack(@PathVariable String trackId) {
        return catalogService.getTrack(trackId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
