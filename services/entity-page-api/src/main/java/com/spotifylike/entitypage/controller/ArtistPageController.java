package com.spotifylike.entitypage.controller;

import com.spotifylike.entitypage.dto.ArtistPageResponse;
import com.spotifylike.entitypage.dto.PageResult;
import com.spotifylike.entitypage.event.PageEventProducer;
import com.spotifylike.entitypage.event.PageViewedEvent;
import com.spotifylike.entitypage.service.ArtistPageService;
import com.spotifylike.entitypage.service.CachedArtistPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/pages/artists")
public class ArtistPageController {

    private final CachedArtistPageService cachedArtistPageService;
    private final PageEventProducer pageEventProducer;

    public ArtistPageController(CachedArtistPageService cachedArtistPageService,
                               PageEventProducer pageEventProducer) {
        this.cachedArtistPageService = cachedArtistPageService;
        this.pageEventProducer = pageEventProducer;
    }

    @GetMapping("/{artistId}")
    public Mono<ResponseEntity<ArtistPageResponse>> getArtistPage(
            @PathVariable String artistId,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Region", required = false) String region,
            @RequestHeader(value = "Accept-Language", required = false) String language,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId) {

        var ctx = ArtistPageService.PageRequestContext.from(userId, region, language, requestId);
        long startMs = System.currentTimeMillis();

        return cachedArtistPageService.getArtistPage(artistId, ctx)
                .doOnSuccess(result -> {
                    if (result != null) {
                        long latencyMs = System.currentTimeMillis() - startMs;
                        PageViewedEvent event = PageViewedEvent.of(
                                artistId, ctx.userId(), ctx.region(), ctx.language(),
                                latencyMs, result.cacheHit(), ctx.traceId());
                        pageEventProducer.publishAsync(event);
                    }
                })
                .map(r -> ResponseEntity.ok(r.response()))
                .onErrorResume(ResponseStatusException.class, e ->
                        Mono.just(ResponseEntity.<ArtistPageResponse>status(e.getStatusCode()).build()));
    }
}
