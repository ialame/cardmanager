-- 01GAX5VP57XCNGA7Y6JQ964GTX
INSERT INTO "promo_trait"
    (id, nom_parsable, `type`, `tcg`)
    VALUES(ULID_DECODE('01GAX5VP57XCNGA7Y6JQ964GTX'), 'exclusive', 'exclusive', 'pok');

-- 01G8K53XQ70BKKK3SRV9SEYVTB
INSERT INTO "promo_trait"
    (id, nom_parsable, `type`, `tcg`)
    VALUES(ULID_DECODE('01G8K53XQ70BKKK3SRV9SEYVTB'), 'Cracked Ice Holo', 'holo', 'pok');

-- 01G8K55BW9HD2ZSBCX8QTKTQ77
INSERT INTO "promo_trait"
    (id, nom_parsable, `type`, `tcg`)
    VALUES(ULID_DECODE('01G8K55BW9HD2ZSBCX8QTKTQ77'), 'Cosmos Holo', 'holo', 'pok');
INSERT INTO "promo_trait_translation"
    (id, translatable_id, nom_complet, nom_zebra, locale)
    VALUES(ULID_CREATE(), ULID_DECODE('01G8K55BW9HD2ZSBCX8QTKTQ77'), 'Cosmos Holo', 'Cosmos Holo', 'us');

-- 01GFN7FZ32RKDPEKT26KYF3YGW
INSERT INTO "promo_trait"
    (id, nom_parsable, `type`, `tcg`)
    VALUES(ULID_DECODE('01GFN7FZ32RKDPEKT26KYF3YGW'), 'Pikachu Pumpkin', 'stamp', 'pok');
INSERT INTO "promo_trait_translation"
    (id, translatable_id, nom_complet, nom_zebra, locale)
    VALUES(ULID_CREATE(), ULID_DECODE('01GFN7FZ32RKDPEKT26KYF3YGW'), 'Trick or Trade', 'Trick or Trade', 'us');
INSERT INTO "promo_trait_translation"
    (id, translatable_id, nom_complet, nom_zebra, locale)
    VALUES(ULID_CREATE(), ULID_DECODE('01GFN7FZ32RKDPEKT26KYF3YGW'), 'Trick or Trade', 'Trick or Trade', 'fr');
