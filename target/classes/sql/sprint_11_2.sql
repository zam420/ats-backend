CREATE TABLE `ats_user_settings`
(
    `id`       bigint NOT NULL AUTO_INCREMENT,
    `type`     int    NOT NULL,
    `settings` json DEFAULT NULL,
    `user_id`  bigint NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ats_user_settings_user_id_type_9288c507_uniq` (`user_id`, `type`),
    CONSTRAINT `ats_user_settings_user_id_90e1bbe6_fk_ats_user_id` FOREIGN KEY (`user_id`) REFERENCES `ats_user` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

alter table ats.ats_candidate_resume add column `phone_country_code` varchar(8) default null;
alter table ats.ats_candidate add column `phone_country_code` varchar(8) default null;

alter table ats.ats_email_template modify `recruitment_type` json not null;
alter table ats.ats_email_template modify `work_location` json not null;
update ats.ats_email_template et
set et.recruitment_type=concat('[', et.recruitment_type, ']'),
    et.work_location=concat('[', et.work_location, ']')
where id>0;

alter table ats.ats_exam add column job_position_id bigint null;
alter table ats.ats_exam
    add constraint ats_exam_job_position_id_8aa3c446_fk_ats_job_position_id
        foreign key (job_position_id) references ats_job_position (id);
update ats.ats_exam ae
set ae.job_position_id=(select cr.applying_position_id
                        from ats.ats_candidate_resume cr
                        where ae.candidate_resume_id = cr.id)
where ae.job_position_id is null;


-- Merge round status
UPDATE ats_interview_rounds SET status = 2 WHERE status = 3;

-- Add column to ats_interview_rounds
ALTER TABLE `ats`.`ats_interview_rounds` ADD COLUMN `round_code` varchar(32) NOT NULL;

-- Generate code for history data
UPDATE ats_interview_rounds a
INNER JOIN (
	SELECT
		itv_r.id AS round_id,
	CASE
			WHEN interview_type = 0 THEN
			CONCAT(
				'ITVS',
				FLOOR( UNIX_TIMESTAMP( itv_r.created_at ) * 1000 ),
				LPAD( itv_r.created_by_id, 5, '0' ),
				LPAD( itv.target_job_id, 5, '0' ),
			LPAD( itv_r.id, 5, '0' ))
			WHEN interview_type = 1 THEN
			CONCAT(
				'ITVG',
				FLOOR( UNIX_TIMESTAMP( itv_r.created_at ) * 1000 ),
				LPAD( itv_r.created_by_id, 5, '0' ),
				LPAD( itv.target_job_id, 5, '0' ),
			LPAD( itv_r.id, 5, '0' ))
		END AS round_code
	FROM
		ats_interview_rounds itv_r
		INNER JOIN ats_interview_record itv ON itv_r.interview_id = itv.id
) t on a.id = t.round_id
SET a.round_code =t.round_code;

-- Uppercase first latter of user name
UPDATE ats_user
SET name = CONCAT(UPPER(LEFT(name, 1)), LOWER(SUBSTRING(name, 2)))
WHERE name IS NOT NULL;

-- Add column to ats_job_position
ALTER TABLE `ats`.`ats_job_position`
ADD COLUMN `public_interview_feedback` tinyint NOT NULL DEFAULT 0;

-- Add column to ats_candidate_resume_forward
ALTER TABLE `ats`.`ats_candidate_resume_forward`
ADD COLUMN `resume_position_id` bigint DEFAULT NULL,
ADD CONSTRAINT `ats_candidate_resume_forward_fk_job_position` FOREIGN KEY (`resume_position_id`) REFERENCES `ats`.`ats_job_position` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;
-- fill new column value
update ats_candidate_resume_forward arf inner join ats_candidate_resume acr on arf.candidate_resume_id = acr.id
set arf.resume_position_id = acr.applying_position_id;

INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='uc'),'uc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='ue'),'ue');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='umm'),'umm');
