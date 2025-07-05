CREATE TABLE `j_hbn_history__one_piece_card` (
    `revision_id` int(11) NOT NULL,
    `id` binary(16) NOT NULL COMMENT '(DC2Type:ulid)',
    `id_prim` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `attribute` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `color` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `rarity` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `parallel` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`,`revision_id`),
    KEY `idx_jhh_onpc_rid` (`revision_id`),
    CONSTRAINT `fk_jhh_onpc_rid` FOREIGN KEY (`revision_id`) REFERENCES `j_hbn_revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
