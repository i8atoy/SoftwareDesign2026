CREATE TABLE IF NOT EXISTS tournaments (
                                           id          BIGSERIAL PRIMARY KEY,
                                           name        VARCHAR(100) NOT NULL,
    prize_money DOUBLE PRECISION DEFAULT 0,
    location    VARCHAR(100),
    vrs_points  INT DEFAULT 0
    );

CREATE TABLE IF NOT EXISTS tournament_team_ids (
                                                   tournament_id BIGINT REFERENCES tournaments(id),
    team_id       BIGINT,
    PRIMARY KEY (tournament_id, team_id)
    );

INSERT INTO tournaments (name, prize_money, location, vrs_points) VALUES
                                                                      ('IEM Cologne 2024',          1000000, 'Cologne, Germany',    3750),
                                                                      ('BLAST Premier World Final',  500000, 'Copenhagen, Denmark', 2500),
                                                                      ('ESL Pro League S20',         750000, 'Malta',               3000),
                                                                      ('PGL Major Copenhagen',      1250000, 'Copenhagen, Denmark', 5000)
    ON CONFLICT DO NOTHING;

INSERT INTO tournament_team_ids (tournament_id, team_id) VALUES (1,1),(1,2),(2,1),(2,2),(2,3),(2,4),(3,3),(3,4)
    ON CONFLICT DO NOTHING;