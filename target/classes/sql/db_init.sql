USE `ats`;

-- -----------------------------------------------------
-- Table `ats`.`ats_candidate`
-- Model: ats.web.candidate_resume.models.candidate_model.Candidate
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ats`.`ats_candidate` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(500) DEFAULT NULL,
  `gender` int DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `age` int unsigned DEFAULT NULL,
  `phone_number` varchar(16) DEFAULT NULL,
  `phone_country_code` varchar(8) DEFAULT NULL,
  `email` varchar(254) DEFAULT NULL,
  `current_place` varchar(200) DEFAULT NULL,
  `native_place_province` varchar(50) DEFAULT NULL,
  `native_place_city` varchar(50) DEFAULT NULL,
  `employment_status` int DEFAULT NULL,
  `expected_work_location` varchar(100) DEFAULT NULL,
  `expected_salary` varchar(50) DEFAULT NULL,
  `skills` json DEFAULT NULL,
  `languages` json DEFAULT NULL,
  `self_assessment` varchar(2000) DEFAULT NULL,
  `original_resume` json DEFAULT NULL,
  `attachments` json DEFAULT NULL,
  `attachment_links` varchar(1024) DEFAULT NULL,
  `notes` varchar(10240) DEFAULT NULL,
  `tags` json DEFAULT NULL,
  `highest_degree` int DEFAULT NULL,
  `data_status` int NOT NULL DEFAULT 0,
  `create_by_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ats_candida_phone_n_40d0c4_idx` (`phone_number`),
  KEY `ats_candida_email_625af4_idx` (`email`),
  KEY `ats_candidate_create_by_id_fk_ats_user_id` (`create_by_id`),
  CONSTRAINT `ats_candidate_create_by_id_fk_ats_user_id` FOREIGN KEY (`create_by_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `ats`.`ats_candidate_resume`
-- Model: ats.web.candidate_resume.models.candidate_resume_model.CandidateResume
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ats`.`ats_candidate_resume` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `candidate_id` bigint NOT NULL,
  `name` varchar(500) DEFAULT NULL,
  `gender` int DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `age` int unsigned DEFAULT NULL,
  `phone_number` varchar(16) DEFAULT NULL,
  `phone_country_code` varchar(8) DEFAULT NULL,
  `email` varchar(254) DEFAULT NULL,
  `current_place` varchar(200) DEFAULT NULL,
  `native_place_province` varchar(50) DEFAULT NULL,
  `native_place_city` varchar(50) DEFAULT NULL,
  `employment_status` int DEFAULT NULL,
  `recruitment_info_from` int DEFAULT NULL,
  `expected_work_location` varchar(100) DEFAULT NULL,
  `expected_salary` varchar(50) DEFAULT NULL,
  `skills` json DEFAULT NULL,
  `languages` json DEFAULT NULL,
  `self_assessment` varchar(2000) DEFAULT NULL,
  `original_resume` json DEFAULT NULL,
  `attachments` json DEFAULT NULL,
  `attachment_links` varchar(1024) DEFAULT NULL,
  `referral_code` varchar(10) DEFAULT NULL,
  `applying_position_id` bigint DEFAULT NULL,
  `recruitment_source` int DEFAULT NULL,
  `applied_at` datetime(6) NOT NULL,
  `notes` varchar(10240) DEFAULT NULL,
  `status` int NOT NULL DEFAULT 0,
  `sub_status` int DEFAULT NULL,
  `data_status` int NOT NULL DEFAULT 0,
  `create_by_id` bigint DEFAULT NULL,
  `extended_qa` json DEFAULT NULL,
  `highest_degree` int DEFAULT NULL,
  `highest_major` varchar(100) DEFAULT NULL,
  `highest_university` varchar(100) DEFAULT NULL,
  `bachelor_university` varchar(100) DEFAULT NULL,
  `last_company_name` varchar(100) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `management_status` int NOT NULL DEFAULT 0,
  `management_sub_status` int DEFAULT NULL,
  `privacy_notes` varchar(500) DEFAULT NULL,
  `interviewers` json DEFAULT NULL,
  `expect_onboarding_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ats_candida_name_e1b80d_idx` (`name`),
  KEY `ats_candidate_resume_candidate_id_fk` (`candidate_id`),
  KEY `ats_candidate_resume_applying_position_id_fk` (`applying_position_id`),
  KEY `ats_candidate_resume_create_by_id_fk` (`create_by_id`),
  CONSTRAINT `ats_candidate_resume_candidate_id_fk` FOREIGN KEY (`candidate_id`) REFERENCES `ats`.`ats_candidate` (`id`),
  CONSTRAINT `ats_candidate_resume_applying_position_id_fk` FOREIGN KEY (`applying_position_id`) REFERENCES `ats`.`ats_job_position` (`id`),
  CONSTRAINT `ats_candidate_resume_create_by_id_fk` FOREIGN KEY (`create_by_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `ats`.`ats_candidate_work_experience`
-- Model: ats.web.candidate_resume.models.candidate_work_experience_model.CandidateWorkExperience
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ats`.`ats_candidate_work_experience` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `candidate_id` bigint NOT NULL,
  `candidate_resume_id` bigint NOT NULL,
  `date_from` date DEFAULT NULL,
  `date_to` date DEFAULT NULL,
  `up_to_now` tinyint(1) DEFAULT 0,
  `company_name` varchar(100) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `major_achievement` text DEFAULT NULL,
  `work_years` int unsigned DEFAULT NULL,
  `data_status` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `ats_candida_company_fc0929_idx` (`company_name`),
  KEY `ats_candidate_work_exp_candidate_id_fk` (`candidate_id`),
  KEY `ats_candidate_work_exp_candidate_resume_id_fk` (`candidate_resume_id`),
  CONSTRAINT `ats_candidate_work_exp_candidate_id_fk` FOREIGN KEY (`candidate_id`) REFERENCES `ats`.`ats_candidate` (`id`),
  CONSTRAINT `ats_candidate_work_exp_candidate_resume_id_fk` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats`.`ats_candidate_resume` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `ats`.`ats_candidate_education_history`
-- Model: ats.web.candidate_resume.models.candidate_education_history_model.CandidateEducationHistory
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ats`.`ats_candidate_education_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `candidate_id` bigint NOT NULL,
  `candidate_resume_id` bigint NOT NULL,
  `date_from` date DEFAULT NULL,
  `date_to` date DEFAULT NULL,
  `up_to_now` tinyint(1) DEFAULT 0,
  `university` varchar(100) DEFAULT NULL,
  `school` varchar(100) DEFAULT NULL,
  `lab_name` varchar(200) DEFAULT NULL,
  `degree` int DEFAULT NULL,
  `major` varchar(100) DEFAULT NULL,
  `performance_rank` int DEFAULT NULL,
  `data_status` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `ats_candida_univers_fc2158_idx` (`university`),
  KEY `ats_candidate_edu_hist_candidate_id_fk` (`candidate_id`),
  KEY `ats_candidate_edu_hist_candidate_resume_id_fk` (`candidate_resume_id`),
  CONSTRAINT `ats_candidate_edu_hist_candidate_id_fk` FOREIGN KEY (`candidate_id`) REFERENCES `ats`.`ats_candidate` (`id`),
  CONSTRAINT `ats_candidate_edu_hist_candidate_resume_id_fk` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats`.`ats_candidate_resume` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `ats`.`ats_candidate_project_experience`
-- Model: ats.web.candidate_resume.models.candidate_project_experience_model.CandidateProjectExperience
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ats`.`ats_candidate_project_experience` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `candidate_id` bigint NOT NULL,
  `candidate_resume_id` bigint NOT NULL,
  `date_from` date DEFAULT NULL,
  `date_to` date DEFAULT NULL,
  `up_to_now` tinyint(1) DEFAULT 0,
  `project_name` varchar(500) DEFAULT NULL,
  `project_role` varchar(100) DEFAULT NULL,
  `project_desc` varchar(2000) DEFAULT NULL,
  `major_achievement` varchar(3000) DEFAULT NULL,
  `data_status` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `ats_candidate_project_exp_candidate_id_fk` (`candidate_id`),
  KEY `ats_candidate_project_exp_candidate_resume_id_fk` (`candidate_resume_id`),
  CONSTRAINT `ats_candidate_project_exp_candidate_id_fk` FOREIGN KEY (`candidate_id`) REFERENCES `ats`.`ats_candidate` (`id`),
  CONSTRAINT `ats_candidate_project_exp_candidate_resume_id_fk` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats`.`ats_candidate_resume` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
