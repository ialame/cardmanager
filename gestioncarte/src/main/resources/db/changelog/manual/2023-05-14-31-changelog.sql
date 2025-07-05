ALTER TABLE j_card_merge_history RENAME j_merge_history;
ALTER TABLE j_merge_history ADD COLUMN `table` VARCHAR(20) NOT NULL;
UPDATE j_merge_history SET `table` = 'pokemon_card';
