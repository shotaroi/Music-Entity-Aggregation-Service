package com.spotifylike.entitypage.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


/**
 * Fire-and-forget Kafka producer for page view events.
 * Does not block the response.
 */
@Component
public class PageEventProducer {

    private static final Logger log = LoggerFactory.getLogger(PageEventProducer.class);

    private final KafkaTemplate<String, PageViewedEvent> kafkaTemplate;
    private final String topic;

    public PageEventProducer(KafkaTemplate<String, PageViewedEvent> kafkaTemplate,
                            @Value("${kafka.topics.page-events:page-events}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publishAsync(PageViewedEvent event) {
        kafkaTemplate.send(topic, event.entityId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.warn("Failed to publish PageViewed event: {}", ex.getMessage());
                    }
                });
    }
}
