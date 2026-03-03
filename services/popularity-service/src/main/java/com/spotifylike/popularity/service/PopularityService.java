package com.spotifylike.popularity.service;

import com.spotifylike.popularity.dto.TopTrackResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Deterministic top-tracks data. Mapped to catalog-service track IDs.
 * Unknown artists return empty list.
 */
@Service
public class PopularityService {

    // Artist ID -> ordered list of (trackId, title, durationMs, popularityScore)
    private static final Map<String, List<TopTrackResponse>> TOP_TRACKS = Map.of(
            "a_1", List.of(
                    new TopTrackResponse("t_1", "Get Lucky", 369000, 95),
                    new TopTrackResponse("t_4", "One More Time", 320000, 92),
                    new TopTrackResponse("t_5", "Harder, Better, Faster, Stronger", 224000, 90),
                    new TopTrackResponse("t_2", "Instant Crush", 337000, 88),
                    new TopTrackResponse("t_3", "Lose Yourself to Dance", 354000, 85)
            ),
            "a_2", List.of(
                    new TopTrackResponse("t_6", "Blinding Lights", 200000, 98),
                    new TopTrackResponse("t_7", "Save Your Tears", 215000, 94)
            ),
            "a_3", List.of(
                    new TopTrackResponse("t_8", "Shake It Off", 219000, 96),
                    new TopTrackResponse("t_9", "Blank Space", 231000, 93)
            )
    );

    public List<TopTrackResponse> getTopTracks(String artistId) {
        return TOP_TRACKS.getOrDefault(artistId, List.of());
    }
}
