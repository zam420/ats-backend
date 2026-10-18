alter table ats.ats_candidate_resume add privacy_notes varchar(500) null;
alter table ats.ats_candidate_resume add interviewers json null;
alter table ats.ats_candidate_resume add expect_onboarding_date date DEFAULT NULL;
ALTER TABLE ats_email_records ADD COLUMN `need_result` tinyint(1) NOT NULL DEFAULT 0;
ALTER TABLE `ats`.`ats_interview_rounds` CHANGE COLUMN `meeting_id` `teams_meeting` json NULL;

create index ats_candida_name_e1b80d_idx on ats_candidate_resume (name);
create index ats_candida_phone_n_40d0c4_idx on ats_candidate (phone_number);
create index ats_candida_email_625af4_idx on ats_candidate (email);
create index ats_candida_univers_fc2158_idx on ats_candidate_education_history (university);
create index ats_candida_company_fc0929_idx on ats_candidate_work_experience (company_name);

INSERT INTO `ats`.`ats_user` (`name`, `email`, `phone`, `password`, `type`, `status`, `join_date`) VALUES ('ATS System', 'ats_test@espressif.com', NULL, NULL, 2, 0, NOW());
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('onboarding_time_watch_dog', '1999-01-01 00:00:00', NULL);

ALTER TABLE `ats`.`ats_notification`
DROP INDEX `ats_notification_user_id_type_trigger_obj_id_task_d8bea25a_uniq`,
ADD INDEX `ats_notification_user_id_type_trigger_obj_id_task_d8bea25a`(`user_id`, `type`, `trigger_obj_id`, `task`) USING BTREE;

CREATE TABLE `ats_exam`
(
    `id`                  bigint        NOT NULL AUTO_INCREMENT,
    `created_at`          datetime(6)   NOT NULL,
    `name`                varchar(255)  NOT NULL,
    `source`              int           NOT NULL,
    `paper_id`            int           NOT NULL,
    `paper_name`          varchar(255)  NOT NULL,
    `test_candidate_id`   int           NOT NULL,
    `test_url`            varchar(4096) NOT NULL,
    `status`              int           NOT NULL,
    `data_status`         int           NOT NULL,
    `score`               float         DEFAULT NULL,
    `paper_score`         float         DEFAULT NULL,
    `cheat_status`        int           DEFAULT NULL,
    `test_time`           datetime(6)   DEFAULT NULL,
    `result_url`          varchar(4096) DEFAULT NULL,
    `candidate_resume_id` bigint        NOT NULL,
    `created_by_id`       bigint        NOT NULL,
    `result_file`         json          DEFAULT NULL,
    `begin_time`          datetime(6)   DEFAULT NULL,
    `end_time`            datetime(6)   DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `ats_exam_candidate_resume_id_66632686_fk_ats_candidate_resume_id` (`candidate_resume_id`),
    KEY `ats_exam_created_by_id_7ebed74c_fk_ats_user_id` (`created_by_id`),
    CONSTRAINT `ats_exam_candidate_resume_id_66632686_fk_ats_candidate_resume_id` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats_candidate_resume` (`id`),
    CONSTRAINT `ats_exam_created_by_id_7ebed74c_fk_ats_user_id` FOREIGN KEY (`created_by_id`) REFERENCES `ats_user` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


