-- Existing jobs receive a short description when they are next edited.
ALTER TABLE `job_position` ADD COLUMN `short_description` TEXT NULL;
