package com.spotifylike.analytics.repository;

import com.spotifylike.analytics.entity.PageView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface PageViewRepository extends JpaRepository<PageView, Long> {

    List<PageView> findByEntityIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            String entityId, Instant from, Instant to);
}
