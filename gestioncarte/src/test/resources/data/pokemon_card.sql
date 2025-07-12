SET REFERENTIAL_INTEGRITY FALSE;

-- 01GF7XQ3V5YMJAHTDBXVKPYD2J
INSERT INTO "card"
    (id, discriminator, card_artist, num, `attributes`, allowed_notes)
    VALUES(ULID_DECODE('01GF7XQ3V5YMJAHTDBXVKPYD2J'), 'pok', NULL /* 0x01821BB8FFE2CC569B7C5E34CF348295 */, '', '{"reverse": 0, "edition": 1, "shadowless": 0}', '[]');
INSERT INTO "pokemon_card"
    (id, has_img, id_prim, id_prim_jap, FA, Type1, Type2, LV, is_distribution_fille, is_crochet_fille, carte_mere_id, status)
    VALUES(ULID_DECODE('01GF7XQ3V5YMJAHTDBXVKPYD2J'), 0, '1000103100', NULL, 0, 'Fire', NULL, 0, 1, 0, NULL /* 0x018120C169C8E4ED4FA09B7288EDAF2D */, 2);

INSERT INTO "card_translation"
    (id, translatable_id, name, label_name, available, locale, discriminator)
    VALUES(X'0183CFDB8F5C5BEEED0C693833874B66', ULID_DECODE('01GF7XQ3V5YMJAHTDBXVKPYD2J'), 'Litwick', 'Litwick', 1, 'us', 'pok');
INSERT INTO "pokemon_card_translation"
    (id, num, original_name, rarity, trainer)
    VALUES(X'0183CFDB8F5C5BEEED0C693833874B66', '031/192', '', 'Promo', '');
INSERT INTO "card_translation"
    (id, translatable_id, name, label_name, available, locale, discriminator)
    VALUES(X'0183CFDB8F5D39C07F9C633D51DE83EB', ULID_DECODE('01GF7XQ3V5YMJAHTDBXVKPYD2J'), 'Lichtel', 'Lichtel', 0, 'de', 'pok');
INSERT INTO "pokemon_card_translation"
    (id, num, original_name, rarity, trainer)
    VALUES(X'0183CFDB8F5D39C07F9C633D51DE83EB', '031/192', '', 'Promo', '');
INSERT INTO "card_translation"
    (id, translatable_id, name, label_name, available, locale, discriminator)
    VALUES(X'0183CFDB8F5DC54F16D2F6FDCC88A88F', ULID_DECODE('01GF7XQ3V5YMJAHTDBXVKPYD2J'), 'Litwick', 'Litwick', 0, 'it', 'pok');
INSERT INTO "pokemon_card_translation"
    (id, num, original_name, rarity, trainer)
    VALUES(X'0183CFDB8F5DC54F16D2F6FDCC88A88F', '031/192', '', 'Promo', '');
INSERT INTO "card_translation"
    (id, translatable_id, name, label_name, available, locale, discriminator)
    VALUES(X'0183CFDB8F5DD34130A61C0DC3E8D417', ULID_DECODE('01GF7XQ3V5YMJAHTDBXVKPYD2J'), 'Litwick', 'Litwick', 0, 'es', 'pok');
INSERT INTO "pokemon_card_translation"
    (id, num, original_name, rarity, trainer)
    VALUES(X'0183CFDB8F5DD34130A61C0DC3E8D417', '031/192', '', 'Promo', '');
INSERT INTO "card_translation"
    (id, translatable_id, name, label_name, available, locale, discriminator)
    VALUES(X'0183CFDB8F5DF4D029A2DFBD6D9A9848', ULID_DECODE('01GF7XQ3V5YMJAHTDBXVKPYD2J'), 'Funécire', 'Funécire', 0, 'fr', 'pok');
INSERT INTO "pokemon_card_translation"
    (id, num, original_name, rarity, trainer)
    VALUES(X'0183CFDB8F5DF4D029A2DFBD6D9A9848', '031/192', '', 'Promo', '');

INSERT INTO promo_card
    (id, name, charset, version_id, promo_card_event_id, card_id)
    VALUES(ULID_DECODE('01GF7XQ3TZ1YD45PFG7PZBKFCW'), 'Pikachu Pumpkin stamp Trick or Trade Halloween BOOster Bundle exclusive', 'us', NULL, NULL, ULID_DECODE('01GF7XQ3V5YMJAHTDBXVKPYD2J'));


-- 01GNF8R70XB5KA5Y2JF1HMVN3M
INSERT INTO "card"
    (id, discriminator, card_artist, num, `attributes`, allowed_notes)
    VALUES(ULID_DECODE('01GNF8R70XB5KA5Y2JF1HMVN3M'), 'pok', NULL /* 0x01821BB8FFE2CC569B7C5E34CF348295 */, '', '{"reverse": 0, "edition": 1, "shadowless": 0}', '[]');
INSERT INTO "pokemon_card"
    (id, has_img, id_prim, id_prim_jap, FA, Type1, Type2, LV, is_distribution_fille, is_crochet_fille, carte_mere_id, status)
    VALUES(ULID_DECODE('01GNF8R70XB5KA5Y2JF1HMVN3M'), 0, '0002700', NULL, 0, 'Fire', NULL, 0, 1, 0, NULL /* 0x018120C169C8E4ED4FA09B7288EDAF2D */, 2);

INSERT INTO "card_translation"
    (id, translatable_id, name, label_name, available, locale, discriminator)
    VALUES(X'018789CC58D7684261AFF9F3E4591E5B', ULID_DECODE('01GNF8R70XB5KA5Y2JF1HMVN3M'), 'Pikachu', 'Pikachu', 1, 'us', 'pok');
INSERT INTO "pokemon_card_translation"
    (id, num, original_name, rarity, trainer)
    VALUES(X'018789CC58D7684261AFF9F3E4591E5B', '027', '', 'Promo', '');
INSERT INTO "card_translation"
    (id, translatable_id, name, label_name, available, locale, discriminator)
    VALUES(X'01855E8C1C1D5966A2F85278634DD473', ULID_DECODE('01GNF8R70XB5KA5Y2JF1HMVN3M'), 'Pikachu', 'Pikachu', 1, 'jp', 'pok');
INSERT INTO "pokemon_card_translation"
    (id, num, original_name, rarity, trainer)
    VALUES(X'01855E8C1C1D5966A2F85278634DD473', '001/SV-P', 'ピカチュウ', 'Promo', '');
INSERT INTO "card_translation"
    (id, translatable_id, name, label_name, available, locale, discriminator)
    VALUES(X'018789CC58D7684261AFF9F3E4591E5C', ULID_DECODE('01GNF8R70XB5KA5Y2JF1HMVN3M'), 'Pikachu', 'Pikachu', 1, 'fr', 'pok');
INSERT INTO "pokemon_card_translation"
    (id, num, original_name, rarity, trainer)
    VALUES(X'018789CC58D7684261AFF9F3E4591E5C', '027', '', 'Promo', '');

INSERT INTO promo_card
    (id, name, charset, version_id, promo_card_event_id, card_id)
    VALUES(ULID_DECODE('01GY4WRP7AZNE7PZ3YYVD36NRR'), 'Paldea Evolved Elite Trainer Box', 'us', NULL, NULL, ULID_DECODE('01GNF8R70XB5KA5Y2JF1HMVN3M'));
INSERT INTO promo_card
    (id, name, charset, version_id, promo_card_event_id, card_id)
    VALUES(ULID_DECODE('01GY4WRP7AZNE7PZ3YYVD36NRS'), 'Pokémon Scarlet and Violet early purchase bonus (November 18, 2022)', 'jp', NULL, NULL, ULID_DECODE('01GNF8R70XB5KA5Y2JF1HMVN3M'));


-- 01GF7XQ3HGWB37XJYVVZE3QGS7
INSERT INTO "card"
    (id, discriminator, card_artist, num, `attributes`, allowed_notes)
    VALUES(ULID_DECODE('01GF7XQ3HGWB37XJYVVZE3QGS7'), 'pok', NULL /* 0x01821BB8FFE2CC569B7C5E34CF348295 */, '', '{"reverse": 0, "edition": 1, "shadowless": 0}', '[]');
INSERT INTO "pokemon_card"
    (id, has_img, id_prim, id_prim_jap, FA, Type1, Type2, LV, is_distribution_fille, is_crochet_fille, carte_mere_id, status)
    VALUES(ULID_DECODE('01GF7XQ3HGWB37XJYVVZE3QGS7'), 0, '', NULL, 0, 'Fire', NULL, 0, 0, 0, NULL /* 0x018120C169C8E4ED4FA09B7288EDAF2D */, 2);

INSERT INTO "card_translation"
    (id, translatable_id, name, label_name, available, locale, discriminator)
    VALUES(ULID_DECODE('01H5W2ENSQZV4HY7DN7TD4T21W'), ULID_DECODE('01GF7XQ3HGWB37XJYVVZE3QGS7'), 'Corviknight ', 'Corviknight ', 1, 'us', 'pok');
INSERT INTO "pokemon_card_translation"
    (id, num, original_name, rarity, trainer)
    VALUES(ULID_DECODE('01H5W2ENSQZV4HY7DN7TD4T21W'), '156/189', '', 'Promo', '');

INSERT INTO promo_card
    (id, name, charset, version_id, promo_card_event_id, card_id)
    VALUES(ULID_DECODE('01H5W2EWJGMKT60WP8915VHECR'), 'Cosmos Holo "Darkness Ablaze" stamp gift with purchase exclusive (various countries)', 'us', NULL, NULL, ULID_DECODE('01GF7XQ3HGWB37XJYVVZE3QGS7'));

INSERT INTO card_card_set
    (card_id, card_set_id)
    values(ULID_DECODE('01GF7XQ3HGWB37XJYVVZE3QGS7'), ULID_DECODE('01G4GEQXEX2X6291YJRM1RQVM2'));


SET REFERENTIAL_INTEGRITY TRUE;
