CREATE TABLE `j_hbn_history__yugioh_card` (
    `revision_id` int(11) NOT NULL,
    `id` binary(16) NOT NULL COMMENT '(DC2Type:ulid)',
    `id_prim` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `types` longtext COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '(DC2Type:json)',
    `level` int(11) DEFAULT NULL,
    `foil` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`,`revision_id`),
    KEY `idx_jhh_yghc_rid` (`revision_id`),
    CONSTRAINT `fk_jhh_yghc_rid` FOREIGN KEY (`revision_id`) REFERENCES `j_hbn_revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `j_hbn_history__yugioh_card_translation` (
    `revision_id` int(11) NOT NULL,
    `id` binary(16) NOT NULL COMMENT '(DC2Type:ulid)',
    `num` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `rarity` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`,`revision_id`),
    KEY `idx_jhh_yghct_rid` (`revision_id`),
    CONSTRAINT `fk_jhh_yghct_rid` FOREIGN KEY (`revision_id`) REFERENCES `j_hbn_revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
