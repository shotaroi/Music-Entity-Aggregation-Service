package com.spotifylike.analytics.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.spotifylike.analytics.entity.PageView;
import com.spotifylike.analytics.repository.PageViewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PageViewedConsumer {

    private static final Logger log = LoggerFactory.getLogger(PageViewedConsumer.class);

    private final PageViewRepository repository;

    public PageViewedConsumer(PageViewRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "${kafka.topics.page-events:page-events}", groupId = "analytics-service")
    public void consume(String message) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            JsonNode node = mapper.readTree(message);
            PageView pv = PageView.from(
                    getText(node, "eventType"),
                    getText(node, "pageType"),
                    getText(node, "entityId"),
                    getText(node, "userId"),
                    getText(node, "region"),
                    getText(node, "lang"),
                    node.has("latencyMs") ? node.get("latencyMs").asLong() : null,
                    node.has("cacheHit") ? node.get("cacheHit").asBoolean() : null,
                    getText(node, "traceId")
            );
            repository.save(pv);
        } catch (Exception e) {
            log.warn("Failed to process page event: {}", e.getMessage());
        }
    }

    private static String getText(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asText() : null;
    }
}
