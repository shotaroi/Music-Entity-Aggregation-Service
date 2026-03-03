package com.spotifylike.entitypage.client;

import com.spotifylike.entitypage.client.dto.CatalogAlbumResponse;
import com.spotifylike.entitypage.client.dto.CatalogArtistResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class CatalogClient {

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;

    public CatalogClient(@Qualifier("catalogWebClient") WebClient catalogWebClient,
                        io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry registry) {
        this.webClient = catalogWebClient;
        this.circuitBreaker = registry.circuitBreaker("catalog");
    }

    public Mono<CatalogArtistResponse> getArtist(String artistId) {
        return webClient.get()
                .uri("/v1/artists/{artistId}", artistId)
                .retrieve()
                .bodyToMono(CatalogArtistResponse.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    public Mono<List<CatalogAlbumResponse>> getArtistAlbums(String artistId) {
        return webClient.get()
                .uri("/v1/artists/{artistId}/albums", artistId)
                .retrieve()
                .bodyToFlux(CatalogAlbumResponse.class)
                .collectList()
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }
}
