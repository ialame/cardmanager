CREATE TABLE `j_hbn_history__evenements_caracteristiques` (
    `revision_id` int(11) NOT NULL,
    `revision_type` tinyint(4) DEFAULT NULL,
    `promo_card_event_id` binary(16) NOT NULL COMMENT '(DC2Type:ulid)',
    `promo_trait_id` binary(16) NOT NULL COMMENT '(DC2Type:ulid)',
    PRIMARY KEY (`promo_card_event_id`, `promo_trait_id`,`revision_id`),
    KEY `idx_jhh_ec_rid` (`revision_id`),
    CONSTRAINT `fk_jhh_ec_rid` FOREIGN KEY (`revision_id`) REFERENCES `j_hbn_revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


insert into j_hbn_history__evenements_caracteristiques select 1 as revision_id, 0 as revision_type, d.* from evenements_caracteristiques as d;
