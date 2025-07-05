-- Pokemon

INSERT INTO j_field_mapping (`field`, `regex`, `source`, `value`) VALUES('cards.pok.rarity', 1, '(?i)^Radiant Rare$', 'Rare Radiant');


-- Yu-Gi-Oh!

INSERT INTO j_field_mapping (`field`, `regex`, `source`, `value`) VALUES('cards.ygh.rarity', 1, '(?i)^Super Rare$', 'SR');

INSERT INTO j_field_mapping (`field`, `regex`, `source`, `value`) VALUES('cards.ygh.rarity', 1, '(?i)^Ultra Rare \\(Pharaoh''s Rare\\)$', 'URPR');
INSERT INTO j_field_mapping (`field`, `regex`, `source`, `value`) VALUES('cards.ygh.rarity', 1, '(?i)^UR \\((PR|RF)\\)$', 'URPR');

INSERT INTO j_field_mapping (`field`, `regex`, `source`, `value`) VALUES('cards.ygh.it.rarity', 1, '(?i)^QSSE$', 'QCSE');

INSERT INTO j_field_mapping (`field`, `regex`, `source`, `value`) VALUES('cards.ygh.rarity', 1, '(?i)^GH$', 'GR');
INSERT INTO j_field_mapping (`field`, `regex`, `source`, `value`) VALUES('cards.ygh.es.rarity', 1, '(?i)^FA$', 'GR');
INSERT INTO j_field_mapping (`field`, `regex`, `source`, `value`) VALUES('cards.ygh.pt.rarity', 1, '(?i)^FA$', 'GR');

INSERT INTO j_field_mapping (`field`, `regex`, `source`, `value`) VALUES('cards.ygh.it.rarity', 1, '(?i)^RC$', 'CR');
INSERT INTO j_field_mapping (`field`, `regex`, `source`, `value`) VALUES('cards.ygh.es.rarity', 1, '(?i)^RC$', 'CR');
INSERT INTO j_field_mapping (`field`, `regex`, `source`, `value`) VALUES('cards.ygh.pt.rarity', 1, '(?i)^RC$', 'CR');

INSERT INTO j_field_mapping (`field`, `regex`, `source`, `value`) VALUES('cards.ygh.rarity', 1, '(?i)^UL$', 'UtR');
INSERT INTO j_field_mapping (`field`, `regex`, `source`, `value`) VALUES('cards.ygh.sp.rarity', 1, '(?i)^DE$', 'UtR');

INSERT INTO j_field_mapping (`field`, `regex`, `source`, `value`) VALUES('cards.ygh.jp.rarity', 1, '(?i)^N$', 'C');
INSERT INTO j_field_mapping (`field`, `regex`, `source`, `value`) VALUES('cards.ygh.kr.rarity', 1, '(?i)^N$', 'C');

INSERT INTO j_field_mapping (`field`, `regex`, `source`, `value`) VALUES('cards.ygh.rarity', 1, '(?i)^SE$', 'ScR');
