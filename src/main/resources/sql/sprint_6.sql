# settings
INSERT INTO `ats_permission`(`code`, `name`) VALUES ('stm', 'settings menu');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='stm'),'stm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='stm'),'stm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='stm'),'stm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='stm'),'stm');

# email template
CREATE TABLE `ats_email_template`
(
    `id`                  bigint        NOT NULL AUTO_INCREMENT,
    `name`                varchar(255)  NOT NULL,
    `subject`             varchar(255)  NOT NULL,
    `content`             varchar(5000) NOT NULL,
    `recruitment_type`    int           NOT NULL,
    `work_location`       int           NOT NULL,
    `notes`               varchar(2000) DEFAULT NULL,
    `create_time`         datetime(6)   NOT NULL,
    `last_modified_time`  datetime(6)   DEFAULT NULL,
    `status`              int           NOT NULL,
    `fields`              json          DEFAULT NULL,
    `create_by_id`        bigint        NOT NULL,
    `last_modified_by_id` bigint        DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `ats_email_template_create_by_id_7d1f6eb5_fk_ats_user_id` (`create_by_id`),
    KEY `ats_email_template_last_modified_by_id_f9f33197_fk_ats_user_id` (`last_modified_by_id`),
    CONSTRAINT `ats_email_template_create_by_id_7d1f6eb5_fk_ats_user_id` FOREIGN KEY (`create_by_id`) REFERENCES `ats_user` (`id`),
    CONSTRAINT `ats_email_template_last_modified_by_id_f9f33197_fk_ats_user_id` FOREIGN KEY (`last_modified_by_id`) REFERENCES `ats_user` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `ats_email_template_sharing`
(
    `id`            bigint      NOT NULL AUTO_INCREMENT,
    `create_time`   datetime(6) NOT NULL,
    `update_time`   datetime(6) NOT NULL,
    `status`        int         NOT NULL,
    `share_from_id` bigint      NOT NULL,
    `share_to_id`   bigint      NOT NULL,
    `template_id`   bigint      NOT NULL,
    PRIMARY KEY (`id`),
    KEY `ats_email_template_sharing_share_from_id_d592bba0_fk_ats_user_id` (`share_from_id`),
    KEY `ats_email_template_sharing_share_to_id_30d22190_fk_ats_user_id` (`share_to_id`),
    KEY `ats_email_template_s_template_id_ad53468a_fk_ats_email` (`template_id`),
    CONSTRAINT `ats_email_template_s_template_id_ad53468a_fk_ats_email` FOREIGN KEY (`template_id`) REFERENCES `ats_email_template` (`id`),
    CONSTRAINT `ats_email_template_sharing_share_from_id_d592bba0_fk_ats_user_id` FOREIGN KEY (`share_from_id`) REFERENCES `ats_user` (`id`),
    CONSTRAINT `ats_email_template_sharing_share_to_id_30d22190_fk_ats_user_id` FOREIGN KEY (`share_to_id`) REFERENCES `ats_user` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

INSERT INTO `ats_permission`(`code`, `name`) VALUES ('etm', 'email template menu');
INSERT INTO `ats_permission`(`code`, `name`) VALUES ('etc', 'email template create');
INSERT INTO `ats_permission`(`code`, `name`) VALUES ('ete', 'email template edit');
INSERT INTO `ats_permission`(`code`, `name`) VALUES ('ets', 'email template search');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='etm'),'etm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='etc'),'etc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='ete'),'ete');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='ets'),'ets');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='etm'),'etm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='etc'),'etc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='ete'),'ete');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='ets'),'ets');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='etm'),'etm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='ets'),'ets');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='etm'),'etm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='ets'),'ets');



CREATE TABLE `ats_interview_process_template` (
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
 CONSTRAINT `ats_interviewproces_created_by_id_3be35bf7_fk_ats_user_` FOREIGN KEY (`created_by_id`) REFERENCES `ats_user` (`id`),
 CONSTRAINT `ats_interview_proces_modified_by_id_948261da_fk_ats_user_` FOREIGN KEY (`modified_by_id`) REFERENCES `ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `ats_interview_process_template_share_to` (
 `id` bigint NOT NULL AUTO_INCREMENT,
 `interviewprocesstemplate_id` bigint NOT NULL,
 `user_id` bigint NOT NULL,
 PRIMARY KEY (`id`),
 UNIQUE KEY `ats_interview_process_te_interviewprocesstemplate_391e1c71_uniq` (`interviewprocesstemplate_id`,`user_id`),
 KEY `ats_interview_proces_user_id_d86e0cd8_fk_ats_user_` (`user_id`),
 CONSTRAINT `ats_interview_proces_interviewprocesstemp_06adabff_fk_ats_inter` FOREIGN KEY (`interviewprocesstemplate_id`) REFERENCES `ats_interview_process_template` (`id`),
 CONSTRAINT `ats_interview_proces_user_id_d86e0cd8_fk_ats_user_` FOREIGN KEY (`user_id`) REFERENCES `ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `ats_interview_round_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `round_type` int NOT NULL,
  `interviewers` json DEFAULT NULL,
  `order` int NOT NULL,
  `ipt_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_interview_round_template_ipt_id_order_82b26685_uniq` (`ipt_id`,`order`),
  CONSTRAINT `ats_interview_round__ipt_id_ce3dccaa_fk_ats_inter` FOREIGN KEY (`ipt_id`) REFERENCES `ats_interview_process_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `ats_permission`(`code`, `name`) VALUES('iptc','interview process template create');
INSERT INTO `ats_permission`(`code`, `name`) VALUES('ipte','interview process template edit');
INSERT INTO `ats_permission`(`code`, `name`) VALUES('ipts','interview process template search');
INSERT INTO `ats_permission`(`code`, `name`) VALUES('iptm','interview process template menu');

INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='iptc'),'iptc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='ipte'),'ipte');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='ipts'),'ipts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='iptc'),'iptc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='ipte'),'ipte');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='ipts'),'ipts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='ipts'),'ipts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='ipts'),'ipts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='iptm'),'iptm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='iptm'),'iptm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='iptm'),'iptm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='iptm'),'iptm');


INSERT INTO `ats_permission`(`code`, `name`) VALUES('aus','all user search');

INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='aus'),'aus');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='aus'),'aus');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='aus'),'aus');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='aus'),'aus');

INSERT INTO `ats_permission`(`code`, `name`) VALUES ('fcr','forward candidate resume');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='fcr'),'fcr');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='fcr'),'fcr');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='fcr'),'fcr');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='fcr'),'fcr');
INSERT INTO `ats_permission`(`code`, `name`) VALUES ('fcrf','feedback candidate resume forward');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='fcrf'),'fcrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='fcrf'),'fcrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='fcrf'),'fcrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='fcrf'),'fcrf');

INSERT INTO `ats_permission`(`code`, `name`) VALUES ('crfs','candidate resume forward search');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='crfs'),'crfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='crfs'),'crfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='crfs'),'crfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='crfs'),'crfs');

# recruitment project
ALTER TABLE `ats_recruitment_project` ADD COLUMN `work_location` int NOT NULL;
UPDATE `ats_recruitment_project` SET `work_location`=1 WHERE id>0;

CREATE TABLE `ats_recruitment_project_ipt`(
    `id`                          bigint NOT NULL AUTO_INCREMENT,
    `recruitmentproject_id`       bigint NOT NULL,
    `interviewprocesstemplate_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ats_recruitment_project__recruitmentproject_id_in_21ffae96_uniq` (`recruitmentproject_id`, `interviewprocesstemplate_id`),
    KEY `ats_recruitment_proj_interviewprocesstemp_6fbe9f90_fk_ats_inter` (`interviewprocesstemplate_id`),
    CONSTRAINT `ats_recruitment_proj_interviewprocesstemp_6fbe9f90_fk_ats_inter` FOREIGN KEY (`interviewprocesstemplate_id`) REFERENCES `ats_interview_process_template` (`id`),
    CONSTRAINT `ats_recruitment_proj_recruitmentproject_i_7331cb38_fk_ats_recru` FOREIGN KEY (`recruitmentproject_id`) REFERENCES `ats_recruitment_project` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `ats_interview_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `target_job_id` bigint NOT NULL,
  `created_by_id` bigint NOT NULL,
  `modified_by_id` bigint NOT NULL,
  `interview_type` int NOT NULL,
  `interview_template_id` bigint NULL,
  PRIMARY KEY (`id`),
  KEY `ats_interview_record_target_job_id_2d71e028_fk_ats_job_p` (`target_job_id`),
  KEY `ats_interview_record_created_by_id_9775f367_fk_ats_user_id` (`created_by_id`),
  KEY `ats_interview_record_modified_by_id_57cee4e6_fk_ats_user_id` (`modified_by_id`),
  KEY `ats_interview_record_interview_template_i_ea366d67_fk_ats_inter` (`interview_template_id`),
  CONSTRAINT `ats_interview_record_created_by_id_9775f367_fk_ats_user_id` FOREIGN KEY (`created_by_id`) REFERENCES `ats_user` (`id`),
  CONSTRAINT `ats_interview_record_interview_template_i_ea366d67_fk_ats_inter` FOREIGN KEY (`interview_template_id`) REFERENCES `ats_interview_process_template` (`id`),
  CONSTRAINT `ats_interview_record_modified_by_id_57cee4e6_fk_ats_user_id` FOREIGN KEY (`modified_by_id`) REFERENCES `ats_user` (`id`),
  CONSTRAINT `ats_interview_record_target_job_id_2d71e028_fk_ats_job_p` FOREIGN KEY (`target_job_id`) REFERENCES `ats_job_position` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `ats_interview_rounds` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `round_type` int NOT NULL,
  `interviewers` json DEFAULT NULL,
  `order` int NOT NULL,
  `from_time` datetime(6) DEFAULT NULL,
  `to_time` datetime(6) DEFAULT NULL,
  `is_online` tinyint(1) DEFAULT NULL,
  `interview_location` varchar(100) DEFAULT NULL,
  `info_interviewer` tinyint(1) DEFAULT NULL,
  `info_candidate` tinyint(1) DEFAULT NULL,
  `data_status` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `interview_id` bigint NOT NULL,
  `email_template_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ats_interview_rounds_interview_id_80c7f7e5` (`interview_id`),
  KEY `ats_interview_rounds_interview_id_order_43a30ad8_idx` (`interview_id`,`order`),
  KEY `ats_interview_rounds_email_template_id_7db8127e_fk_ats_email` (`email_template_id`),
  CONSTRAINT `ats_interview_rounds_email_template_id_7db8127e_fk_ats_email` FOREIGN KEY (`email_template_id`) REFERENCES `ats_email_template` (`id`),
  CONSTRAINT `ats_interview_rounds_interview_id_80c7f7e5_fk_ats_inter` FOREIGN KEY (`interview_id`) REFERENCES `ats_interview_record` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `ats_interview_candidate` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `status` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `candidate_resume_id` bigint NOT NULL,
  `round_id` bigint NOT NULL,
  `interview_email` json DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_interview_candidate_round_id_candidate_resum_bc57eec9_uniq` (`round_id`,`candidate_resume_id`),
  KEY `ats_interview_candid_candidate_resume_id_91cb3ddd_fk_ats_candi` (`candidate_resume_id`),
  CONSTRAINT `ats_interview_candid_candidate_resume_id_91cb3ddd_fk_ats_candi` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats_candidate_resume` (`id`),
  CONSTRAINT `ats_interview_candid_round_id_e7edd739_fk_ats_inter` FOREIGN KEY (`round_id`) REFERENCES `ats_interview_rounds` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `ats_candidate_resume_forward` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `status` int NOT NULL,
  `forward_notes` longtext,
  `feedback_notes` longtext,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `candidate_resume_id` bigint NOT NULL,
  `create_by_id` bigint NOT NULL,
  `from_user_id` bigint NOT NULL,
  `to_user_id` bigint NOT NULL,
  `update_by_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ats_candidate_resume_candidate_resume_id_50774a19_fk_ats_candi` (`candidate_resume_id`),
  KEY `ats_candidate_resume_create_by_id_c726cded_fk_ats_user_` (`create_by_id`),
  KEY `ats_candidate_resume_from_user_id_ebd7785b_fk_ats_user_` (`from_user_id`),
  KEY `ats_candidate_resume_forward_to_user_id_fd42db8c_fk_ats_user_id` (`to_user_id`),
  KEY `ats_candidate_resume_update_by_id_4fe5c81f_fk_ats_user_` (`update_by_id`),
  CONSTRAINT `ats_candidate_resume_candidate_resume_id_50774a19_fk_ats_candi` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats_candidate_resume` (`id`),
  CONSTRAINT `ats_candidate_resume_create_by_id_c726cded_fk_ats_user_` FOREIGN KEY (`create_by_id`) REFERENCES `ats_user` (`id`),
  CONSTRAINT `ats_candidate_resume_forward_to_user_id_fd42db8c_fk_ats_user_id` FOREIGN KEY (`to_user_id`) REFERENCES `ats_user` (`id`),
  CONSTRAINT `ats_candidate_resume_from_user_id_ebd7785b_fk_ats_user_` FOREIGN KEY (`from_user_id`) REFERENCES `ats_user` (`id`),
  CONSTRAINT `ats_candidate_resume_update_by_id_4fe5c81f_fk_ats_user_` FOREIGN KEY (`update_by_id`) REFERENCES `ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `ats_email_task` (
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

CREATE TABLE `ats_email_task_attachments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `emailtask_id` bigint NOT NULL,
  `appfile_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_email_task_attachments_emailtask_id_appfile_id_cd02b837_uniq` (`emailtask_id`,`appfile_id`),
  KEY `ats_email_task_attac_appfile_id_16ef0eb9_fk_ats_app_f` (`appfile_id`),
  CONSTRAINT `ats_email_task_attac_appfile_id_16ef0eb9_fk_ats_app_f` FOREIGN KEY (`appfile_id`) REFERENCES `ats_app_file` (`id`),
  CONSTRAINT `ats_email_task_attac_emailtask_id_56b330ea_fk_ats_email` FOREIGN KEY (`emailtask_id`) REFERENCES `ats_email_task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`) VALUES ('email_task_schedule', '1999-01-01 00:00:00');
ALTER TABLE `ats`.`ats_candidate` DROP COLUMN `status`;
ALTER TABLE `ats`.`ats_history` ADD COLUMN `is_subject` tinyint(1) NOT NULL DEFAULT 1;
ALTER TABLE `ats`.`ats_history` DROP INDEX `ats_history_model_id_issubject`,
ADD UNIQUE INDEX `ats_history_model_id_subject`(`target_model`, `target_id`, `is_subject`) USING BTREE;
update `ats_candidate_resume` set status = -1 where status =1;
INSERT INTO `ats_permission`(`code`, `name`) VALUES('ic','interview create');

INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='ic'),'ic');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='ic'),'ic');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='ic'),'ic');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='ic'),'ic');

INSERT INTO `ats_permission`(`code`, `name`) VALUES ('rsm','resume screening menu');
-- Hiring Managers
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,(SELECT `id` FROM `ats_permission` WHERE `code`='rsm'),'rsm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,(SELECT `id` FROM `ats_permission` WHERE `code`='crs'),'crs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,(SELECT `id` FROM `ats_permission` WHERE `code`='fcrf'),'fcrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,(SELECT `id` FROM `ats_permission` WHERE `code`='crfs'),'crfs');

-- Interviewers
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,(SELECT `id` FROM `ats_permission` WHERE `code`='rsm'),'rsm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,(SELECT `id` FROM `ats_permission` WHERE `code`='crs'),'crs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,(SELECT `id` FROM `ats_permission` WHERE `code`='fcrf'),'fcrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,(SELECT `id` FROM `ats_permission` WHERE `code`='crfs'),'crfs');

INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='cre'),'cre');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='cre'),'cre');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='cre'),'cre');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='cre'),'cre');

ALTER TABLE `ats`.`ats_candidate_work_experience` MODIFY COLUMN `major_achievement` text NULL;

CREATE TABLE `ats_job_position_interview_process_template`
(
    `id`                          bigint NOT NULL AUTO_INCREMENT,
    `jobposition_id`              bigint NOT NULL,
    `interviewprocesstemplate_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ats_job_position_intervi_jobposition_id_interview_f80ba055_uniq` (`jobposition_id`, `interviewprocesstemplate_id`),
    KEY `ats_job_position_int_interviewprocesstemp_68821354_fk_ats_inter` (`interviewprocesstemplate_id`),
    CONSTRAINT `ats_job_position_int_interviewprocesstemp_68821354_fk_ats_inter` FOREIGN KEY (`interviewprocesstemplate_id`) REFERENCES `ats_interview_process_template` (`id`),
    CONSTRAINT `ats_job_position_int_jobposition_id_6127bfdb_fk_ats_job_p` FOREIGN KEY (`jobposition_id`) REFERENCES `ats_job_position` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
