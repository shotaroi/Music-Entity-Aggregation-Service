package com.spotifylike.entitypage.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.spotifylike.entitypage.dto.ArtistPageResponse;
import com.spotifylike.entitypage.dto.PageResult;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Redis cache for artist pages with:
 * - Cache key: artistId|region|language|experimentBucket
 * - TTL: 60s + random jitter 0-15s
 * - Stampede protection: Redis lock (SET NX EX 3s)
 */
@Service
public class PageCacheService {

    private static final String KEY_PREFIX = "page:artist:";
    private static final String LOCK_PREFIX = "page:lock:";
    private static final int BASE_TTL_SECONDS = 60;
    private static final int JITTER_SECONDS = 15;
    private static final int LOCK_TTL_SECONDS = 3;
    private static final int LOCK_WAIT_MS = 250;
    private static final int MAX_LOCK_RETRIES = 12; // ~3s total wait

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public PageCacheService(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public String buildCacheKey(String artistId, String region, String language, String experimentBucket) {
        return KEY_PREFIX + artistId + "|" + region + "|" + language + "|" + experimentBucket;
    }

    public String buildLockKey(String artistId, String region, String language, String experimentBucket) {
        return LOCK_PREFIX + artistId + "|" + region + "|" + language + "|" + experimentBucket;
    }

    public Mono<ArtistPageResponse> get(String key) {
        return redisTemplate.opsForValue().get(key)
                .flatMap(json -> {
                    try {
                        return Mono.just(objectMapper.readValue(json, ArtistPageResponse.class));
                    } catch (JsonProcessingException e) {
                        return Mono.empty();
                    }
                });
    }

    public Mono<Void> put(String key, ArtistPageResponse value) {
        int ttlSeconds = BASE_TTL_SECONDS + ThreadLocalRandom.current().nextInt(0, JITTER_SECONDS + 1);
        try {
            String json = objectMapper.writeValueAsString(value);
            return redisTemplate.opsForValue().set(key, json, Duration.ofSeconds(ttlSeconds))
                    .then();
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    /**
     * Try to acquire lock. Returns true if acquired.
     */
    public Mono<Boolean> tryLock(String lockKey) {
        return redisTemplate.opsForValue().setIfAbsent(lockKey, "1", Duration.ofSeconds(LOCK_TTL_SECONDS));
    }

    public Mono<Void> releaseLock(String lockKey) {
        return redisTemplate.delete(lockKey).then();
    }

    /**
     * Get from cache, or compute with stampede protection.
     * Returns PageResult with cacheHit flag.
     */
    public Mono<PageResult> getOrComputeWithMeta(
            String cacheKey,
            String lockKey,
            Mono<ArtistPageResponse> compute) {

        return get(cacheKey)
                .map(r -> new PageResult(r, true))
                .switchIfEmpty(
                        tryLock(lockKey).flatMap(acquired -> {
                            if (Boolean.TRUE.equals(acquired)) {
                                return compute
                                        .flatMap(value -> put(cacheKey, value)
                                                .thenReturn(new PageResult(value, false)))
                                        .doFinally(s -> releaseLock(lockKey).subscribe());
                            } else {
                                return retryGetOrComputeWithMeta(cacheKey, lockKey, compute, MAX_LOCK_RETRIES);
                            }
                        })
                );
    }

    private Mono<PageResult> retryGetOrComputeWithMeta(
            String cacheKey,
            String lockKey,
            Mono<ArtistPageResponse> compute,
            int retriesLeft) {

        if (retriesLeft <= 0) {
            return compute.map(r -> new PageResult(r, false));
        }
        return Mono.delay(Duration.ofMillis(LOCK_WAIT_MS))
                .then(get(cacheKey))
                .map(r -> new PageResult(r, true))
                .switchIfEmpty(
                        tryLock(lockKey).flatMap(acquired -> {
                            if (Boolean.TRUE.equals(acquired)) {
                                return compute
                                        .flatMap(value -> put(cacheKey, value)
                                                .thenReturn(new PageResult(value, false)))
                                        .doFinally(s -> releaseLock(lockKey).subscribe());
                            } else {
                                return retryGetOrComputeWithMeta(cacheKey, lockKey, compute, retriesLeft - 1);
                            }
                        })
                );
    }
}
