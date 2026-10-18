-- make my interview menu accessible for hrd , project admin, hrr and hrrc
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,(SELECT `id` FROM `ats_permission` WHERE `code`='mim'),'mim');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,(SELECT `id` FROM `ats_permission` WHERE `code`='mim'),'mim');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,(SELECT `id` FROM `ats_permission` WHERE `code`='mim'),'mim');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,(SELECT `id` FROM `ats_permission` WHERE `code`='mim'),'mim');

INSERT INTO ats_resume_operation_records(candidate_resume_id,operator_id,operation_type,operate_args,operate_at)
SELECT
    acr.id AS candidate_resume_id,
    CASE
		WHEN acr.create_by_id IS NULL
		THEN au.id
		ELSE acr.create_by_id
		END AS operator_id,
    10 AS operation_type,
    CASE
		WHEN acr.create_by_id IS NULL
		THEN JSON_OBJECT('is_applied_by_candidate', true,'applied_position', ajp.title,'candidate_name', acr.name)
    ELSE JSON_OBJECT('is_applied_by_candidate', false,'applied_position', ajp.title,'candidate_name', acr.name)
		END AS operate_args,
    applied_at AS operate_at
FROM ats_candidate_resume acr
INNER JOIN ats_job_position ajp ON acr.applying_position_id = ajp.id
LEFT JOIN ats_user au ON au.email = 'ats@espressif.com';

CREATE TABLE `ats_api_token`
(
    `id`     bigint       NOT NULL AUTO_INCREMENT,
    `token`  varchar(512) NOT NULL,
    `apis`   json         NOT NULL,
    `status` int          NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

