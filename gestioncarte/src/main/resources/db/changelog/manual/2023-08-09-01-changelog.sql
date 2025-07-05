CREATE TABLE `j_hbn_history__lorcana_card` (
    `revision_id` int(11) NOT NULL,
    `id` binary(16) NOT NULL COMMENT '(DC2Type:ulid)',
    `id_prim` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `rarity` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `ink` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `reprint` tinyint(1) DEFAULT NULL,
    PRIMARY KEY (`id`,`revision_id`),
    KEY `idx_jhh_lorc_rid` (`revision_id`),
    CONSTRAINT `fk_jhh_lorc_rid` FOREIGN KEY (`revision_id`) REFERENCES `j_hbn_revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `j_hbn_history__lorcana_card_translation` (
    `revision_id` int(11) NOT NULL,
    `id` binary(16) NOT NULL COMMENT '(DC2Type:ulid)',
    `num` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `full_number` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`,`revision_id`),
    KEY `idx_jhh_lorct_rid` (`revision_id`),
    CONSTRAINT `fk_jhh_lorct_rid` FOREIGN KEY (`revision_id`) REFERENCES `j_hbn_revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
