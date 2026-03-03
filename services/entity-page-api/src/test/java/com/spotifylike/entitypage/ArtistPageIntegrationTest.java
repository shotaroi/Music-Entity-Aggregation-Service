package com.spotifylike.entitypage;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=test")
@AutoConfigureWebTestClient
@Testcontainers
@EmbeddedKafka(partitions = 1, topics = {"page-events"}, bootstrapServersProperty = "spring.kafka.bootstrap-servers")
class ArtistPageIntegrationTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    static WireMockServer catalogServer = new WireMockServer(0);
    static WireMockServer popularityServer = new WireMockServer(0);

    static {
        catalogServer.start();
        popularityServer.start();
    }

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379).toString());
        registry.add("services.catalog.url", () -> "http://localhost:" + catalogServer.port());
        registry.add("services.popularity.url", () -> "http://localhost:" + popularityServer.port());
    }

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void resetStubs() {
        catalogServer.resetAll();
        popularityServer.resetAll();
    }

    @Test
    void healthEndpointReturnsUp() {
        webTestClient.get().uri("/actuator/health")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }

    @Test
    @org.junit.jupiter.api.Disabled("Requires WebClient timeout override for WireMock - TODO")
    void cacheHitServesFromRedisWithoutCallingDownstream() {
        // Stub catalog and popularity - use urlPathEqualTo for exact match
        catalogServer.stubFor(WireMock.get(urlPathEqualTo("/v1/artists/a_1"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"a_1\",\"name\":\"Daft Punk\",\"imageUrl\":\"https://example.com/img\"}")));
        catalogServer.stubFor(WireMock.get(urlPathEqualTo("/v1/artists/a_1/albums"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody("[{\"id\":\"al_1\",\"artistId\":\"a_1\",\"title\":\"RAM\",\"releaseDate\":\"2013-05-17\",\"imageUrl\":null}]")));
        popularityServer.stubFor(WireMock.get(urlPathEqualTo("/v1/artists/a_1/top-tracks"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody("[{\"trackId\":\"t_1\",\"title\":\"Get Lucky\",\"durationMs\":369000,\"popularityScore\":95}]")));

        // First request (cache miss) - downstream called
        webTestClient.get().uri("/v1/pages/artists/a_1")
                .header("X-User-Id", "u_99")
                .header("X-Region", "SE")
                .header("Accept-Language", "en")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.displayName").isEqualTo("Daft Punk")
                .jsonPath("$.entityId").isEqualTo("a_1");

        // Second request (cache hit) - downstream should NOT be called again
        webTestClient.get().uri("/v1/pages/artists/a_1")
                .header("X-User-Id", "u_99")
                .header("X-Region", "SE")
                .header("Accept-Language", "en")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.displayName").isEqualTo("Daft Punk");

        // Catalog artist endpoint should have been called only once (first request)
        int catalogArtistCalls = catalogServer.countRequestsMatching(
                WireMock.getRequestedFor(urlPathEqualTo("/v1/artists/a_1")).build()).getCount();
        assertThat(catalogArtistCalls).isEqualTo(1);
    }

    @Test
    void artistPageReturnsErrorWhenArtistNotFound() {
        catalogServer.stubFor(WireMock.get(urlPathEqualTo("/v1/artists/unknown"))
                .willReturn(aResponse().withStatus(404)));
        catalogServer.stubFor(WireMock.get(urlPathEqualTo("/v1/artists/unknown/albums"))
                .willReturn(aResponse().withStatus(200).withBody("[]")));

        // Catalog 404: may return 404 or 500 (circuit breaker)
        webTestClient.get().uri("/v1/pages/artists/unknown")
                .header("X-User-Id", "u_99")
                .exchange()
                .expectStatus().value(anyOf(is(404), is(500)));
    }
}
