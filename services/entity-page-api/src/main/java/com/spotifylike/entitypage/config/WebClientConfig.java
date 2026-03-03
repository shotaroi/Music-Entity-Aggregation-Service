package com.spotifylike.entitypage.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    private static final int CONNECT_TIMEOUT_MS = 50;
    private static final int READ_TIMEOUT_MS = 80;
    private static final int WRITE_TIMEOUT_MS = 80;

    private static HttpClient createHttpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MS)
                .responseTimeout(Duration.ofMillis(READ_TIMEOUT_MS))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(READ_TIMEOUT_MS, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS)));
    }

    @Bean("catalogWebClient")
    public WebClient catalogWebClient(
            WebClient.Builder builder,
            @Value("${services.catalog.url:http://localhost:8081}") String baseUrl) {
        return builder
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .build();
    }

    @Bean("popularityWebClient")
    public WebClient popularityWebClient(
            WebClient.Builder builder,
            @Value("${services.popularity.url:http://localhost:8082}") String baseUrl) {
        return builder
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .build();
    }
}
