-- 01H6ZT76EP0KXMCPBW4K4GTJ6T
insert into serie (id, discriminator)
values (ULID_DECODE('01H6ZT76EP0KXMCPBW4K4GTJ6T'), 'onp');
insert into one_piece_serie (id, id_pca)
values (ULID_DECODE('01H6ZT76EP0KXMCPBW4K4GTJ6T'), NULL);
