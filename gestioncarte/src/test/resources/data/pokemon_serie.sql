-- 01G4GFP9VFTN4NFB3GR0MWX27G
insert into serie (id, discriminator)
values (ULID_DECODE('01G4GFP9VFTN4NFB3GR0MWX27G'), 'pok');
insert into pokemon_serie (id, id_pca)
values (ULID_DECODE('01G4GFP9VFTN4NFB3GR0MWX27G'), NULL);
