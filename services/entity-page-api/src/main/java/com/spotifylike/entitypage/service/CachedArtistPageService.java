package com.spotifylike.entitypage.service;

import com.spotifylike.entitypage.cache.PageCacheService;
import com.spotifylike.entitypage.dto.ArtistPageResponse;
import com.spotifylike.entitypage.dto.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Cache-aware wrapper around ArtistPageService.
 * Delegates to ArtistPageService for computation, uses PageCacheService for caching.
 */
@Service
public class CachedArtistPageService {

    private final ArtistPageService artistPageService;
    private final PageCacheService pageCacheService;

    public CachedArtistPageService(ArtistPageService artistPageService, PageCacheService pageCacheService) {
        this.artistPageService = artistPageService;
        this.pageCacheService = pageCacheService;
    }

    public Mono<PageResult> getArtistPage(String artistId, ArtistPageService.PageRequestContext ctx) {
        String experimentBucket = artistPageService.computeExperimentBucket(ctx.userId());
        String cacheKey = pageCacheService.buildCacheKey(artistId, ctx.region(), ctx.language(), experimentBucket);
        String lockKey = pageCacheService.buildLockKey(artistId, ctx.region(), ctx.language(), experimentBucket);

        Mono<ArtistPageResponse> compute = artistPageService.fetchArtistPage(artistId, ctx, experimentBucket);

        return pageCacheService.getOrComputeWithMeta(cacheKey, lockKey, compute)
                .onErrorResume(WebClientResponseException.NotFound.class,
                        e -> Mono.error(new org.springframework.web.server.ResponseStatusException(
                                org.springframework.http.HttpStatus.NOT_FOUND, "Artist not found")));
    }
}
