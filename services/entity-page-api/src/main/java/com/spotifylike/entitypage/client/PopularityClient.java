package com.spotifylike.entitypage.client;

import com.spotifylike.entitypage.client.dto.PopularityTopTrackResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class PopularityClient {

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;

    public PopularityClient(@Qualifier("popularityWebClient") WebClient popularityWebClient,
                           io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry registry) {
        this.webClient = popularityWebClient;
        this.circuitBreaker = registry.circuitBreaker("popularity");
    }

    public Mono<List<PopularityTopTrackResponse>> getTopTracks(String artistId) {
        return webClient.get()
                .uri("/v1/artists/{artistId}/top-tracks", artistId)
                .retrieve()
                .bodyToFlux(PopularityTopTrackResponse.class)
                .collectList()
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }
}
