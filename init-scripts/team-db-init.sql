CREATE TABLE IF NOT EXISTS teams (
                                     id          BIGSERIAL PRIMARY KEY,
                                     name        VARCHAR(100) NOT NULL,
    country     VARCHAR(100),
    vrs_points  INT DEFAULT 0,
    photo_url   VARCHAR(255),
    manager_id  BIGINT
    );

CREATE TABLE IF NOT EXISTS players (
                                       id        BIGSERIAL PRIMARY KEY,
                                       name      VARCHAR(100) NOT NULL,
    position  VARCHAR(50),
    age       INT,
    photo_url VARCHAR(255),
    team_id   BIGINT REFERENCES teams(id)
    );

INSERT INTO teams (name, country, vrs_points, photo_url, manager_id) VALUES
                                                                         ('Natus Vincere', 'Ukraine',       845, 'https://img-cdn.hltv.org/teamlogo/ragFtFx6tBd99BECyEb4O7.svg', 1),
                                                                         ('FaZe Clan',     'International', 940, 'https://img-cdn.hltv.org/teamlogo/lntG42KTShas5bA2kNBbHs.svg', 2),
                                                                         ('Team Vitality', 'France',        770, 'https://img-cdn.hltv.org/teamlogo/OPMVna6aRUqaJarCSHkgSl.svg', NULL),
                                                                         ('G2 Esports',    'International', 710, 'https://img-cdn.hltv.org/teamlogo/kGScrEFVSAtSHFTDiWmjde.svg', NULL)
    ON CONFLICT DO NOTHING;

INSERT INTO players (name, position, age, photo_url, team_id) VALUES
                                                                  ('s1mple',     'AWPer',  27, 'https://img-cdn.hltv.org/playerbodyshot/s1mple.png',      1),
                                                                  ('electroNic', 'Rifler', 26, 'https://img-cdn.hltv.org/playerbodyshot/electronic.png',  1),
                                                                  ('b1t',        'Rifler', 22, 'https://img-cdn.hltv.org/playerbodyshot/b1t.png',         1),
                                                                  ('Perfecto',   'Support',25, 'https://img-cdn.hltv.org/playerbodyshot/Perfecto.png',    1),
                                                                  ('npl',        'Support',21, 'https://img-cdn.hltv.org/playerbodyshot/npl.png',         1),
                                                                  ('karrigan',   'IGL',    33, 'https://img-cdn.hltv.org/playerbodyshot/karrigan.png',    2),
                                                                  ('ropz',       'Rifler', 24, 'https://img-cdn.hltv.org/playerbodyshot/ropz.png',        2),
                                                                  ('broky',      'AWPer',  23, 'https://img-cdn.hltv.org/playerbodyshot/broky.png',       2),
                                                                  ('rain',       'Rifler', 30, 'https://img-cdn.hltv.org/playerbodyshot/rain.png',        2),
                                                                  ('HooXi',      'IGL',    28, 'https://img-cdn.hltv.org/playerbodyshot/HooXi.png',       2),
                                                                  ('ZywOo',      'AWPer',  23, 'https://img-cdn.hltv.org/playerbodyshot/ZywOo.png',       3),
                                                                  ('apEX',       'IGL',    32, 'https://img-cdn.hltv.org/playerbodyshot/apex.png',        3),
                                                                  ('dupreeh',    'Rifler', 30, 'https://img-cdn.hltv.org/playerbodyshot/dupreeh.png',     3),
                                                                  ('Spinx',      'Rifler', 23, 'https://img-cdn.hltv.org/playerbodyshot/Spinx.png',       3),
                                                                  ('flameZ',     'Rifler', 22, 'https://img-cdn.hltv.org/playerbodyshot/flamez.png',      3),
                                                                  ('NiKo',       'Rifler', 27, 'https://img-cdn.hltv.org/playerbodyshot/NiKo.png',        4),
                                                                  ('huNter-',    'Rifler', 26, 'https://img-cdn.hltv.org/playerbodyshot/huNter.png',      4),
                                                                  ('jks',        'Rifler', 28, 'https://img-cdn.hltv.org/playerbodyshot/jks.png',         4),
                                                                  ('nexa',       'IGL',    27, 'https://img-cdn.hltv.org/playerbodyshot/nexa.png',        4),
                                                                  ('m0NESY',     'AWPer',  18, 'https://img-cdn.hltv.org/playerbodyshot/m0NESY.png',      4)
    ON CONFLICT DO NOTHING;