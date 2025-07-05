CREATE TABLE `j_hbn_history__yugioh_set_translation` (
    `revision_id` int(11) NOT NULL,
    `id` binary(16) NOT NULL COMMENT '(DC2Type:ulid)',
    `code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`,`revision_id`),
    KEY `idx_jhh_yghst_rid` (`revision_id`),
    CONSTRAINT `fk_jhh_yghst_rid` FOREIGN KEY (`revision_id`) REFERENCES `j_hbn_revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
