-- roles: see ats.common.constant.enums.AtsSystemRole
INSERT INTO `ats_role` (`id`, `public_id`, `code`, `name`) VALUES (1, 'V1StPuN8_Z5k', 'sa', 'System Admin');
INSERT INTO `ats_role` (`id`, `public_id`, `code`, `name`) VALUES(2, 'V3mGXR8_Z5l', 'hra', 'HR Admin');
INSERT INTO `ats_role` (`id`, `public_id`, `code`, `name`) VALUES(3, 'V9YtGXR8_Z5n', 'rec', 'Recruiter');
INSERT INTO `ats_role` (`id`, `public_id`, `code`, `name`) VALUES(4, 'V8pRGBH8_Z5w', 'hm', 'Hiring Manager');
INSERT INTO `ats_role` (`id`, `public_id`, `code`, `name`) VALUES(5, 'V1StGFN8_Z5b', 'itv', 'Interviewer');
INSERT INTO `ats_role` (`id`, `public_id`, `code`, `name`) VALUES(6, 'V1StPos8_Z5r', 'cdt', 'Candidate');

-- permissions

-- recruitment
-- recruitment project
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(1,'V1StGXR8_Z5s','rpm','D menu');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(2,'V3mGXR8_Z5l','rpe','recruitment project edit'); -- permission to recruitment project`s create,update,delete
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(3,'V1QUGRR8_Z5a','rps','recruitment project search');
-- job position
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(4,'V1StPos8_Z5r','jpm','job position menu');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(5,'V9StGFN8_Z5b','jpc','job position create'); -- permission to job position`s create
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(6,'V8pRGBH8_Anw','jpe','job position edit'); -- permission to job position`s update,delete
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(7,'V9YtGXR8_Z5n','jps','job position search');
-- talent pool
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(8,'V1SpnTR8_Zbz','tpm','talent pool menu');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES (9,'V3lOyXR8_Z60','tpc','talent pool create');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES (10,'V0OPqXR8_Z61','tpe','talent pool edit');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES (11,'V5StGXR8_Z62','tps','talent pool search');
-- candidate resume
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(12,'V9pQGXR8_Z63','crm','candidate resume menu');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES (13,'VL3nGXR8_Z64','crc','candidate resume create');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES (14,'V1StGXR8_Z65','cre','candidate resume edit');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES (15,'V1StGXR8_Z66','crd','candidate resume delete');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES (16,'V1StGXR8_Z67','crs','candidate resume search');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES (17,'V1StGXR8_Z68','fcr','forward candidate resume');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES (18,'V1StGXR8_Z69','fcrf','feedback candidate resume forward');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES (19,'V1StGXR8_Z6a','crfs','candidate resume forward search');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES (20,'V1StGXR8_Z6b','rsm','resume screening menu');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES (21,'V1StGXR8_Z6c','cres','candidate resume email search');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(22,'V1StGXR8_Z6d','rdft','resume detail feedback tab');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(23,'V1StGXR8_Z6e','rdht','resume detail history tab');
-- settings
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(24,'V1StGXR8_Z6f','stm','settings menu');
-- email template
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(25,'V1StGXR8_Z6g','etm','email template menu');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(26,'V1StGXR8_Z6h','etc','email template create');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(27,'V1StGXR8_Z6i','ete','email template edit');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(28,'V1StGXR8_Z6j','ets','email template search');
-- interview
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(29,'V1StGXR8_Z6k','iptm','interview process template menu');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(30,'4RStGXR8_Z6k','iptc','interview process template create');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(31,'V1StGXR8_Z6l','ipte','interview process template edit');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(32,'V1StGXR8_Z6m','ipts','interview process template search');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(33,'V1StGXR8_Z6n','ic','interview create');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(34,'V1StGXR8_Z6o','is','interview search');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(35,'V1StGXR8_Z6p','ifc','interview feedback create');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(36,'V1StGXR8_Z6q','ife','interview feedback edit');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(37,'V1StGXR8_Z6r','ifs','interview feedback search');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(38,'V1StGXR8_Z6s','mim','my interview menu');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(39,'V1StGXR8_Z6t','im','interviewer menu');

-- written test
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(40,'V1StGXR8_Z6u','wts','written test search');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(41,'V1StGXR8_Z6v','wtc','written test create');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(42,'V1StGXR8_Z6w','wte','written test edit');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(43,'V1StGXR8_Z6x','wtfs','written test forward search');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(44,'V1StGXR8_Z6y','wtfc','written test forward create');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(45,'V1StGXR8_Z6z','wtfe','written test forward edit');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(46,'T1StGXR8_Z6A','wthrf','written test hr feedback');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(47,'T1StGXR8_Z6B', 'wtrm','written test review menu');

-- user
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(48,'3NStGXR8_Z6C','aus','all user search');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(49,'3NStGXR8_Z6D','uc','user create');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(50,'3NStGXR8_Z6E','ue','user edit');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(51,'3NStGXR8_Z6F','umm','user management menu');
-- tag management
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(52,'3NStGXR8_Z6G','tmm','tag management menu');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(53,'3NStGXR8_Z6H','ats','all tag search');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(54,'3NStGXR8_Z6I','te','tag edit');
INSERT INTO `ats_permission`(`id`, `public_id`, `code`, `name`) VALUES(55,'3NStGXR8_Z6J','td','tag delete');



-- role-permission
-- HR Admin
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,1,'rpm');#not available to HR Recruiter-Recruitment project menu
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,2,'rpe');#not available to HR Recruiter-Recruitment project edit
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,3,'rps');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,4,'jpm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,5,'jpc');#not available to HR Recruiter-Job position create
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,6,'jpe');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,7,'jps');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,8,'tpm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,9,'tpc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,10,'tpe');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,11,'tps');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,12,'crm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,13,'crc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,14,'cre');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,16,'crs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,17,'fcr');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,18,'fcrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,19,'crfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,21,'cres');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,22,'rdft');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,23,'rdht');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,24,'stm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,25,'etm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,26,'etc');#not available to HR Recruiter - email template create
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,27,'ete');#not available to HR Recruiter - email template edit
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,28,'ets');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,29,'iptm');#not available to HR Recruiter - interview process template menu
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,30,'iptc');#not available to HR Recruiter - interview process template create
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,31,'ipte');#not available to HR Recruiter - interview process template edit
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,32,'ipts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,33,'ic');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,34,'is');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,35,'ifc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,37,'ifs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,38,'mim');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,40,'wts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,41,'wtc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,42,'wte');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,43,'wtfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,44,'wtfc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,45,'wtfe');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,46,'wthrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,48,'aus');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,49,'uc');#not available to HR Recruiter - user create
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,50,'ue');#not available to HR Recruiter - user edit
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,51,'umm');#not available to HR Recruiter - user manage
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,52,'tmm');#not available to HR Recruiter - tag manage
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,53,'ats');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,54,'te');#not available to HR Recruiter - tag edit
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,55,'td');#not available to HR Recruiter - tag delete


-- HR Recruiters
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,3,'rps');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,4,'jpm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,6,'jpe');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,7,'jps');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,8,'tpm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,9,'tpc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,10,'tpe');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,11,'tps');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,12,'crm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,13,'crc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,14,'cre');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,16,'crs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,17,'fcr');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,18,'fcrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,19,'crfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,21,'cres');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,22,'rdft');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,23,'rdht');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,24,'stm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,25,'etm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,28,'ets');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,32,'ipts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,33,'ic');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,34,'is');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,35,'ifc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,37,'ifs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,38,'mim');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,40,'wts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,41,'wtc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,42,'wte');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,43,'wtfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,44,'wtfc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,45,'wtfe');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,46,'wthrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,48,'aus');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,53,'ats');

-- Hiring Managers
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,16,'crs');#candidate resume search
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,18,'fcrf');#forward candidate resume
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,19,'crfs');#candidate resume forward search
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,20,'rsm');#resume search menu
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,34,'is');#interview search
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,35,'ifc');#interview feedback create
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,37,'ifs');#interview feedback search
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,38,'mim');#my interview menu
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,39,'im');#interview menu
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,40,'wts');#written test forward search
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,43,'wtfs');#written test forward search
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,53,'ats');

-- Interviewers
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,16,'crs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,18,'fcrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,19,'crfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,20,'rsm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,34,'is');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,35,'ifc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,37,'ifs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,38,'mim');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,39,'im');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,40,'wts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,43,'wtfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,45,'wtfe');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,47,'wtrm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,53,'ats');


-- Candidates

