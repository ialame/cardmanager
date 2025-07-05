CREATE TABLE `j_hbn_history__version` (
    `revision_id` int(11) NOT NULL,
    `revision_type` tinyint(4) DEFAULT NULL,
    `id` binary(16) NOT NULL COMMENT '(DC2Type:ulid)',
    `nom` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
    `hidden` tinyint(1) DEFAULT NULL,
    PRIMARY KEY (`id`,`revision_id`),
    KEY `idx_jhh_v_rid` (`revision_id`),
    CONSTRAINT `fk_jhh_v_rid` FOREIGN KEY (`revision_id`) REFERENCES `j_hbn_revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `j_hbn_history__version_translation` (
    `revision_id` int(11) NOT NULL,
    `revision_type` tinyint(4) DEFAULT NULL,
    `id` binary(16) NOT NULL COMMENT '(DC2Type:ulid)',
    `translatable_id` binary(16) DEFAULT NULL COMMENT '(DC2Type:ulid)',
    `nom_complet` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `nom_zebra` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
    `locale` varchar(5) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`,`revision_id`),
    KEY `idx_jhh_vt_rid` (`revision_id`),
    CONSTRAINT `fk_jhh_vt_rid` FOREIGN KEY (`revision_id`) REFERENCES `j_hbn_revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into j_hbn_history__version select 1 as revision_id, 0 as revision_type, d.* from version as d;
insert into j_hbn_history__version_translation select 1 as revision_id, 0 as revision_type, d.* from version_translation as d;
