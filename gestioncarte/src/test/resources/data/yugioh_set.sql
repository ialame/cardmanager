-- 01HK4QEGK7JXKZSGCWQZ902QZA
insert into card_set
(id, serie_id, discriminator, parent_id, ap_mention)
values (ULID_DECODE('01HK4QEGK7JXKZSGCWQZ902QZA'), ULID_DECODE('01GVQJARHMG0WQBZ7HRAVR1PCS'), 'ygh', NULL, '');
insert into yugioh_set
(id, promo, id_pca, type)
values (ULID_DECODE('01HK4QEGK7JXKZSGCWQZ902QZA'), false, 0, 'Collectible tin');

INSERT INTO card_set_translation
(id, translatable_id, name, label_name, release_date, available, locale, discriminator)
VALUES (ULID_DECODE('01HKYHF1RYZSC1SK70Q0ZMRB7Z'), ULID_DECODE('01HK4QEGK7JXKZSGCWQZ902QZA'), '2022 Tin of the Pharaoh''s Gods', '2022 Tin of the Pharaoh''s Gods', '2022-09-16', true, 'us', 'ygh');
INSERT INTO yugioh_set_translation
(id, code)
VALUES (ULID_DECODE('01HKYHF1RYZSC1SK70Q0ZMRB7Z'), 'MP22');

INSERT INTO card_set_translation
(id, translatable_id, name, label_name, release_date, available, locale, discriminator)
VALUES (ULID_DECODE('01HKYHQCVZFEJ9H0D4S3MX9Q5W'), ULID_DECODE('01HK4QEGK7JXKZSGCWQZ902QZA'), '2022 Lata de los Dioses del Faraón', '2022 Lata de los Dioses del Faraón', '2022-09-15', true, 'es', 'ygh');
INSERT INTO yugioh_set_translation
(id, code)
VALUES (ULID_DECODE('01HKYHQCVZFEJ9H0D4S3MX9Q5W'), 'MP22');

INSERT INTO card_set_translation
(id, translatable_id, name, label_name, release_date, available, locale, discriminator)
VALUES (ULID_DECODE('01HKYHS5HMY1MR4KMMTB1C5PC9'), ULID_DECODE('01HK4QEGK7JXKZSGCWQZ902QZA'), 'Boîte des Dieux du Pharaon 2022', 'Boîte des Dieux du Pharaon 2022', '2022-09-15', true, 'fr', 'ygh');
INSERT INTO yugioh_set_translation
(id, code)
VALUES (ULID_DECODE('01HKYHS5HMY1MR4KMMTB1C5PC9'), 'MP22');

INSERT INTO card_set_translation
(id, translatable_id, name, label_name, release_date, available, locale, discriminator)
VALUES (ULID_DECODE('01HKYHZ8BYEDP2N9BSFDKTJTY8'), ULID_DECODE('01HK4QEGK7JXKZSGCWQZ902QZA'), '2022 Tin of the Pharaoh''s Gods', '2022 Tin of the Pharaoh''s Gods', '2022-09-15', true, 'de', 'ygh');
INSERT INTO yugioh_set_translation
(id, code)
VALUES (ULID_DECODE('01HKYHZ8BYEDP2N9BSFDKTJTY8'), 'MP22');

INSERT INTO card_set_translation
(id, translatable_id, name, label_name, release_date, available, locale, discriminator)
VALUES (ULID_DECODE('01HKYJ1ZAW8KTVMA4NBJXCSS21'), ULID_DECODE('01HK4QEGK7JXKZSGCWQZ902QZA'), 'Tin delle Divinità del Faraone 2022', 'Tin delle Divinità del Faraone 2022', '2022-09-15', true, 'it', 'ygh');
INSERT INTO yugioh_set_translation
(id, code)
VALUES (ULID_DECODE('01HKYJ1ZAW8KTVMA4NBJXCSS21'), 'MP22');

INSERT INTO card_set_translation
(id, translatable_id, name, label_name, release_date, available, locale, discriminator)
VALUES (ULID_DECODE('01HKYJ3JJVQZ7778QWEHP0JRPJ'), ULID_DECODE('01HK4QEGK7JXKZSGCWQZ902QZA'), 'Lata dos Deuses do Faraó 2022', 'Lata dos Deuses do Faraó 2022', '2022-09-15', true, 'pt', 'ygh');
INSERT INTO yugioh_set_translation
(id, code)
VALUES (ULID_DECODE('01HKYJ3JJVQZ7778QWEHP0JRPJ'), 'MP22');

INSERT INTO j_yugipedia_set (url, localization, set_id)
VALUES ('Set_Card_Lists:2022_Tin_of_the_Pharaoh''s_Gods_(TCG-EN)', 'us', ULID_DECODE('01HK4QEGK7JXKZSGCWQZ902QZA'));
INSERT INTO j_yugipedia_set (url, localization, set_id)
VALUES ('Set_Card_Lists:2022_Tin_of_the_Pharaoh''s_Gods_(TCG-FR)', 'fr', ULID_DECODE('01HK4QEGK7JXKZSGCWQZ902QZA'));
INSERT INTO j_yugipedia_set (url, localization, set_id)
VALUES ('Set_Card_Lists:2022_Tin_of_the_Pharaoh''s_Gods_(TCG-DE)', 'de', ULID_DECODE('01HK4QEGK7JXKZSGCWQZ902QZA'));
INSERT INTO j_yugipedia_set (url, localization, set_id)
VALUES ('Set_Card_Lists:2022_Tin_of_the_Pharaoh''s_Gods_(TCG-IT)', 'it', ULID_DECODE('01HK4QEGK7JXKZSGCWQZ902QZA'));
INSERT INTO j_yugipedia_set (url, localization, set_id)
VALUES ('Set_Card_Lists:2022_Tin_of_the_Pharaoh''s_Gods_(TCG-PT)', 'pt', ULID_DECODE('01HK4QEGK7JXKZSGCWQZ902QZA'));
INSERT INTO j_yugipedia_set (url, localization, set_id)
VALUES ('Set_Card_Lists:2022_Tin_of_the_Pharaoh''s_Gods_(TCG-ES)', 'es', ULID_DECODE('01HK4QEGK7JXKZSGCWQZ902QZA'));

INSERT INTO j_yugioh_official_site_pid (pid, localization, set_id)
VALUES  ('2000001195000', 'us', ULID_DECODE('01HK4QEGK7JXKZSGCWQZ902QZA'));
INSERT INTO j_yugioh_official_site_pid (pid, localization, set_id)
VALUES  ('2000001195000', 'fr', ULID_DECODE('01HK4QEGK7JXKZSGCWQZ902QZA'));
INSERT INTO j_yugioh_official_site_pid (pid, localization, set_id)
VALUES  ('2000001195000', 'de', ULID_DECODE('01HK4QEGK7JXKZSGCWQZ902QZA'));
INSERT INTO j_yugioh_official_site_pid (pid, localization, set_id)
VALUES  ('2000001195000', 'es', ULID_DECODE('01HK4QEGK7JXKZSGCWQZ902QZA'));
INSERT INTO j_yugioh_official_site_pid (pid, localization, set_id)
VALUES  ('2000001195000', 'pt', ULID_DECODE('01HK4QEGK7JXKZSGCWQZ902QZA'));
INSERT INTO j_yugioh_official_site_pid (pid, localization, set_id)
VALUES  ('2000001195000', 'it', ULID_DECODE('01HK4QEGK7JXKZSGCWQZ902QZA'));
