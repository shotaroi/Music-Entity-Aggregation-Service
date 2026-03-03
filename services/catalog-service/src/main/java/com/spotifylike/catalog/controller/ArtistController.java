package com.spotifylike.catalog.controller;

import com.spotifylike.catalog.dto.ArtistResponse;
import com.spotifylike.catalog.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/artists")
public class ArtistController {

    private final CatalogService catalogService;

    public ArtistController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/{artistId}")
    public ResponseEntity<ArtistResponse> getArtist(@PathVariable String artistId) {
        return catalogService.getArtist(artistId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{artistId}/albums")
    public ResponseEntity<?> getArtistAlbums(@PathVariable String artistId) {
        var albums = catalogService.getArtistAlbums(artistId);
        return ResponseEntity.ok(albums);
    }
}
