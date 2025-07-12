SET REFERENTIAL_INTEGRITY FALSE;

-- 01GXGPC560410PARKG79ZRA6G6
INSERT INTO "card"
(id, discriminator, card_artist, num, `attributes`, allowed_notes)
VALUES(ULID_DECODE('01GXGPC560410PARKG79ZRA6G6'), 'onp', NULL /* 0x01821BB8FFE2CC569B7C5E34CF348295 */, 'OP01-001', '{}', '[]');
INSERT INTO "one_piece_card"
(id, id_prim, type, attribute, color, rarity, parallel)
VALUES(ULID_DECODE('01GXGPC560410PARKG79ZRA6G6'), NULL, 'LEADER', 'Slash', 'Red', 'L', 0);

INSERT INTO "card_translation"
(id, translatable_id, name, label_name, available, locale, discriminator)
VALUES(X'018B4CDE8E1A9BF4B210A8F5A5504AA5', ULID_DECODE('01GXGPC560410PARKG79ZRA6G6'), 'Roronoa Zoro', 'Roronoa Zoro', 1, 'us', 'bas');

INSERT INTO card_card_set
(card_id, card_set_id)
values(ULID_DECODE('01GXGPC560410PARKG79ZRA6G6'), ULID_DECODE('01GXGP22SM1HXDR9GJW9NQDCB4'));


-- 01HHPRNVVHDWTWV8K8NRWCTT9B
INSERT INTO "card"
(id, discriminator, card_artist, num, `attributes`, allowed_notes)
VALUES(ULID_DECODE('01HHPRNVVHDWTWV8K8NRWCTT9B'), 'onp', NULL /* 0x01821BB8FFE2CC569B7C5E34CF348295 */, 'P-045', '{}', '[]');
INSERT INTO "one_piece_card"
(id, id_prim, type, attribute, color, rarity, parallel)
VALUES(ULID_DECODE('01HHPRNVVHDWTWV8K8NRWCTT9B'), NULL, 'LEADER', 'Slash', 'Red', 'L', 0);

INSERT INTO "card_translation"
(id, translatable_id, name, label_name, available, locale, discriminator)
VALUES(ULID_DECODE('01HHPRR00SP7PCEJ6QKDJPFX14'), ULID_DECODE('01HHPRNVVHDWTWV8K8NRWCTT9B'), 'Roronoa Zoro', 'Roronoa Zoro', 1, 'jp', 'bas');

INSERT INTO card_card_set
(card_id, card_set_id)
values(ULID_DECODE('01HHPRNVVHDWTWV8K8NRWCTT9B'), ULID_DECODE('01GXGNKR5Z10BFY8VKDNK18E82'));

INSERT INTO promo_card
(id, name, charset, version_id, promo_card_event_id, card_id)
VALUES(ULID_DECODE('01HHPRSJBNWC4PAG115R2GBRYK'), 'Prize for October 2023 Meet-up event', 'jp', NULL, NULL, ULID_DECODE('01HHPRNVVHDWTWV8K8NRWCTT9B'));

SET REFERENTIAL_INTEGRITY TRUE;
