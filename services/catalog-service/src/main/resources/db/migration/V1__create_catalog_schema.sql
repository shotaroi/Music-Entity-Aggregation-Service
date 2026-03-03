-- Catalog schema: artists, albums, tracks
CREATE SCHEMA IF NOT EXISTS catalog;

CREATE TABLE catalog.artists (
    id          VARCHAR(64) PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    image_url   VARCHAR(512),
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE catalog.albums (
    id          VARCHAR(64) PRIMARY KEY,
    artist_id   VARCHAR(64) NOT NULL REFERENCES catalog.artists(id) ON DELETE CASCADE,
    title       VARCHAR(255) NOT NULL,
    release_date DATE NOT NULL,
    image_url   VARCHAR(512),
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE catalog.tracks (
    id          VARCHAR(64) PRIMARY KEY,
    album_id    VARCHAR(64) NOT NULL REFERENCES catalog.albums(id) ON DELETE CASCADE,
    title       VARCHAR(255) NOT NULL,
    duration_ms INTEGER NOT NULL,
    track_number INTEGER NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_albums_artist_id ON catalog.albums(artist_id);
CREATE INDEX idx_tracks_album_id ON catalog.tracks(album_id);
