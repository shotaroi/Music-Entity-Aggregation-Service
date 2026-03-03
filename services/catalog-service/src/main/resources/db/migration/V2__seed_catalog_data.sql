-- Seed sample artists, albums, tracks
INSERT INTO catalog.artists (id, name, image_url) VALUES
    ('a_1', 'Daft Punk', 'https://i.scdn.co/image/ab6761610000e5eb0a7386b1e646775c8ad2e9c2'),
    ('a_2', 'The Weeknd', 'https://i.scdn.co/image/ab6761610000e5eb0a7386b1e646775c8ad2e9c3'),
    ('a_3', 'Taylor Swift', 'https://i.scdn.co/image/ab6761610000e5eb0a7386b1e646775c8ad2e9c4');

INSERT INTO catalog.albums (id, artist_id, title, release_date, image_url) VALUES
    ('al_1', 'a_1', 'Random Access Memories', '2013-05-17', 'https://i.scdn.co/image/ab67616d0000b2732e8ed79e177ff6011076f5f0'),
    ('al_2', 'a_1', 'Discovery', '2001-03-12', 'https://i.scdn.co/image/ab67616d0000b2732e8ed79e177ff6011076f5f1'),
    ('al_3', 'a_2', 'After Hours', '2020-03-20', 'https://i.scdn.co/image/ab67616d0000b2732e8ed79e177ff6011076f5f2'),
    ('al_4', 'a_3', '1989', '2014-10-27', 'https://i.scdn.co/image/ab67616d0000b2732e8ed79e177ff6011076f5f3');

INSERT INTO catalog.tracks (id, album_id, title, duration_ms, track_number) VALUES
    ('t_1', 'al_1', 'Get Lucky', 369000, 1),
    ('t_2', 'al_1', 'Instant Crush', 337000, 2),
    ('t_3', 'al_1', 'Lose Yourself to Dance', 354000, 3),
    ('t_4', 'al_2', 'One More Time', 320000, 1),
    ('t_5', 'al_2', 'Harder, Better, Faster, Stronger', 224000, 2),
    ('t_6', 'al_3', 'Blinding Lights', 200000, 1),
    ('t_7', 'al_3', 'Save Your Tears', 215000, 2),
    ('t_8', 'al_4', 'Shake It Off', 219000, 1),
    ('t_9', 'al_4', 'Blank Space', 231000, 2);
