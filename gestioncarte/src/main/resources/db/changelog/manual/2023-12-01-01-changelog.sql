ALTER TABLE j_hbn_history__promo_card CHANGE nom_bulbapedia name varchar(255) DEFAULT NULL;
ALTER TABLE j_hbn_history__promo_trait ADD tcg VARCHAR(5) DEFAULT NULL;
ALTER TABLE j_hbn_history__version ADD tcg VARCHAR(5) DEFAULT NULL, CHANGE nom nom VARCHAR(50) DEFAULT NULL;
