ALTER TABLE `ats`.`ats_candidate_resume` ADD COLUMN `highest_major` varchar(100) DEFAULT NULL;
ALTER TABLE `ats`.`ats_candidate_resume` ADD COLUMN `highest_university` varchar(100) DEFAULT NULL;
ALTER TABLE `ats`.`ats_candidate_resume` ADD COLUMN `bachelor_university` varchar(100) DEFAULT NULL;
ALTER TABLE `ats`.`ats_candidate_resume` ADD COLUMN `last_company_name` varchar(100) DEFAULT NULL;


ALTER TABLE `ats`.`ats_resume_duplication` MODIFY COLUMN `user_id` bigint NULL;

create table ats_resume_duplication_task
(
    id          bigint auto_increment
        primary key,
    create_time datetime(6) not null,
    resume_id   int         not null
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

insert into `ats`.`ats_task_record` (task_id, record, sub_id)
values ('resume_duplication_schedule', '2000-01-01 00:00:00', NULL);


ALTER TABLE `ats`.`ats_app_file` MODIFY COLUMN `upload_user_id` bigint DEFAULT NULL;

ALTER TABLE `ats`.`ats_candidate_resume` CHARACTER SET = utf8mb4;
ALTER TABLE `ats`.`ats_candidate` CHARACTER SET = utf8mb4;
ALTER TABLE `ats`.`ats_candidate_education_history` CHARACTER SET = utf8mb4;
ALTER TABLE `ats`.`ats_candidate_project_experience` CHARACTER SET = utf8mb4;
ALTER TABLE `ats`.`ats_candidate_work_experience` CHARACTER SET = utf8mb4;
ALTER TABLE `ats`.`ats_interview_feedback_template` CHARACTER SET = utf8mb4;
ALTER TABLE `ats`.`ats_interview_process_template` CHARACTER SET = utf8mb4;
ALTER TABLE `ats`.`ats_talent_pool` CHARACTER SET = utf8mb4;

ALTER TABLE `ats`.`ats_candidate` MODIFY COLUMN `notes` varchar(10240);
ALTER TABLE `ats`.`ats_candidate_resume` MODIFY COLUMN `notes` varchar(10240);
