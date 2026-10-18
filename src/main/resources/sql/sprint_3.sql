ALTER TABLE `ats`.`ats_candidate` MODIFY COLUMN `create_by_id` bigint NULL;
ALTER TABLE `ats`.`ats_candidate_resume` MODIFY COLUMN `create_by_id` bigint NULL;
ALTER TABLE `ats`.`ats_candidate` MODIFY COLUMN `notes` varchar(2080) NULL DEFAULT NULL;
ALTER TABLE `ats`.`ats_candidate_resume` MODIFY COLUMN `notes` varchar(2080) NULL DEFAULT NULL;
ALTER TABLE `ats`.`ats_candidate_work_experience` ADD COLUMN `up_to_now` tinyint(1) DEFAULT NULL;
ALTER TABLE `ats`.`ats_candidate_education_history` ADD COLUMN `up_to_now` tinyint(1) DEFAULT NULL;
ALTER TABLE `ats`.`ats_candidate_project_experience` ADD COLUMN `up_to_now` tinyint(1) DEFAULT NULL;
ALTER TABLE `ats`.`ats_candidate` ADD COLUMN `age` int unsigned DEFAULT NULL;
ALTER TABLE `ats`.`ats_candidate_resume` ADD COLUMN `age` int unsigned DEFAULT NULL;
ALTER TABLE `ats`.`ats_candidate_resume` MODIFY COLUMN `original_resume` json NULL;
ALTER TABLE `ats`.`ats_candidate` MODIFY COLUMN `original_resume` json NULL;

CREATE TABLE `ats_task_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` varchar(225) NOT NULL,
  `record` varchar(225) NOT NULL,
  `sub_id` varchar(225) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_task_record_task_id_sub_id_17e0811d_uniq` (`task_id`,`sub_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `ats_resume_duplication` (
                                          `id` bigint NOT NULL AUTO_INCREMENT,
                                          `check_time` datetime(6) NOT NULL,
                                          `operations` json NOT NULL,
                                          `duplicate_resumes` json NOT NULL,
                                          `candidate_resume_id` bigint NOT NULL,
                                          `user_id` bigint NOT NULL,
                                          PRIMARY KEY (`id`),
                                          KEY `ats_resume_duplicati_candidate_resume_id_3367537a_fk_ats_candi` (`candidate_resume_id`),
                                          KEY `ats_resume_duplication_user_id_10574f98_fk_ats_user_id` (`user_id`),
                                          CONSTRAINT `ats_resume_duplicati_candidate_resume_id_3367537a_fk_ats_candi` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats_candidate_resume` (`id`),
                                          CONSTRAINT `ats_resume_duplication_user_id_10574f98_fk_ats_user_id` FOREIGN KEY (`user_id`) REFERENCES `ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

