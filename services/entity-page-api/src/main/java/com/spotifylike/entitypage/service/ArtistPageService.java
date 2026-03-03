package com.spotifylike.entitypage.service;

import com.spotifylike.entitypage.client.CatalogClient;
import com.spotifylike.entitypage.client.PopularityClient;
import com.spotifylike.entitypage.client.dto.CatalogAlbumResponse;
import com.spotifylike.entitypage.client.dto.CatalogArtistResponse;
import com.spotifylike.entitypage.client.dto.PopularityTopTrackResponse;
import com.spotifylike.entitypage.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ArtistPageService {

    private static final String PLACEHOLDER_IMAGE_URL = "https://via.placeholder.com/640x640?text=No+Image";
    private static final int HERO_IMAGE_SIZE = 640;

    private final CatalogClient catalogClient;
    private final PopularityClient popularityClient;

    public ArtistPageService(CatalogClient catalogClient, PopularityClient popularityClient) {
        this.catalogClient = catalogClient;
        this.popularityClient = popularityClient;
    }

    /**
     * Fetch and build artist page (no cache). Used by CachedArtistPageService.
     */
    public Mono<ArtistPageResponse> fetchArtistPage(String artistId, PageRequestContext ctx, String experimentBucket) {
        Mono<CatalogArtistResponse> artistMono = catalogClient.getArtist(artistId);
        Mono<List<CatalogAlbumResponse>> albumsMono = catalogClient.getArtistAlbums(artistId)
                .onErrorReturn(List.of());
        Mono<List<PopularityTopTrackResponse>> topTracksMono = popularityClient.getTopTracks(artistId)
                .onErrorReturn(List.of());

        return Mono.zip(artistMono, albumsMono, topTracksMono)
                .map(tuple -> buildResponse(
                        tuple.getT1(),
                        tuple.getT2(),
                        tuple.getT3(),
                        artistId,
                        ctx,
                        experimentBucket
                ));
    }

    public String computeExperimentBucket(String userId) {
        if (userId == null || userId.isBlank()) {
            return "control";
        }
        int bucket = Math.abs(userId.hashCode() % 100);
        return bucket < 50 ? "control" : "variantA";
    }

    private ArtistPageResponse buildResponse(
            CatalogArtistResponse artist,
            List<CatalogAlbumResponse> albums,
            List<PopularityTopTrackResponse> topTracks,
            String entityId,
            PageRequestContext ctx,
            String experimentBucket) {

        String imageUrl = (artist.imageUrl() != null && !artist.imageUrl().isBlank())
                ? artist.imageUrl()
                : PLACEHOLDER_IMAGE_URL;

        HeroImage heroImage = new HeroImage(imageUrl, HERO_IMAGE_SIZE, HERO_IMAGE_SIZE);

        List<PageSection> sections = new ArrayList<>();

        if (!topTracks.isEmpty()) {
            List<SectionItem> trackItems = topTracks.stream()
                    .map(t -> SectionItem.forTrack(t.trackId(), t.title(), t.durationMs()))
                    .toList();
            sections.add(new PageSection("TOP_TRACKS", trackItems));
        }

        List<SectionItem> albumItems = albums.stream()
                .map(a -> SectionItem.forAlbum(a.id(), a.title(), a.releaseDate()))
                .toList();
        sections.add(new PageSection("ALBUMS", albumItems));

        if ("variantA".equals(experimentBucket)) {
            sections.add(new PageSection("RELATED_ARTISTS", List.of()));
        }

        Map<String, String> experiments = Map.of("showRelatedArtists", experimentBucket);

        return new ArtistPageResponse(
                "ARTIST",
                entityId,
                artist.name(),
                heroImage,
                sections,
                experiments,
                Instant.now(),
                ctx.traceId()
        );
    }

    public record PageRequestContext(String userId, String region, String language, String requestId, String traceId) {
        public static PageRequestContext from(String userId, String region, String language, String requestId) {
            String traceId = requestId != null && !requestId.isBlank() ? requestId : UUID.randomUUID().toString();
            return new PageRequestContext(
                    userId != null ? userId : "",
                    region != null && !region.isBlank() ? region : "SE",
                    language != null && !language.isBlank() ? language : "en",
                    requestId != null ? requestId : traceId,
                    traceId
            );
        }
    }
}
