-- Required for job title natural-language full-text search.
ALTER TABLE `job_position` ADD FULLTEXT INDEX `ft_job_position_title` (`title`);
