-- Add System Virtual User
INSERT INTO ats.ats_user(id, public_id, first_name, last_name, email, phone, password, type, status, join_date) VALUES (1, 'V1StGXR8_Z5j', 'ATS System', 'System', 'ats@espressif.com', NULL, NULL, 2, 0, NOW());

# Users
insert into ats.ats_user(id, public_id, first_name, last_name, email, type, status, join_date) values (2, 'V1StGXR8_Z5k', 'Admin', 'User', 'ats_admin@fsworkpaper.ai', 0, 0, current_time);
insert into ats.ats_user(id, public_id, first_name, last_name, email, type, status, join_date) values (3, 'V1StGXR8_Z5l', 'HRD 1', 'User', 'hrd1@fsworkpaper.ai', 0, 0, current_time);
insert into ats.ats_user(id, public_id, first_name, last_name, email, type, status, join_date) values (4, 'V1StGXR8_Z5m', 'Project Admin 1', 'User', 'pr_admin1@fsworkpaper.ai', 0, 0, current_time);
insert into ats.ats_user(id, public_id, first_name, last_name, email, type, status, join_date) values (5, 'V1StGXR8_Z5n', 'Rec 1', 'User', 'ats_rec1@fsworkpaper.ai', 0, 0, current_time);
insert into ats.ats_user(id, public_id, first_name, last_name, email, type, status, join_date) values (6, 'V1StGXR8_Z5o', 'Rec 2', 'User', 'ats_rec2@fsworkpaper.ai', 0, 0, current_time);
insert into ats.ats_user(id, public_id, first_name, last_name, email, type, status, join_date) values (7, 'V1StGXR8_Z5p', 'Project Admin 2', 'User', 'pr_admin2@fsworkpaper.ai', 0, 0, current_time);
insert into ats.ats_user(id, public_id, first_name, last_name, email, type, status, join_date) values (8, 'V1StGXR8_Z5q', 'Rec 3', 'User', 'ats_rec3@fsworkpaper.ai', 0, 0, current_time);
insert into ats.ats_user(id, public_id, first_name, last_name, email, type, status, join_date) values (9, 'V1StGXR8_Z5r', 'Rec Coordinator 1', 'User', 'ats_rec_coord1@fsworkpaper.ai', 0, 0, current_time);
insert into ats.ats_user(id, public_id, first_name, last_name, email, type, status, join_date) values (10, 'V1StGXR8_Z5s', 'Rec Coordinator 2', 'User', 'ats_rec_coord2@fsworkpaper.ai', 0, 0, current_time);
insert into ats.ats_user(id, public_id, first_name, last_name, email, type, status, join_date) values (11, 'V1StGXR8_Z5t', 'HRD 2', 'User', 'hrd2@fsworkpaper.ai', 0, 0, current_time);
insert into ats.ats_user(id, public_id, first_name, last_name, email, type, status, join_date) values (12, 'V1StGXR8_Z5u', 'Rec 4', 'User', 'ats_rec4@fsworkpaper.ai', 0, 0, current_time);
insert into ats.ats_user(id, public_id, first_name, last_name, email, type, status, join_date) values (13, 'V1StGXR8_Z5v', 'Rec 5', 'User', 'ats_rec5@fsworkpaper.ai', 0, 0, current_time);

# User Roles
insert into ats.ats_user_role(user_id, role_id, role_code) values (2, 1, 'sa');
insert into ats.ats_user_role(user_id, role_id, role_code) values (2, 7, 'itv');
insert into ats.ats_user_role(user_id, role_id, role_code) values (3, 2, 'hrd');
insert into ats.ats_user_role(user_id, role_id, role_code) values (3, 7, 'itv');
insert into ats.ats_user_role(user_id, role_id, role_code) values (4, 3, 'proa');
insert into ats.ats_user_role(user_id, role_id, role_code) values (4, 4, 'hrr');
insert into ats.ats_user_role(user_id, role_id, role_code) values (4, 7, 'itv');
insert into ats.ats_user_role(user_id, role_id, role_code) values (5, 4, 'hrr');
insert into ats.ats_user_role(user_id, role_id, role_code) values (5, 7, 'itv');
insert into ats.ats_user_role(user_id, role_id, role_code) values (6, 4, 'hrr');
insert into ats.ats_user_role(user_id, role_id, role_code) values (6, 7, 'itv');
insert into ats.ats_user_role(user_id, role_id, role_code) values (7, 3, 'proa');
insert into ats.ats_user_role(user_id, role_id, role_code) values (7, 4, 'hrr');
insert into ats.ats_user_role(user_id, role_id, role_code) values (7, 7, 'itv');
insert into ats.ats_user_role(user_id, role_id, role_code) values (8, 4, 'hrr');
insert into ats.ats_user_role(user_id, role_id, role_code) values (8, 7, 'itv');
insert into ats.ats_user_role(user_id, role_id, role_code) values (9, 5, 'hrrc');
insert into ats.ats_user_role(user_id, role_id, role_code) values (9, 7, 'itv');
insert into ats.ats_user_role(user_id, role_id, role_code) values (10, 5, 'hrrc');
insert into ats.ats_user_role(user_id, role_id, role_code) values (10, 7, 'itv');
insert into ats.ats_user_role(user_id, role_id, role_code) values (11, 2, 'hrd');
insert into ats.ats_user_role(user_id, role_id, role_code) values (11, 3, 'proa');
insert into ats.ats_user_role(user_id, role_id, role_code) values (11, 7, 'itv');
insert into ats.ats_user_role(user_id, role_id, role_code) values (12, 4, 'hrr');
insert into ats.ats_user_role(user_id, role_id, role_code) values (12, 7, 'itv');
insert into ats.ats_user_role(user_id, role_id, role_code) values (13, 4, 'hrr');
insert into ats.ats_user_role(user_id, role_id, role_code) values (13, 7, 'itv');



-- Add System Tasks
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('email_resume_parser', NOW(), 'recruit@espressif.com');
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('email_resume_parser', NOW(), 'campus@espressif.com');
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('email_resume_parser', NOW(), 'campus.in@espressif.com');
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('email_resume_parser', NOW(), 'recruit.in@espressif.com');
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('email_resume_parser', NOW(), 'recruit.sgpr@espressif.com');
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('email_resume_parser', NOW(), 'recruit.cz@espressif.com');
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('email_resume_parser', NOW(), 'recruit.br@espressif.com');
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('email_resume_parser', NOW(), 'recruit.jp@espressif.com');
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('resume_duplication_schedule', NOW(), NULL);
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('interview_meeting_rsp_handler_task', NOW(), NULL);
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('exam_update_nowcoder_schedule', NOW(), NULL);
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('auto_tag_resumes_for_fresh_resumes', NOW(), NULL);
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('auto_tag_resumes_for_fresh_tags', NOW(), NULL);
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('email_task_schedule', '1', NULL);
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('interview_time_task', '1', NULL);
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('onboarding_time_watch_dog', '1', NULL);
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('mail_counter_init', '1', NULL);

-- history resume's tag management
INSERT IGNORE INTO ats_tag(`name`,`created_by_id`,`created_at`,`updated_at`)
SELECT
    DISTINCT(JSON_UNQUOTE(JSON_EXTRACT(tags,CONCAT('$[', help_topic.help_topic_id, ']')))) AS `name`,t.id as `created_by_id`,NOW() as `created_at`,NOW() as `updated_at`
FROM
    ats_candidate
    JOIN mysql.help_topic ON help_topic.help_topic_id < JSON_LENGTH(tags)
		JOIN (select id from ats_user where type=2 limit 1)t
WHERE
    JSON_VALID(tags) AND JSON_LENGTH(tags);


INSERT INTO ats_resume_tags(match_type,candidate_resume_id,tag_id,match_count)
SELECT 0,t.resume_id,ats_tag.id,1 FROM(
    SELECT ats_candidate_resume.id AS resume_id,JSON_UNQUOTE(JSON_EXTRACT(ats_candidate.tags,CONCAT('$[', help_topic.help_topic_id, ']'))) AS tag_name
    FROM ats_candidate_resume
        JOIN mysql.help_topic ON help_topic.help_topic_id < JSON_LENGTH(ats_candidate.tags)
        INNER JOIN ats_candidate ON ats_candidate_resume.candidate_id = ats_candidate.id
    WHERE JSON_VALID(ats_candidate.tags) AND JSON_LENGTH(ats_candidate.tags)
) t INNER JOIN ats_tag ON t.tag_name = ats_tag.`name`;

