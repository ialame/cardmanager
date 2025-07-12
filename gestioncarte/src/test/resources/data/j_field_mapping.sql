INSERT INTO "j_field_mapping" ("field", "regex", "source", "value") VALUES('test.1', 1, '(?i)test', 'foo');
INSERT INTO "j_field_mapping" ("field", "regex", "source", "value") VALUES('test.2', 1, 'test', 'foo');
INSERT INTO "j_field_mapping" ("field", "regex", "source", "value") VALUES('test.3', 0, 'test', 'foo');

INSERT INTO "j_field_mapping" ("field", "regex", "source", "value") VALUES('cards.ygh.rarity', 1, '(?i)Ultra Rare \(Pharaoh''s Rare\)', 'URPR');
INSERT INTO "j_field_mapping" ("field", "regex", "source", "value") VALUES('cards.ygh.rarity', 1, '(?i)UR \((PR|RF)\)', 'URPR');
INSERT INTO "j_field_mapping" ("field", "regex", "source", "value") VALUES('cards.ygh.rarity', 1, '(?i)^PSE$', 'PScR');
