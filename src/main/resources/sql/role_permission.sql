-- roles: see ats.common.constant.enums.AtsSystemRole
INSERT INTO `ats_role` (`id`, `code`, `name`) VALUES (1, 'sa', 'System Admin');
INSERT INTO `ats_role` (`id`, `code`, `name`) VALUES(2, 'hrd', 'HR Director');
INSERT INTO `ats_role` (`id`, `code`, `name`) VALUES(3, 'proa', 'Project Admin');
INSERT INTO `ats_role` (`id`, `code`, `name`) VALUES(4, 'hrr', 'HR Recruiter');
INSERT INTO `ats_role` (`id`, `code`, `name`) VALUES(5, 'hrrc', 'HR Recruiter Coordinator');
INSERT INTO `ats_role` (`id`, `code`, `name`) VALUES(6, 'hm', 'Hiring Manager');
INSERT INTO `ats_role` (`id`, `code`, `name`) VALUES(7, 'itv', 'Interviewer');
INSERT INTO `ats_role` (`id`, `code`, `name`) VALUES(8, 'cdt', 'Candidate');

-- permissions

-- recruitment
-- recruitment project
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(1,'rpm','recruitment project menu');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(2,'rpe','recruitment project edit'); -- permission to recruitment project`s create,update,delete
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(3,'rps','recruitment project search');
-- job position
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(4,'jpm','job position menu');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(5,'jpc','job position create'); -- permission to job position`s create
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(6,'jpe','job position edit'); -- permission to job position`s update,delete
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(7,'jps','job position search');
-- talent pool
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(8,'tpm','talent pool menu');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES (9,'tpc','talent pool create');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES (10,'tpe','talent pool edit');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES (11,'tps','talent pool search');
-- candidate resume
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(12,'crm','candidate resume menu');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES (13,'crc','candidate resume create');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES (14,'cre','candidate resume edit');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES (15,'crd','candidate resume delete');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES (16,'crs','candidate resume search');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES (17,'fcr','forward candidate resume');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES (18,'fcrf','feedback candidate resume forward');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES (19,'crfs','candidate resume forward search');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES (20,'rsm','resume screening menu');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES (21,'cres','candidate resume email search');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(22,'rdft','resume detail feedback tab');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(23,'rdht','resume detail history tab');
-- settings
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(24,'stm','settings menu');
-- email template
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(25,'etm','email template menu');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(26,'etc','email template create');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(27,'ete','email template edit');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(28,'ets','email template search');
-- interview
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(29,'iptc','interview process template create');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(30,'ipte','interview process template edit');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(31,'ipts','interview process template search');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(32,'ic','interview create');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(33,'is','interview search');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(34,'ifc','interview feedback create');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(35,'ife','interview feedback edit');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(36,'ifs','interview feedback search');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(37,'mim','my interview menu');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(38,'im','interviewer menu');

-- written test
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(39,'wts','written test search');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(40,'wtc','written test create');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(41,'wte','written test edit');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(42,'wtfs','written test forward search');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(43,'wtfc','written test forward create');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(44,'wtfe','written test forward edit');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(45,'wthrf','written test hr feedback');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(46,'wtrm','written test review menu');

-- user
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(47,'aus','all user search');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(48,'uc','user create');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(49,'ue','user edit');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(50,'umm','user management menu');
-- tag management
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(51,'tmm','tag management menu');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(52,'ats','all tag search');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(53,'te','tag edit');
INSERT INTO `ats_permission`(`id`, `code`, `name`) VALUES(54,'td','tag delete');



-- role-permission
-- HR Director
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,2,'rpe');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,3,'rps');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,4,'jpm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,5,'jpc');
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
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,26,'etc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,27,'ete');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,28,'ets');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,31,'ipts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,32,'ic');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,33,'is');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,34,'ifc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,37,'mim');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,47,'aus');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,48,'uc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,49,'ue');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,50,'umm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,36,'ifs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,39,'wts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,40,'wtc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,41,'wte');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,42,'wtfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,43,'wtfc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,44,'wtfe');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,45,'wthrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,51,'tmm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,52,'ats');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,53,'te');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(2,54,'td');

-- Project Admins

INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,1,'rpm');

INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,5,'jpc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,6,'jpe');

INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,26,'etc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,27,'ete');

INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,29,'iptc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,30,'ipte');

INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,51,'tmm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,53,'te');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,54,'td');

INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,3,'rps');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,4,'jpm');
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
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,31,'ipts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,32,'ic');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,33,'is');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,34,'ifc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,36,'ifs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,37,'mim');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,39,'wts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,40,'wtc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,41,'wte');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,42,'wtfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,43,'wtfc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,44,'wtfe');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,45,'wthrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,47,'aus');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(3,52,'ats');


-- HR Recruiters
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,6,'jpe');

INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,3,'rps');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,4,'jpm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,7,'jps');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,8,'tpm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,9,'tpc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,10,'tpe');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,11,'tps');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,12,'crm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,13,'crc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,14,'cre');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,16,'crs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,17,'fcr');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,18,'fcrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,19,'crfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,21,'cres');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,22,'rdft');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,23,'rdht');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,24,'stm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,25,'etm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,28,'ets');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,31,'ipts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,32,'ic');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,33,'is');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,34,'ifc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,36,'ifs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,37,'mim');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,39,'wts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,40,'wtc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,41,'wte');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,42,'wtfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,43,'wtfc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,44,'wtfe');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,45,'wthrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,47,'aus');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(4,52,'ats');

-- HR Recruiter Coordinators
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,3,'rps');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,4,'jpm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,7,'jps');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,8,'tpm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,9,'tpc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,10,'tpe');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,11,'tps');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,12,'crm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,13,'crc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,14,'cre');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,16,'crs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,17,'fcr');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,18,'fcrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,19,'crfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,21,'cres');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,22,'rdft');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,23,'rdht');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,24,'stm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,25,'etm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,28,'ets');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,31,'ipts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,32,'ic');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,33,'is');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,34,'ifc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,36,'ifs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,37,'mim');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,39,'wts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,40,'wtc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,41,'wte');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,42,'wtfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,43,'wtfc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,44,'wtfe');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,45,'wthrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,47,'aus');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(5,52,'ats');

-- Hiring Managers
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,20,'rsm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,16,'crs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,18,'fcrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,19,'crfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,33,'is');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,36,'ifs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,34,'ifc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,38,'im');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,37,'mim');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,39,'wts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,42,'wtfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(6,52,'ats');

-- Interviewers
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,20,'rsm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,16,'crs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,18,'fcrf');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,19,'crfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,33,'is');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,34,'ifc');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,36,'ifs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,38,'im');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,37,'mim');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,39,'wts');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,42,'wtfs');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,44,'wtfe');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,46,'wtrm');
INSERT INTO `ats_role_permission`(`role_id`, `permission_id`, `permission_code`) VALUES(7,52,'ats');


-- Candidates

