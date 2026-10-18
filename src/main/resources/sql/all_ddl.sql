-- =====================================================================
-- Database Initialization: Complete ATS DDL Script (all_ddl.sql)
-- Target Database Engine: MySQL 8.0+
-- Default Engine: InnoDB | Character Set: utf8mb4 | Collation: utf8mb4_0900_ai_ci
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `ats` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `ats`;

SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================================
-- 1. USER, ROLE & PERMISSION MANAGEMENT
-- =====================================================================

-- Table: ats_user
-- Model: ats.web.user.models.user_model.User
CREATE TABLE `ats_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `phone` varchar(64) DEFAULT NULL,
  `password` varchar(256) DEFAULT NULL,
  `type` int NOT NULL DEFAULT '0',
  `status` int NOT NULL DEFAULT '0',
  `join_date` datetime(6) NOT NULL,
  `activation_code` varchar(255) DEFAULT NULL,
  `activation_code_sent_date_time` datetime(6) DEFAULT NULL,
  `activation_date_time` datetime(6) DEFAULT NULL,
  `password_reset_code` varchar(255) DEFAULT NULL,
  `password_reset_code_sent_date_time` datetime(6) DEFAULT NULL,
  `creation_date_time` datetime(6) DEFAULT NULL,
  `credentials_expired` tinyint(1) DEFAULT '0',
  `deactivated` tinyint(1) DEFAULT '0',
  `first_name` varchar(50),
  `is_google_login` tinyint(1) DEFAULT '0',
  `last_name` varchar(50),
  `locked` tinyint(1) DEFAULT '0',
  `locked_by_user_id` bigint DEFAULT NULL,
  `locked_by_user_name` varchar(255) DEFAULT NULL,
  `locked_date_time` datetime(6) DEFAULT NULL,
  `modified_date_time` datetime(6) DEFAULT NULL,
  `public_id` varchar(12) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `FK550x3vphiuy87lc3f31nq6pbk` (`locked_by_user_id`),
  CONSTRAINT `FK550x3vphiuy87lc3f31nq6pbk` FOREIGN KEY (`locked_by_user_id`) REFERENCES `ats_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- Table: ats_role
-- Model: ats.web.user.models.role_model.Role
CREATE TABLE IF NOT EXISTS `ats`.`ats_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(32) NOT NULL,
  `name` varchar(256) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_permission
-- Model: ats.web.user.models.permission_model.Permission
CREATE TABLE IF NOT EXISTS `ats`.`ats_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(32) NOT NULL,
  `name` varchar(256) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_user_role
-- Model: ats.web.user.models.user_role_model.UserRole
CREATE TABLE IF NOT EXISTS `ats`.`ats_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `role_code` varchar(32) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_user_role_user_id_role_id_uniq` (`user_id`, `role_id`),
  KEY `ats_user_role_role_id_fk` (`role_id`),
  CONSTRAINT `ats_user_role_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `ats`.`ats_user` (`id`),
  CONSTRAINT `ats_user_role_role_id_fk` FOREIGN KEY (`role_id`) REFERENCES `ats`.`ats_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_role_permission
-- Model: ats.web.user.models.role_permission_model.RolePermission
CREATE TABLE IF NOT EXISTS `ats`.`ats_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  `permission_code` varchar(32) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_role_permission_role_id_permission_id_uniq` (`role_id`, `permission_id`),
  KEY `ats_role_permission_permission_id_fk` (`permission_id`),
  KEY `ats_role_permission_permission_code_fk` (`permission_code`),
  CONSTRAINT `ats_role_permission_role_id_fk` FOREIGN KEY (`role_id`) REFERENCES `ats`.`ats_role` (`id`),
  CONSTRAINT `ats_role_permission_permission_id_fk` FOREIGN KEY (`permission_id`) REFERENCES `ats`.`ats_permission` (`id`),
  CONSTRAINT `ats_role_permission_permission_code_fk` FOREIGN KEY (`permission_code`) REFERENCES `ats`.`ats_permission` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_user_settings
-- Model: ats.web.user.models.user_settings_model.UserSettings
CREATE TABLE IF NOT EXISTS `ats`.`ats_user_settings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` int NOT NULL,
  `settings` json DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_user_settings_user_id_type_9288c507_uniq` (`user_id`, `type`),
  CONSTRAINT `ats_user_settings_user_id_90e1bbe6_fk_ats_user_id` FOREIGN KEY (`user_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 2. COMMON UTILITIES, FILES, TAGS & AUDIT LOGS
-- =====================================================================

-- Table: ats_app_file
-- Model: ats.web.common.models.file_model.AppFile
CREATE TABLE IF NOT EXISTS `ats`.`ats_app_file` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `file_name` varchar(512) NOT NULL,
  `file_path` varchar(512) NOT NULL,
  `upload_user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ats_app_file_upload_user_id_fk` (`upload_user_id`),
  CONSTRAINT `ats_app_file_upload_user_id_fk` FOREIGN KEY (`upload_user_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_tag
-- Model: ats.web.common.models.tag_model.TagModel
CREATE TABLE IF NOT EXISTS `ats`.`ats_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `created_by_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `ats_tag_created_by_id_7cea5ef4_fk_ats_user_id` (`created_by_id`),
  CONSTRAINT `ats_tag_created_by_id_7cea5ef4_fk_ats_user_id` FOREIGN KEY (`created_by_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_task_record
-- Model: ats.web.common.models.task_record_model.TaskRecord
CREATE TABLE IF NOT EXISTS `ats`.`ats_task_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` varchar(225) NOT NULL,
  `record` varchar(225) NOT NULL,
  `sub_id` varchar(225) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_task_record_task_id_sub_id_17e0811d_uniq` (`task_id`, `sub_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_api_token
-- Model: ats.web.common.models.ats_api_token_model.AtsApiToken
CREATE TABLE IF NOT EXISTS `ats`.`ats_api_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `token` varchar(512) NOT NULL,
  `apis` json NOT NULL,
  `status` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_table_header_config
-- Model: ats.web.common.models.table_header_config_model.TableHeaderConfig
CREATE TABLE IF NOT EXISTS `ats`.`ats_table_header_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `display` tinyint(1) NOT NULL,
  `table` varchar(100) NOT NULL,
  `field` varchar(100) NOT NULL,
  `sort` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_table_header_config_user_table_field_uniq` (`user_id`, `table`, `field`),
  CONSTRAINT `ats_table_header_config_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_table_button_config
-- Model: ats.web.common.models.table_button_config_model.TableButtonConfigModel
CREATE TABLE IF NOT EXISTS `ats`.`ats_table_button_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `state` varchar(100) NOT NULL,
  `table` varchar(100) NOT NULL,
  `button` varchar(100) NOT NULL,
  `sort` int NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_table_button_config_uniq` (`user_id`, `table`, `state`, `button`),
  CONSTRAINT `ats_table_button_config_user_id_fk_ats_user_id` FOREIGN KEY (`user_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_history
-- Model: ats.web.history.models.history_model.History
CREATE TABLE IF NOT EXISTS `ats`.`ats_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `target_model` varchar(100) NOT NULL,
  `target_id` int NOT NULL,
  `is_subject` tinyint(1) NOT NULL DEFAULT 1,
  `history` json NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_history_target_model_target_id_is_subject_uniq` (`target_model`, `target_id`, `is_subject`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_notification
-- Model: ats.web.notification.models.notification_model.Notification
CREATE TABLE IF NOT EXISTS `ats`.`ats_notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `type` int NOT NULL,
  `trigger_obj_id` int NOT NULL,
  `message` varchar(1024) NOT NULL,
  `extend_param` json DEFAULT NULL,
  `create_time` datetime(6) NOT NULL,
  `notify_time` datetime(6) NOT NULL,
  `task` tinyint(1) NOT NULL,
  `complete` tinyint(1) NOT NULL,
  `completion_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ats_notification_user_id_type_trigger_obj_id_task_d8bea25a` (`user_id`, `type`, `trigger_obj_id`, `task`),
  CONSTRAINT `ats_notification_user_id_fk_ats_user_id` FOREIGN KEY (`user_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 3. EMAIL TEMPLATES, LOGS & BACKGROUND TASKS
-- =====================================================================

-- Table: ats_email_template
-- Model: ats.web.email_template.models.email_template_model.EmailTemplate
CREATE TABLE IF NOT EXISTS `ats`.`ats_email_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `subject` varchar(255) NOT NULL,
  `content` varchar(5000) NOT NULL,
  `recruitment_type` json NOT NULL,
  `work_location` json NOT NULL,
  `notes` varchar(2000) DEFAULT NULL,
  `create_time` datetime(6) NOT NULL,
  `last_modified_time` datetime(6) DEFAULT NULL,
  `status` int NOT NULL,
  `fields` json DEFAULT NULL,
  `create_by_id` bigint NOT NULL,
  `last_modified_by_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ats_email_template_create_by_id_7d1f6eb5_fk_ats_user_id` (`create_by_id`),
  KEY `ats_email_template_last_modified_by_id_f9f33197_fk_ats_user_id` (`last_modified_by_id`),
  CONSTRAINT `ats_email_template_create_by_id_7d1f6eb5_fk_ats_user_id` FOREIGN KEY (`create_by_id`) REFERENCES `ats`.`ats_user` (`id`),
  CONSTRAINT `ats_email_template_last_modified_by_id_f9f33197_fk_ats_user_id` FOREIGN KEY (`last_modified_by_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_email_template_sharing
-- Model: ats.web.email_template.models.email_template_sharing_model.EmailTemplateSharing
CREATE TABLE IF NOT EXISTS `ats`.`ats_email_template_sharing` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `update_time` datetime(6) NOT NULL,
  `status` int NOT NULL,
  `share_from_id` bigint NOT NULL,
  `share_to_id` bigint NOT NULL,
  `template_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ats_email_template_sharing_share_from_id_d592bba0_fk_ats_user_id` (`share_from_id`),
  KEY `ats_email_template_sharing_share_to_id_30d22190_fk_ats_user_id` (`share_to_id`),
  KEY `ats_email_template_s_template_id_ad53468a_fk_ats_email` (`template_id`),
  CONSTRAINT `ats_email_template_s_template_id_ad53468a_fk_ats_email` FOREIGN KEY (`template_id`) REFERENCES `ats`.`ats_email_template` (`id`),
  CONSTRAINT `ats_email_template_sharing_share_from_id_d592bba0_fk_ats_user_id` FOREIGN KEY (`share_from_id`) REFERENCES `ats`.`ats_user` (`id`),
  CONSTRAINT `ats_email_template_sharing_share_to_id_30d22190_fk_ats_user_id` FOREIGN KEY (`share_to_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_email_records
-- Model: ats.web.common.models.ats_mail_record.AtsEmailRecords
CREATE TABLE IF NOT EXISTS `ats`.`ats_email_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `to` json NOT NULL,
  `cc` json DEFAULT NULL,
  `bcc` json DEFAULT NULL,
  `subject` varchar(1024) DEFAULT NULL,
  `body` longtext NOT NULL,
  `trigger_models` json NOT NULL,
  `status` int NOT NULL,
  `remark` varchar(225) DEFAULT 'success',
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `from_user_id` bigint DEFAULT NULL,
  `is_html_mail` tinyint(1) NOT NULL DEFAULT 0,
  `mail_type` int NOT NULL,
  `need_result` tinyint(1) NOT NULL DEFAULT 0,
  `scheduled_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ats_email_records_from_user_id_048bb526_fk_ats_user_id` (`from_user_id`),
  KEY `ats_email_records_status_scheduled_time_fb3c5ebc_idx` (`status`, `scheduled_time`),
  KEY `ats_email_records_mail_type_32346fca` (`mail_type`),
  CONSTRAINT `ats_email_records_from_user_id_048bb526_fk_ats_user_id` FOREIGN KEY (`from_user_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_email_records_attachments (M2M)
CREATE TABLE IF NOT EXISTS `ats`.`ats_email_records_attachments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `atsemailrecords_id` bigint NOT NULL,
  `appfile_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_email_records_attach_atsemailrecords_id_appfi_199b65c2_uniq` (`atsemailrecords_id`, `appfile_id`),
  KEY `ats_email_records_at_appfile_id_ffb2e2d4_fk_ats_app_f` (`appfile_id`),
  CONSTRAINT `ats_email_records_at_appfile_id_ffb2e2d4_fk_ats_app_f` FOREIGN KEY (`appfile_id`) REFERENCES `ats`.`ats_app_file` (`id`),
  CONSTRAINT `ats_email_records_at_atsemailrecords_id_a9bdf0f0_fk_ats_email` FOREIGN KEY (`atsemailrecords_id`) REFERENCES `ats`.`ats_email_records` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_email_counter
-- Model: ats.web.common.models.ats_mail_counter.AtsEmailCounter
CREATE TABLE IF NOT EXISTS `ats`.`ats_email_counter` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `counter_tag` varchar(128) NOT NULL,
  `mail_count` int NOT NULL DEFAULT 0,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `counter_tag` (`counter_tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_email_task
CREATE TABLE IF NOT EXISTS `ats`.`ats_email_task` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `to` json NOT NULL,
  `cc` json DEFAULT NULL,
  `bcc` json DEFAULT NULL,
  `trigger_model_id` json NOT NULL,
  `handler` varchar(225) NOT NULL,
  `status` int NOT NULL,
  `remark` varchar(225) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_email_task_attachments (M2M)
CREATE TABLE IF NOT EXISTS `ats`.`ats_email_task_attachments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `emailtask_id` bigint NOT NULL,
  `appfile_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_email_task_attachments_emailtask_id_appfile_id_cd02b837_uniq` (`emailtask_id`, `appfile_id`),
  KEY `ats_email_task_attac_appfile_id_16ef0eb9_fk_ats_app_f` (`appfile_id`),
  CONSTRAINT `ats_email_task_attac_appfile_id_16ef0eb9_fk_ats_app_f` FOREIGN KEY (`appfile_id`) REFERENCES `ats`.`ats_app_file` (`id`),
  CONSTRAINT `ats_email_task_attac_emailtask_id_56b330ea_fk_ats_email` FOREIGN KEY (`emailtask_id`) REFERENCES `ats`.`ats_email_task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 4. INTERVIEW TEMPLATES
-- =====================================================================

-- Table: ats_interview_feedback_template
-- Model: ats.web.interview.models.interview_feedback_template_model.InterviewFeedbackTemplate
CREATE TABLE IF NOT EXISTS `ats`.`ats_interview_feedback_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_interview_process_template
-- Model: ats.web.interview.models.interview_process_template_model.InterviewProcessTemplate
CREATE TABLE IF NOT EXISTS `ats`.`ats_interview_process_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `work_location` int NOT NULL,
  `notes` varchar(100) DEFAULT NULL,
  `status` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `created_by_id` bigint NOT NULL,
  `modified_by_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ats_interview_proces_created_by_id_3be35bf7_fk_ats_user_` (`created_by_id`),
  KEY `ats_interview_proces_modified_by_id_948261da_fk_ats_user_` (`modified_by_id`),
  CONSTRAINT `ats_interviewproces_created_by_id_3be35bf7_fk_ats_user_` FOREIGN KEY (`created_by_id`) REFERENCES `ats`.`ats_user` (`id`),
  CONSTRAINT `ats_interview_proces_modified_by_id_948261da_fk_ats_user_` FOREIGN KEY (`modified_by_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_interview_process_template_share_to (M2M)
CREATE TABLE IF NOT EXISTS `ats`.`ats_interview_process_template_share_to` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `interviewprocesstemplate_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_interview_process_te_interviewprocesstemplate_391e1c71_uniq` (`interviewprocesstemplate_id`, `user_id`),
  KEY `ats_interview_proces_user_id_d86e0cd8_fk_ats_user_` (`user_id`),
  CONSTRAINT `ats_interview_proces_interviewprocesstemp_06adabff_fk_ats_inter` FOREIGN KEY (`interviewprocesstemplate_id`) REFERENCES `ats`.`ats_interview_process_template` (`id`),
  CONSTRAINT `ats_interview_proces_user_id_d86e0cd8_fk_ats_user_` FOREIGN KEY (`user_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_interview_round_template
-- Model: ats.web.interview.models.interview_round_template_model.InterviewRoundTemplate
CREATE TABLE IF NOT EXISTS `ats`.`ats_interview_round_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `round_type` int NOT NULL,
  `interviewers` json DEFAULT NULL,
  `order` int NOT NULL,
  `ipt_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_interview_round_template_ipt_id_order_82b26685_uniq` (`ipt_id`, `order`),
  CONSTRAINT `ats_interview_round__ipt_id_ce3dccaa_fk_ats_inter` FOREIGN KEY (`ipt_id`) REFERENCES `ats`.`ats_interview_process_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 5. RECRUITMENT PROJECTS & JOB POSITIONS
-- =====================================================================

-- Table: ats_recruitment_project
-- Model: ats.web.recruitment_project.models.recruitment_project_model.RecruitmentProject
CREATE TABLE IF NOT EXISTS `ats`.`ats_recruitment_project` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `status` int NOT NULL DEFAULT 0,
  `work_location` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `ats_recruitment_project_created_by_id_fk` (`created_by_id`),
  CONSTRAINT `ats_recruitment_project_created_by_id_fk` FOREIGN KEY (`created_by_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_recruitment_project_admins (M2M)
CREATE TABLE IF NOT EXISTS `ats`.`ats_recruitment_project_admins` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `recruitmentproject_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_recruitment_project_admins_project_user_uniq` (`recruitmentproject_id`, `user_id`),
  KEY `ats_recruitment_project_admins_user_id_fk` (`user_id`),
  CONSTRAINT `ats_recruitment_project_admins_recruitmentproject_id_fk` FOREIGN KEY (`recruitmentproject_id`) REFERENCES `ats`.`ats_recruitment_project` (`id`),
  CONSTRAINT `ats_recruitment_project_admins_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_recruitment_project_ipt (M2M)
CREATE TABLE IF NOT EXISTS `ats`.`ats_recruitment_project_ipt` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `recruitmentproject_id` bigint NOT NULL,
  `interviewprocesstemplate_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_recruitment_project__recruitmentproject_id_in_21ffae96_uniq` (`recruitmentproject_id`, `interviewprocesstemplate_id`),
  KEY `ats_recruitment_proj_interviewprocesstemp_6fbe9f90_fk_ats_inter` (`interviewprocesstemplate_id`),
  CONSTRAINT `ats_recruitment_proj_interviewprocesstemp_6fbe9f90_fk_ats_inter` FOREIGN KEY (`interviewprocesstemplate_id`) REFERENCES `ats`.`ats_interview_process_template` (`id`),
  CONSTRAINT `ats_recruitment_proj_recruitmentproject_i_7331cb38_fk_ats_recru` FOREIGN KEY (`recruitmentproject_id`) REFERENCES `ats`.`ats_recruitment_project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_job_position
-- Model: ats.web.job_position.models.job_position_model.JobPosition
CREATE TABLE IF NOT EXISTS `ats`.`ats_job_position` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `project_id` bigint NOT NULL,
  `recruitment_type` int NOT NULL,
  `employment_type` int NOT NULL,
  `job_type` int NOT NULL,
  `work_location` json NOT NULL,
  `job_desc` varchar(5000) NOT NULL,
  `job_req` varchar(5000) NOT NULL,
  `hr_recruiter_id` bigint NOT NULL,
  `resume_inbox` varchar(254) NOT NULL,
  `publish_channel` json DEFAULT NULL,
  `created_by_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `published_at` datetime(6) DEFAULT NULL,
  `status` int NOT NULL DEFAULT 0,
  `extended_questions` json DEFAULT NULL,
  `public_interview_feedback` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_job_position_title_project_id_uniq` (`title`, `project_id`),
  KEY `ats_job_position_project_id_fk` (`project_id`),
  KEY `ats_job_position_hr_recruiter_id_fk` (`hr_recruiter_id`),
  KEY `ats_job_position_created_by_id_fk` (`created_by_id`),
  CONSTRAINT `ats_job_position_project_id_fk` FOREIGN KEY (`project_id`) REFERENCES `ats`.`ats_recruitment_project` (`id`),
  CONSTRAINT `ats_job_position_hr_recruiter_id_fk` FOREIGN KEY (`hr_recruiter_id`) REFERENCES `ats`.`ats_user` (`id`),
  CONSTRAINT `ats_job_position_created_by_id_fk` FOREIGN KEY (`created_by_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_job_position_hr_coordinators (M2M)
CREATE TABLE IF NOT EXISTS `ats`.`ats_job_position_hr_coordinators` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `jobposition_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_job_position_hr_coordinators_jobposition_user_uniq` (`jobposition_id`, `user_id`),
  KEY `ats_job_position_hr_coordinators_user_id_fk` (`user_id`),
  CONSTRAINT `ats_job_position_hr_coordinators_jobposition_id_fk` FOREIGN KEY (`jobposition_id`) REFERENCES `ats`.`ats_job_position` (`id`),
  CONSTRAINT `ats_job_position_hr_coordinators_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_job_position_interview_process_template (M2M)
CREATE TABLE IF NOT EXISTS `ats`.`ats_job_position_interview_process_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `jobposition_id` bigint NOT NULL,
  `interviewprocesstemplate_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_job_position_intervi_jobposition_id_interview_f80ba055_uniq` (`jobposition_id`, `interviewprocesstemplate_id`),
  KEY `ats_job_position_int_interviewprocesstemp_68821354_fk_ats_inter` (`interviewprocesstemplate_id`),
  CONSTRAINT `ats_job_position_int_interviewprocesstemp_68821354_fk_ats_inter` FOREIGN KEY (`interviewprocesstemplate_id`) REFERENCES `ats`.`ats_interview_process_template` (`id`),
  CONSTRAINT `ats_job_position_int_jobposition_id_6127bfdb_fk_ats_job_p` FOREIGN KEY (`jobposition_id`) REFERENCES `ats`.`ats_job_position` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_job_position_interview_feedback_template (M2M)
CREATE TABLE IF NOT EXISTS `ats`.`ats_job_position_interview_feedback_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `jobposition_id` bigint NOT NULL,
  `interviewfeedbacktemplate_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_job_position_ift_jobposition_feedback_uniq` (`jobposition_id`, `interviewfeedbacktemplate_id`),
  KEY `ats_job_position_ift_feedback_id_fk` (`interviewfeedbacktemplate_id`),
  CONSTRAINT `ats_job_position_ift_jobposition_id_fk` FOREIGN KEY (`jobposition_id`) REFERENCES `ats`.`ats_job_position` (`id`),
  CONSTRAINT `ats_job_position_ift_feedback_id_fk` FOREIGN KEY (`interviewfeedbacktemplate_id`) REFERENCES `ats`.`ats_interview_feedback_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 6. CANDIDATES & RESUMES
-- =====================================================================

-- Table: ats_candidate
-- Model: ats.web.candidate_resume.models.candidate_model.Candidate
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

-- Table: ats_candidate_resume
-- Model: ats.web.candidate_resume.models.candidate_resume_model.CandidateResume
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

-- Table: ats_candidate_work_experience
-- Model: ats.web.candidate_resume.models.candidate_work_experience_model.CandidateWorkExperience
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

-- Table: ats_candidate_education_history
-- Model: ats.web.candidate_resume.models.candidate_education_history_model.CandidateEducationHistory
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

-- Table: ats_candidate_project_experience
-- Model: ats.web.candidate_resume.models.candidate_project_experience_model.CandidateProjectExperience
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

-- Table: ats_candidate_resume_offer
-- Model: ats.web.candidate_resume.models.candidate_resume_offer_model.CandidateResumeOffer
CREATE TABLE IF NOT EXISTS `ats`.`ats_candidate_resume_offer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `feedback_status` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `candidate_resume_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `candidate_resume_id` (`candidate_resume_id`),
  CONSTRAINT `ats_candidate_resume_candidate_resume_id_63e01b4d_fk_ats_candi` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats`.`ats_candidate_resume` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_candidate_resume_forward
-- Model: ats.web.candidate_resume.models.candidate_resume_forward.CandidateResumeForward
CREATE TABLE IF NOT EXISTS `ats`.`ats_candidate_resume_forward` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `status` int NOT NULL,
  `forward_notes` varchar(2048) DEFAULT NULL,
  `feedback_notes` varchar(2048) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `candidate_resume_id` bigint NOT NULL,
  `resume_position_id` bigint DEFAULT NULL,
  `from_user_id` bigint NOT NULL,
  `to_user_id` bigint NOT NULL,
  `create_by_id` bigint NOT NULL,
  `update_by_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ats_candidate_resume_candidate_resume_id_50774a19_fk_ats_candi` (`candidate_resume_id`),
  KEY `ats_candidate_resume_forward_resume_pos_fk` (`resume_position_id`),
  KEY `ats_candidate_resume_create_by_id_c726cded_fk_ats_user_` (`create_by_id`),
  KEY `ats_candidate_resume_from_user_id_ebd7785b_fk_ats_user_` (`from_user_id`),
  KEY `ats_candidate_resume_forward_to_user_id_fd42db8c_fk_ats_user_id` (`to_user_id`),
  KEY `ats_candidate_resume_update_by_id_4fe5c81f_fk_ats_user_` (`update_by_id`),
  CONSTRAINT `ats_candidate_resume_candidate_resume_id_50774a19_fk_ats_candi` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats`.`ats_candidate_resume` (`id`),
  CONSTRAINT `ats_candidate_resume_forward_fk_job_position` FOREIGN KEY (`resume_position_id`) REFERENCES `ats`.`ats_job_position` (`id`),
  CONSTRAINT `ats_candidate_resume_create_by_id_c726cded_fk_ats_user_` FOREIGN KEY (`create_by_id`) REFERENCES `ats`.`ats_user` (`id`),
  CONSTRAINT `ats_candidate_resume_forward_to_user_id_fd42db8c_fk_ats_user_id` FOREIGN KEY (`to_user_id`) REFERENCES `ats`.`ats_user` (`id`),
  CONSTRAINT `ats_candidate_resume_from_user_id_ebd7785b_fk_ats_user_` FOREIGN KEY (`from_user_id`) REFERENCES `ats`.`ats_user` (`id`),
  CONSTRAINT `ats_candidate_resume_update_by_id_4fe5c81f_fk_ats_user_` FOREIGN KEY (`update_by_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_resume_duplication
-- Model: ats.web.candidate_resume.models.resume_duplication_model.CandidateResumeDuplication
CREATE TABLE IF NOT EXISTS `ats`.`ats_resume_duplication` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `check_time` datetime(6) NOT NULL,
  `operations` json NOT NULL,
  `duplicate_resumes` json NOT NULL,
  `candidate_resume_id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ats_resume_duplicati_candidate_resume_id_3367537a_fk_ats_candi` (`candidate_resume_id`),
  KEY `ats_resume_duplication_user_id_10574f98_fk_ats_user_id` (`user_id`),
  CONSTRAINT `ats_resume_duplicati_candidate_resume_id_3367537a_fk_ats_candi` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats`.`ats_candidate_resume` (`id`),
  CONSTRAINT `ats_resume_duplication_user_id_10574f98_fk_ats_user_id` FOREIGN KEY (`user_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_resume_duplication_task
-- Model: ats.web.candidate_resume.models.resume_duplication_task_model.ResumeDuplicationTask
CREATE TABLE IF NOT EXISTS `ats`.`ats_resume_duplication_task` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `resume_id` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_resume_comments
-- Model: ats.web.candidate_resume.models.resume_comments.ResumeCommentModel
CREATE TABLE IF NOT EXISTS `ats`.`ats_resume_comments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `share_to` json DEFAULT NULL,
  `comment` varchar(10240) NOT NULL,
  `comment_at` datetime(6) NOT NULL,
  `candidate_resume_id` bigint NOT NULL,
  `commentator_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ats_resume_comments_candidate_resume_id_c118434c_fk_ats_candi` (`candidate_resume_id`),
  KEY `ats_resume_comments_commentator_id_b5f6dd96_fk_ats_user_id` (`commentator_id`),
  CONSTRAINT `ats_resume_comments_candidate_resume_id_c118434c_fk_ats_candi` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats`.`ats_candidate_resume` (`id`),
  CONSTRAINT `ats_resume_comments_commentator_id_b5f6dd96_fk_ats_user_id` FOREIGN KEY (`commentator_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_resume_tags
-- Model: ats.web.candidate_resume.models.resume_tag_model.ResumeTagsModel
CREATE TABLE IF NOT EXISTS `ats`.`ats_resume_tags` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `match_type` int NOT NULL,
  `candidate_resume_id` bigint NOT NULL,
  `tag_id` bigint NOT NULL,
  `match_count` int NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_resume_tags_candidate_resume_id_tag_id_e48f4e71_uniq` (`candidate_resume_id`, `tag_id`),
  KEY `ats_resume_tags_tag_id_f65d778b_fk_ats_tag_id` (`tag_id`),
  CONSTRAINT `ats_resume_tags_candidate_resume_id_1c058891_fk_ats_candi` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats`.`ats_candidate_resume` (`id`),
  CONSTRAINT `ats_resume_tags_tag_id_f65d778b_fk_ats_tag_id` FOREIGN KEY (`tag_id`) REFERENCES `ats`.`ats_tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_resume_operation_records
-- Model: ats.web.candidate_resume.models.resume_records.ResumeOperationRecords
CREATE TABLE IF NOT EXISTS `ats`.`ats_resume_operation_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `candidate_resume_id` bigint NOT NULL,
  `operator_id` bigint NOT NULL,
  `operation_type` int NOT NULL,
  `operate_args` json DEFAULT NULL,
  `operate_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ats_resume_operation_records_candidate_resume_id_fk` (`candidate_resume_id`),
  KEY `ats_resume_operation_records_operator_id_fk` (`operator_id`),
  CONSTRAINT `ats_resume_operation_records_candidate_resume_id_fk` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats`.`ats_candidate_resume` (`id`),
  CONSTRAINT `ats_resume_operation_records_operator_id_fk` FOREIGN KEY (`operator_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_resume_view_record
-- Model: ats.web.candidate_resume.models.resume_view_record.ResumeViewRecord
CREATE TABLE IF NOT EXISTS `ats`.`ats_resume_view_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `candidate_resume_id` bigint NOT NULL,
  `view_by` bigint NOT NULL,
  `view_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_resume_view_record_uniq` (`candidate_resume_id`, `view_by`),
  KEY `ats_resume_view_record_view_by_fk` (`view_by`),
  CONSTRAINT `ats_resume_view_record_fk_candidate_resume` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats`.`ats_candidate_resume` (`id`),
  CONSTRAINT `ats_resume_view_record_fk_view_by` FOREIGN KEY (`view_by`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_talent_pool
-- Model: ats.web.talent_pool.models.talent_pool_model.TalentPool
CREATE TABLE IF NOT EXISTS `ats`.`ats_talent_pool` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `candidate_resume_id` bigint NOT NULL,
  `pooled_reason` int NOT NULL,
  `comments` varchar(500) DEFAULT NULL,
  `pooled_at` datetime(6) NOT NULL,
  `pooled_by_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `candidate_resume_id` (`candidate_resume_id`),
  KEY `ats_talent_pool_pooled_by_id_fk` (`pooled_by_id`),
  CONSTRAINT `ats_talent_pool_candidate_resume_id_fk` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats`.`ats_candidate_resume` (`id`),
  CONSTRAINT `ats_talent_pool_pooled_by_id_fk` FOREIGN KEY (`pooled_by_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 7. INTERVIEW PROCESS & EXECUTION
-- =====================================================================

-- Table: ats_interview_record
-- Model: ats.web.interview.models.interview_model.Interview
CREATE TABLE IF NOT EXISTS `ats`.`ats_interview_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `target_job_id` bigint NOT NULL,
  `created_by_id` bigint NOT NULL,
  `modified_by_id` bigint NOT NULL,
  `interview_type` int NOT NULL,
  `interview_template_id` bigint DEFAULT NULL,
  `interview_status` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `ats_interview_record_target_job_id_2d71e028_fk_ats_job_p` (`target_job_id`),
  KEY `ats_interview_record_created_by_id_9775f367_fk_ats_user_id` (`created_by_id`),
  KEY `ats_interview_record_modified_by_id_57cee4e6_fk_ats_user_id` (`modified_by_id`),
  KEY `ats_interview_record_interview_template_i_ea366d67_fk_ats_inter` (`interview_template_id`),
  CONSTRAINT `ats_interview_record_created_by_id_9775f367_fk_ats_user_id` FOREIGN KEY (`created_by_id`) REFERENCES `ats`.`ats_user` (`id`),
  CONSTRAINT `ats_interview_record_interview_template_i_ea366d67_fk_ats_inter` FOREIGN KEY (`interview_template_id`) REFERENCES `ats`.`ats_interview_process_template` (`id`),
  CONSTRAINT `ats_interview_record_modified_by_id_57cee4e6_fk_ats_user_id` FOREIGN KEY (`modified_by_id`) REFERENCES `ats`.`ats_user` (`id`),
  CONSTRAINT `ats_interview_record_target_job_id_2d71e028_fk_ats_job_p` FOREIGN KEY (`target_job_id`) REFERENCES `ats`.`ats_job_position` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_interview_rounds
-- Model: ats.web.interview.models.interview_round_model.InterviewRounds
CREATE TABLE IF NOT EXISTS `ats`.`ats_interview_rounds` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `interview_id` bigint NOT NULL,
  `round_code` varchar(32) NOT NULL,
  `order` int NOT NULL,
  `round_type` int NOT NULL,
  `interviewers` json DEFAULT NULL,
  `from_time` datetime(6) DEFAULT NULL,
  `to_time` datetime(6) DEFAULT NULL,
  `is_online` tinyint(1) DEFAULT NULL,
  `teams_meeting` json DEFAULT NULL,
  `interview_location` varchar(100) DEFAULT NULL,
  `info_interviewer` tinyint(1) DEFAULT NULL,
  `info_candidate` tinyint(1) DEFAULT NULL,
  `email_template_id` bigint DEFAULT NULL,
  `status` int NOT NULL DEFAULT 0,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `created_by_id` bigint NOT NULL,
  `modified_by_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ats_interview_rounds_interview_id_80c7f7e5` (`interview_id`),
  KEY `ats_interview_rounds_interview_id_order_43a30ad8_idx` (`interview_id`, `order`),
  KEY `ats_interview_rounds_email_template_id_7db8127e_fk_ats_email` (`email_template_id`),
  KEY `ats_interview_rounds_created_by_id_fk` (`created_by_id`),
  KEY `ats_interview_rounds_modified_by_id_fk` (`modified_by_id`),
  CONSTRAINT `ats_interview_rounds_interview_id_80c7f7e5_fk_ats_inter` FOREIGN KEY (`interview_id`) REFERENCES `ats`.`ats_interview_record` (`id`),
  CONSTRAINT `ats_interview_rounds_email_template_id_7db8127e_fk_ats_email` FOREIGN KEY (`email_template_id`) REFERENCES `ats`.`ats_email_template` (`id`),
  CONSTRAINT `ats_interview_round_create_by` FOREIGN KEY (`created_by_id`) REFERENCES `ats`.`ats_user` (`id`),
  CONSTRAINT `ats_interview_round_modify_by` FOREIGN KEY (`modified_by_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_interview_candidate
-- Model: ats.web.interview.models.interview_candidate_model.InterviewCandidate
CREATE TABLE IF NOT EXISTS `ats`.`ats_interview_candidate` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `status` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `candidate_resume_id` bigint NOT NULL,
  `round_id` bigint NOT NULL,
  `interview_email` json DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_interview_candidate_round_id_candidate_resum_bc57eec9_uniq` (`round_id`, `candidate_resume_id`),
  KEY `ats_interview_candid_candidate_resume_id_91cb3ddd_fk_ats_candi` (`candidate_resume_id`),
  CONSTRAINT `ats_interview_candid_candidate_resume_id_91cb3ddd_fk_ats_candi` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats`.`ats_candidate_resume` (`id`),
  CONSTRAINT `ats_interview_candid_round_id_e7edd739_fk_ats_inter` FOREIGN KEY (`round_id`) REFERENCES `ats`.`ats_interview_rounds` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_interview_feedback
-- Model: ats.web.interview.models.interview_feedback_model.InterviewFeedback
CREATE TABLE IF NOT EXISTS `ats`.`ats_interview_feedback` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `interview_result` int NOT NULL,
  `round_specific_feedback` json DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `created_by_id` bigint NOT NULL,
  `interview_candidate_id` bigint NOT NULL,
  `interview_round_id` bigint NOT NULL,
  `modified_by_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_interview_feedback_interview_candidate_id_8a85fe08_idx` (`interview_candidate_id`),
  KEY `ats_interview_feedback_created_by_id_750c67c7_fk_ats_user_id` (`created_by_id`),
  KEY `ats_interview_feedba_interview_round_id_1941319a_fk_ats_inter` (`interview_round_id`),
  KEY `ats_interview_feedback_modified_by_id_e1e9fa9c_fk_ats_user_id` (`modified_by_id`),
  CONSTRAINT `ats_interview_feedba_interview_candidate__8a85fe08_fk_ats_inter` FOREIGN KEY (`interview_candidate_id`) REFERENCES `ats`.`ats_interview_candidate` (`id`),
  CONSTRAINT `ats_interview_feedba_interview_round_id_1941319a_fk_ats_inter` FOREIGN KEY (`interview_round_id`) REFERENCES `ats`.`ats_interview_rounds` (`id`),
  CONSTRAINT `ats_interview_feedback_created_by_id_750c67c7_fk_ats_user_id` FOREIGN KEY (`created_by_id`) REFERENCES `ats`.`ats_user` (`id`),
  CONSTRAINT `ats_interview_feedback_modified_by_id_e1e9fa9c_fk_ats_user_id` FOREIGN KEY (`modified_by_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =====================================================================
-- 8. EXAMS & ASSESSMENTS
-- =====================================================================

-- Table: ats_exam
-- Model: ats.web.exam.models.exam_model.Exam
CREATE TABLE IF NOT EXISTS `ats`.`ats_exam` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `candidate_resume_id` bigint NOT NULL,
  `job_position_id` bigint DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `source` int NOT NULL,
  `paper_id` int NOT NULL,
  `paper_name` varchar(255) NOT NULL,
  `test_candidate_id` int NOT NULL,
  `test_url` varchar(4096) NOT NULL,
  `status` int NOT NULL,
  `data_status` int NOT NULL,
  `score` float DEFAULT NULL,
  `paper_score` float DEFAULT NULL,
  `cheat_status` int DEFAULT NULL,
  `test_time` datetime(6) DEFAULT NULL,
  `web_report_url` varchar(4096) DEFAULT NULL,
  `result_url` varchar(4096) DEFAULT NULL,
  `result_file` json DEFAULT NULL,
  `begin_time` datetime(6) DEFAULT NULL,
  `end_time` datetime(6) DEFAULT NULL,
  `hr_feedback` int DEFAULT NULL,
  `completed_at` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ats_exam_candidate_resume_id_66632686_fk_ats_candidate_resume_id` (`candidate_resume_id`),
  KEY `ats_exam_job_position_id_8aa3c446_fk_ats_job_position_id` (`job_position_id`),
  KEY `ats_exam_created_by_id_7ebed74c_fk_ats_user_id` (`created_by_id`),
  CONSTRAINT `ats_exam_candidate_resume_id_66632686_fk_ats_candidate_resume_id` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats`.`ats_candidate_resume` (`id`),
  CONSTRAINT `ats_exam_job_position_id_8aa3c446_fk_ats_job_position_id` FOREIGN KEY (`job_position_id`) REFERENCES `ats`.`ats_job_position` (`id`),
  CONSTRAINT `ats_exam_created_by_id_7ebed74c_fk_ats_user_id` FOREIGN KEY (`created_by_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table: ats_exam_forward
-- Model: ats.web.exam.models.exam_forward_model.ExamForward
CREATE TABLE IF NOT EXISTS `ats`.`ats_exam_forward` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `status` int NOT NULL,
  `feedback_notes` varchar(2048) DEFAULT NULL,
  `data_status` int NOT NULL,
  `candidate_resume_id` bigint NOT NULL,
  `exam_id` bigint NOT NULL,
  `from_user_id` bigint NOT NULL,
  `to_user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ats_exam_forward_candidate_resume_id_9c23a3ba_fk_ats_candi` (`candidate_resume_id`),
  KEY `ats_exam_forward_exam_id_4270197c_fk_ats_exam_id` (`exam_id`),
  KEY `ats_exam_forward_from_user_id_6b086a2e_fk_ats_user_id` (`from_user_id`),
  KEY `ats_exam_forward_to_user_id_ed9c93eb_fk_ats_user_id` (`to_user_id`),
  CONSTRAINT `ats_exam_forward_candidate_resume_id_9c23a3ba_fk_ats_candi` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats`.`ats_candidate_resume` (`id`),
  CONSTRAINT `ats_exam_forward_exam_id_4270197c_fk_ats_exam_id` FOREIGN KEY (`exam_id`) REFERENCES `ats`.`ats_exam` (`id`),
  CONSTRAINT `ats_exam_forward_from_user_id_6b086a2e_fk_ats_user_id` FOREIGN KEY (`from_user_id`) REFERENCES `ats`.`ats_user` (`id`),
  CONSTRAINT `ats_exam_forward_to_user_id_ed9c93eb_fk_ats_user_id` FOREIGN KEY (`to_user_id`) REFERENCES `ats`.`ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;
