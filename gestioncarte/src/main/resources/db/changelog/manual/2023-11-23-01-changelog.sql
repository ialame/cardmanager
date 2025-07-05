ALTER TABLE j_hbn_history__cartepokemons_promosused RENAME j_hbn_history__card_promosused;
ALTER TABLE j_hbn_history__card_promosused CHANGE pokemon_card_id card_id BINARY(16) DEFAULT NULL COMMENT '(DC2Type:ulid)';
ALTER TABLE j_hbn_history__promo_card CHANGE pokemon_card_id card_id BINARY(16) DEFAULT NULL COMMENT '(DC2Type:ulid)';
