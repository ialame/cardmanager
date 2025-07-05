-- 01G4GFP9VFTN4NFB3GR0MWX27G
insert into serie (id, discriminator)
values (ULID_DECODE('01GVQJARHMG0WQBZ7HRAVR1PCS'), 'pok');
insert into yugioh_serie (id, id_pca)
values (ULID_DECODE('01GVQJARHMG0WQBZ7HRAVR1PCS'), NULL);
