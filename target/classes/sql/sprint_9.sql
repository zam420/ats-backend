CREATE TABLE `ats_resume_comments`
(
    `id`                  bigint      NOT NULL AUTO_INCREMENT,
    `share_to`            json DEFAULT NULL,
    `comment`             varchar(10240) NOT NULL,
    `comment_at`          datetime(6) NOT NULL,
    `candidate_resume_id` bigint      NOT NULL,
    `commentator_id`      bigint      NOT NULL,
    PRIMARY KEY (`id`),
    KEY                   `ats_resume_comments_candidate_resume_id_c118434c_fk_ats_candi` (`candidate_resume_id`),
    KEY                   `ats_resume_comments_commentator_id_b5f6dd96_fk_ats_user_id` (`commentator_id`),
    CONSTRAINT `ats_resume_comments_candidate_resume_id_c118434c_fk_ats_candi` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats_candidate_resume` (`id`),
    CONSTRAINT `ats_resume_comments_commentator_id_b5f6dd96_fk_ats_user_id` FOREIGN KEY (`commentator_id`) REFERENCES `ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `ats_email_counter`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT,
    `counter_tag` varchar(128) NOT NULL,
    `mail_count`  int          NOT NULL DEFAULT 0,
    `created_at`  datetime(6) NOT NULL,
    `updated_at`  datetime(6) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `counter_tag` (`counter_tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


## Exam
CREATE TABLE `ats_exam_forward`
(
    `id`                  bigint      NOT NULL AUTO_INCREMENT,
    `created_at`          datetime(6) NOT NULL,
    `status`              int         NOT NULL,
    `feedback_notes`      varchar(2048) DEFAULT NULL,
    `data_status`         int         NOT NULL,
    `candidate_resume_id` bigint      NOT NULL,
    `exam_id`             bigint      NOT NULL,
    `from_user_id`        bigint      NOT NULL,
    `to_user_id`          bigint      NOT NULL,
    PRIMARY KEY (`id`),
    KEY `ats_exam_forward_candidate_resume_id_9c23a3ba_fk_ats_candi` (`candidate_resume_id`),
    KEY `ats_exam_forward_exam_id_4270197c_fk_ats_exam_id` (`exam_id`),
    KEY `ats_exam_forward_from_user_id_6b086a2e_fk_ats_user_id` (`from_user_id`),
    KEY `ats_exam_forward_to_user_id_ed9c93eb_fk_ats_user_id` (`to_user_id`),
    CONSTRAINT `ats_exam_forward_candidate_resume_id_9c23a3ba_fk_ats_candi` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats_candidate_resume` (`id`),
    CONSTRAINT `ats_exam_forward_exam_id_4270197c_fk_ats_exam_id` FOREIGN KEY (`exam_id`) REFERENCES `ats_exam` (`id`),
    CONSTRAINT `ats_exam_forward_from_user_id_6b086a2e_fk_ats_user_id` FOREIGN KEY (`from_user_id`) REFERENCES `ats_user` (`id`),
    CONSTRAINT `ats_exam_forward_to_user_id_ed9c93eb_fk_ats_user_id` FOREIGN KEY (`to_user_id`) REFERENCES `ats_user` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

ALTER TABLE ats.`ats_exam` ADD COLUMN `hr_feedback` int DEFAULT NULL;
ALTER TABLE ats.`ats_exam` ADD COLUMN `completed_at` datetime(6) DEFAULT NULL;

INSERT INTO `ats_permission`(`code`, `name`) VALUES('umm','user management menu');

INSERT INTO `ats_permission`(`code`, `name`) VALUES('wts','written test search');
INSERT INTO `ats_permission`(`code`, `name`) VALUES('wtc','written test create');
INSERT INTO `ats_permission`(`code`, `name`) VALUES('wte','written test edit');
INSERT INTO `ats_permission`(`code`, `name`) VALUES('wtfc','written test forward create');
INSERT INTO `ats_permission`(`code`, `name`) VALUES('wtfe','written test forward edit');
INSERT INTO `ats_permission`(`code`, `name`) VALUES('wthrf','written test hr feedback');
INSERT INTO `ats_permission`(`code`, `name`) VALUES('wtrm','written test review menu');

INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='wts'),'wts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='wtc'),'wtc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='wte'),'wte');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='wtfs'),'wtfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='wtfc'),'wtfc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='wtfe'),'wtfe');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='wthrf'),'wthrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='wts'),'wts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='wtc'),'wtc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='wte'),'wte');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='wtfs'),'wtfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='wtfc'),'wtfc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='wtfe'),'wtfe');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='wthrf'),'wthrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='wts'),'wts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='wtc'),'wtc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='wte'),'wte');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='wtfs'),'wtfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='wtfc'),'wtfc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='wtfe'),'wtfe');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='wthrf'),'wthrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='wts'),'wts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='wtc'),'wtc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='wte'),'wte');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='wtfs'),'wtfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='wtfc'),'wtfc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='wtfe'),'wtfe');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='wthrf'),'wthrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,(SELECT `id` FROM `ats_permission` WHERE `code`='wts'),'wts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,(SELECT `id` FROM `ats_permission` WHERE `code`='wtfs'),'wtfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,(SELECT `id` FROM `ats_permission` WHERE `code`='wts'),'wts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,(SELECT `id` FROM `ats_permission` WHERE `code`='wtfs'),'wtfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,(SELECT `id` FROM `ats_permission` WHERE `code`='wtfe'),'wtfe');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,(SELECT `id` FROM `ats_permission` WHERE `code`='wtrm'),'wtrm');


update ats_exam set data_status=11 where data_status=0;
update ats_exam set data_status=12 where data_status=1;
update ats_exam set data_status=1 where data_status=11;
update ats_exam set data_status=2 where data_status=12;

INSERT INTO `ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('mail_counter_init', '1', NULL);
-- >>>>>>>>>>>>>>>>>>>>>>>>>>>>tag feature
CREATE TABLE `ats_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `created_by_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `ats_tag_created_by_id_7cea5ef4_fk_ats_user_id` (`created_by_id`),
  CONSTRAINT `ats_tag_created_by_id_7cea5ef4_fk_ats_user_id` FOREIGN KEY (`created_by_id`) REFERENCES `ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
-- 解析简历tags 字段将历史数据解析到tag表
INSERT INTO ats_tag(`name`,`created_by_id`,`created_at`,`updated_at`)
SELECT
    DISTINCT(JSON_UNQUOTE(JSON_EXTRACT(tags,CONCAT('$[', help_topic.help_topic_id, ']')))) AS `name`,t.id as `created_by_id`,NOW() as `created_at`,NOW() as `updated_at`
FROM
    ats_candidate_resume
    JOIN mysql.help_topic ON help_topic.help_topic_id < JSON_LENGTH(tags)
		JOIN (select id from ats_user where type=2 limit 1)t
WHERE
    JSON_VALID(tags) AND JSON_LENGTH(tags);
-- 创建ats_resume_tags关联表
CREATE TABLE `ats_resume_tags` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `match_type` int NOT NULL,
  `candidate_resume_id` bigint NOT NULL,
  `tag_id` bigint NOT NULL,
  `match_count` int NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_resume_tags_candidate_resume_id_tag_id_e48f4e71_uniq` (`candidate_resume_id`,`tag_id`),
  KEY `ats_resume_tags_tag_id_f65d778b_fk_ats_tag_id` (`tag_id`),
  CONSTRAINT `ats_resume_tags_candidate_resume_id_1c058891_fk_ats_candi` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats_candidate_resume` (`id`),
  CONSTRAINT `ats_resume_tags_tag_id_f65d778b_fk_ats_tag_id` FOREIGN KEY (`tag_id`) REFERENCES `ats_tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
-- 解析简历的tags字段，将历史数据解析到ats_resume_tags关联表中
INSERT INTO ats_resume_tags(match_type,candidate_resume_id,tag_id,match_count)
SELECT 0,t.resume_id,ats_tag.id,1 FROM
    (SELECT ats_candidate_resume.id AS resume_id,JSON_UNQUOTE(JSON_EXTRACT(tags,CONCAT('$[', help_topic.help_topic_id, ']'))) AS tag_name
    FROM ats_candidate_resume JOIN mysql.help_topic ON help_topic.help_topic_id < JSON_LENGTH(tags)
    WHERE JSON_VALID(tags) AND JSON_LENGTH(tags)) t
    INNER JOIN ats_tag ON t.tag_name = ats_tag.`name`;

INSERT INTO `ats_task_record` ( `task_id`, `record`, `sub_id`) VALUES ('auto_tag_resumes_for_fresh_resumes', NOW(), NULL);
INSERT INTO `ats_task_record` ( `task_id`, `record`, `sub_id`) VALUES ('auto_tag_resumes_for_fresh_tags', NOW(), NULL);

INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('通信系统', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('雷达', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('激光', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('信号传输', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('FPGA', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('Wi-Fi', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('4G', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('5G', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('Zgbee', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('基带Base Band', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('信道', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('赛灵思 （Xilinx）', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('MIMO系统', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('导航', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('网络', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('交换机协议', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('低功耗', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('蓝牙', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('AI 加速', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('Al计算', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('异步 FIFO', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('ASIC 设计', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('SOC 设计', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('AHB 总线', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('神经网络', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('CPU', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('Risc-V', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('System Veriog', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('UVM', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('外设', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('接口', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('FreeRTOS', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('UART', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('SPI', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('I2C', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('深度学习', NOW(), NOW(), (select id from ats_user where type=2 limit 1));
INSERT INTO `ats`.`ats_tag` (`name`, `created_at`, `updated_at`, `created_by_id`) VALUES ('语言识别', NOW(), NOW(), (select id from ats_user where type=2 limit 1));

-- tag management permission
INSERT INTO `ats_permission`(`code`, `name`) VALUES('tmm','tag management menu');
INSERT INTO `ats_permission`(`code`, `name`) VALUES('ats','all tag search');
INSERT INTO `ats_permission`(`code`, `name`) VALUES('te','tag edit');
INSERT INTO `ats_permission`(`code`, `name`) VALUES('td','tag delete');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='tmm'),'tmm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='ats'),'ats');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='te'),'te');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='td'),'td');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='tmm'),'tmm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='ats'),'ats');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='te'),'te');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='td'),'td');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='ats'),'ats');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='ats'),'ats');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,(SELECT `id` FROM `ats_permission` WHERE `code`='ats'),'ats');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,(SELECT `id` FROM `ats_permission` WHERE `code`='ats'),'ats');
