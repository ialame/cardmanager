-- 01GXGP22SM1HXDR9GJW9NQDCB4
insert into card_set
(id, serie_id, discriminator, parent_id, ap_mention)
values (ULID_DECODE('01GXGP22SM1HXDR9GJW9NQDCB4'), ULID_DECODE('01H6ZT76EP0KXMCPBW4K4GTJ6T'), 'onp', NULL, '');
insert into one_piece_set
(id, id_pca, promo, code)
values (ULID_DECODE('01GXGP22SM1HXDR9GJW9NQDCB4'), 0,  false, 'OP01');

INSERT INTO card_set_translation
(id, translatable_id, name, label_name, release_date, available, locale, discriminator)
VALUES (ULID_DECODE('01HD6FFEZTYFQ9FGVTKJ2REFQ0'), ULID_DECODE('01GXGP22SM1HXDR9GJW9NQDCB4'), 'Romance Dawn', 'Romance Dawn', '2022-12-01', true, 'jp', 'bas');

-- 01H1RZ6HHYCG2MXBZSZE7TDAB0
insert into card_set
(id, serie_id, discriminator, parent_id, ap_mention)
values (ULID_DECODE('01H1RZ6HHYCG2MXBZSZE7TDAB0'), ULID_DECODE('01H6ZT76EP0KXMCPBW4K4GTJ6T'), 'onp', NULL, '');
insert into one_piece_set
(id, id_pca, promo, code)
values (ULID_DECODE('01H1RZ6HHYCG2MXBZSZE7TDAB0'), 0,  false, 'OP04');

INSERT INTO card_set_translation
(id, translatable_id, name, label_name, release_date, available, locale, discriminator)
VALUES (ULID_DECODE('01H6ZTEZ9DXDB11B6J7EYTGQXW'), ULID_DECODE('01H1RZ6HHYCG2MXBZSZE7TDAB0'), 'Kingdoms Of Intrigue', 'Kingdoms Of Intrigue', '2023-05-27', true, 'jp', 'bas');

-- 01GXGNKR5Z10BFY8VKDNK18E82
insert into card_set
(id, serie_id, discriminator, parent_id, ap_mention)
values (ULID_DECODE('01GXGNKR5Z10BFY8VKDNK18E82'), ULID_DECODE('01H6ZT76EP0KXMCPBW4K4GTJ6T'), 'onp', NULL, '');
insert into one_piece_set
(id, id_pca, promo, code)
values (ULID_DECODE('01GXGNKR5Z10BFY8VKDNK18E82'), 0,  false, 'P');

INSERT INTO card_set_translation
(id, translatable_id, name, label_name, release_date, available, locale, discriminator)
VALUES (ULID_DECODE('01HHPRVN99XKAE55MBYB9D8CJX'), ULID_DECODE('01GXGNKR5Z10BFY8VKDNK18E82'), 'Promotional Card', 'Promotional Card', '2022-12-01', true, 'jp', 'bas');
