alter table ats_talent_pool modify pooled_at datetime default '2023-09-11 00:00:00' not null;
alter table ats_talent_pool add pooled_by_id int default 6 not null;
create unique index candidate_resume_id on ats.ats_talent_pool(candidate_resume_id);

update ats_candidate_resume
set status= -1
where status != -1
  and id in (select candidate_resume_id from ats_talent_pool);

alter table ats.ats_candidate_resume add management_status int default 0 not null;
alter table ats.ats_candidate_resume add management_sub_status int default null;


# HRR
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='tpc'),'tpc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='tpe'),'tpe');
# HRC
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='tpc'),'tpc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='tpe'),'tpe');

INSERT INTO `ats_permission`(`code`, `name`) VALUES('mim','my interview menu');
INSERT INTO `ats_permission`(`code`, `name`) VALUES('im','interviewer menu');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,(SELECT `id` FROM `ats_permission` WHERE `code`='im'),'im');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,(SELECT `id` FROM `ats_permission` WHERE `code`='mim'),'mim');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,(SELECT `id` FROM `ats_permission` WHERE `code`='im'),'im');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,(SELECT `id` FROM `ats_permission` WHERE `code`='mim'),'mim');

INSERT INTO `ats_permission`(`code`, `name`) VALUES('rdft','resume detail feedback tab');
INSERT INTO `ats_permission`(`code`, `name`) VALUES('rdht','resume detail history tab');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='rdft'),'rdft');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='rdht'),'rdht');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='rdft'),'rdft');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='rdht'),'rdht');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='rdft'),'rdft');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='rdht'),'rdht');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='rdft'),'rdft');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='rdht'),'rdht');

INSERT INTO `ats_permission`(`code`, `name`) VALUES('is','interview search');
INSERT INTO `ats_permission`(`code`, `name`) VALUES('ifc','interview feedback create');
INSERT INTO `ats_permission`(`code`, `name`) VALUES('ife','interview feedback edit');
INSERT INTO `ats_permission`(`code`, `name`) VALUES('ifs','interview feedback search');


INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='is'),'is');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='is'),'is');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='is'),'is');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='is'),'is');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,(SELECT `id` FROM `ats_permission` WHERE `code`='is'),'is');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,(SELECT `id` FROM `ats_permission` WHERE `code`='is'),'is');

INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='ifc'),'ifc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='ifc'),'ifc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='ifc'),'ifc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='ifc'),'ifc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,(SELECT `id` FROM `ats_permission` WHERE `code`='ifc'),'ifc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,(SELECT `id` FROM `ats_permission` WHERE `code`='ifc'),'ifc');


INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='ifs'),'ifs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='ifs'),'ifs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='ifs'),'ifs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='ifs'),'ifs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,(SELECT `id` FROM `ats_permission` WHERE `code`='ifs'),'ifs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,(SELECT `id` FROM `ats_permission` WHERE `code`='ifs'),'ifs');



CREATE TABLE `ats_interview_feedback` (
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
  UNIQUE KEY `ats_interview_feedback_interview_candidate_id_8a85fe08_idx` (`interview_candidate_id`) USING BTREE,
  KEY `ats_interview_feedback_created_by_id_750c67c7_fk_ats_user_id` (`created_by_id`),
  KEY `ats_interview_feedba_interview_round_id_1941319a_fk_ats_inter` (`interview_round_id`),
  KEY `ats_interview_feedback_modified_by_id_e1e9fa9c_fk_ats_user_id` (`modified_by_id`),
  CONSTRAINT `ats_interview_feedba_interview_candidate__8a85fe08_fk_ats_inter` FOREIGN KEY (`interview_candidate_id`) REFERENCES `ats_interview_candidate` (`id`),
  CONSTRAINT `ats_interview_feedba_interview_round_id_1941319a_fk_ats_inter` FOREIGN KEY (`interview_round_id`) REFERENCES `ats_interview_rounds` (`id`),
  CONSTRAINT `ats_interview_feedback_created_by_id_750c67c7_fk_ats_user_id` FOREIGN KEY (`created_by_id`) REFERENCES `ats_user` (`id`),
  CONSTRAINT `ats_interview_feedback_modified_by_id_e1e9fa9c_fk_ats_user_id` FOREIGN KEY (`modified_by_id`) REFERENCES `ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


INSERT INTO `ats_permission`(`code`, `name`) VALUES ('cres','candidate resume email search');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='cres'),'cres');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='cres'),'cres');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='cres'),'cres');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='cres'),'cres');


ALTER TABLE `ats`.`ats_interview_rounds`
ADD COLUMN `meeting_id` json DEFAULT NULL AFTER `is_online`,
ADD COLUMN `created_by_id` bigint NOT NULL AFTER `status`,
ADD COLUMN `modified_by_id` bigint NOT NULL AFTER `created_by_id`,
ADD CONSTRAINT `ats_interview_round_create_by` FOREIGN KEY (`created_by_id`) REFERENCES `ats`.`ats_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
ADD CONSTRAINT `ats_interview_round_modify_by` FOREIGN KEY (`modified_by_id`) REFERENCES `ats`.`ats_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;


CREATE TABLE `ats_candidate_resume_offer`
(
    `id`                  bigint      NOT NULL AUTO_INCREMENT,
    `feedback_status`     int         NOT NULL,
    `created_at`          datetime(6) NOT NULL,
    `updated_at`          datetime(6) NOT NULL,
    `candidate_resume_id` bigint      NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `candidate_resume_id` (`candidate_resume_id`),
    CONSTRAINT `ats_candidate_resume_candidate_resume_id_63e01b4d_fk_ats_candi` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats_candidate_resume` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


CREATE TABLE `ats_email_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `to` json NOT NULL,
  `cc` json DEFAULT NULL,
  `bcc` json DEFAULT NULL,
  `subject` varchar(1024) DEFAULT NULL,
  `body` longtext NOT NULL,
  `trigger_models` json NOT NULL,
  `status` int NOT NULL,
  `remark` varchar(225) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `from_user_id` bigint DEFAULT NULL,
  `is_html_mail` tinyint(1) NOT NULL,
  `mail_type` int NOT NULL,
  `scheduled_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ats_email_records_from_user_id_048bb526_fk_ats_user_id` (`from_user_id`),
  KEY `ats_email_records_status_scheduled_time_fb3c5ebc_idx` (`status`,`scheduled_time`),
  KEY `ats_email_records_mail_type_32346fca` (`mail_type`),
  CONSTRAINT `ats_email_records_from_user_id_048bb526_fk_ats_user_id` FOREIGN KEY (`from_user_id`) REFERENCES `ats_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=255 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `ats_email_records_attachments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `atsemailrecords_id` bigint NOT NULL,
  `appfile_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_email_records_attach_atsemailrecords_id_appfi_199b65c2_uniq` (`atsemailrecords_id`,`appfile_id`),
  KEY `ats_email_records_at_appfile_id_ffb2e2d4_fk_ats_app_f` (`appfile_id`),
  CONSTRAINT `ats_email_records_at_appfile_id_ffb2e2d4_fk_ats_app_f` FOREIGN KEY (`appfile_id`) REFERENCES `ats_app_file` (`id`),
  CONSTRAINT `ats_email_records_at_atsemailrecords_id_a9bdf0f0_fk_ats_email` FOREIGN KEY (`atsemailrecords_id`) REFERENCES `ats_email_records` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


ALTER TABLE `ats_candidate_resume` ADD COLUMN `sub_status` int DEFAULT NULL;
ALTER TABLE `ats_interview_record` ADD COLUMN `interview_status` int NOT NULL DEFAULT 0;
ALTER TABLE `ats_interview_rounds` CHANGE COLUMN `data_status` `status` int NOT NULL DEFAULT 0;

INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`) VALUES ('interview_meeting_rsp_handler_task', '2024-02-29 00:00:00');
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`) VALUES ('interview_time_task','1999-01-01 00:00:00');

