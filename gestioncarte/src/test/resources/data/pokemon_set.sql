-- 01G4GEQXEX2X6291YJRM1RQVM2
insert into card_set
    (id, serie_id, discriminator, parent_id, ap_mention)
    values (ULID_DECODE('01G4GEQXEX2X6291YJRM1RQVM2'), ULID_DECODE('01G4GFP9VFTN4NFB3GR0MWX27G'), 'pok', NULL, '');
insert into pokemon_set
    (id, is_promo, id_pca, num_sur, nom_raccourci)
    values (ULID_DECODE('01G4GEQXEX2X6291YJRM1RQVM2'), false, 0, '189', 'swsh3');

INSERT INTO card_set_translation
    (id, translatable_id, name, label_name, release_date, available, locale, discriminator)
    VALUES (ULID_DECODE('01H5W3367SDGTEFGWTPT412VGZ'), ULID_DECODE('01G4GEQXEX2X6291YJRM1RQVM2'), 'Darkness Ablaze', 'Darkness Ablaze', '2020-08-14', true, 'us', 'pok');
INSERT INTO pokemon_set_translation
    (id, original_name)
    VALUES (ULID_DECODE('01H5W3367SDGTEFGWTPT412VGZ'), '');
