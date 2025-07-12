ALTER TABLE `j_hbn_history__j_card_extraction_status` modify `target_id` binary(16) DEFAULT NULL COMMENT '(DC2Type:ulid)';
ALTER TABLE `j_hbn_history__j_card_extraction_status` modify `ignored_locales` longtext DEFAULT NULL COMMENT '(DC2Type:json)';

ALTER TABLE `j_hbn_history__j_feature_translation_pattern` modify `feature_id` binary(16) DEFAULT NULL COMMENT '(DC2Type:ulid)';
ALTER TABLE `j_hbn_history__j_feature_translation_pattern` modify `feature_translation_id` binary(16) DEFAULT NULL COMMENT '(DC2Type:ulid)';
