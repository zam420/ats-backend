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
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z5s','rpm','D menu');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V3mGXR8_Z5l','rpe','recruitment project edit'); -- permission to recruitment project`s create,update,delete
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1QUGRR8_Z5a','rps','recruitment project search');
-- job position
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StPos8_Z5r','jpm','job position menu');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V9StGFN8_Z5b','jpc','job position create'); -- permission to job position`s create
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V8pRGBH8_Anw','jpe','job position edit'); -- permission to job position`s update,delete
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V9YtGXR8_Z5n','jps','job position search');
-- talent pool
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1SpnTR8_Zbz','tpm','talent pool menu');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES(9,'V3lOyXR8_Z60','tpc','talent pool create');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V0OPqXR8_Z61','tpe','talent pool edit');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V5StGXR8_Z62','tps','talent pool search');
-- candidate resume
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V9pQGXR8_Z63','crm','candidate resume menu');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('VL3nGXR8_Z64','crc','candidate resume create');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z65','cre','candidate resume edit');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z66','crd','candidate resume delete');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z67','crs','candidate resume search');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z68','fcr','forward candidate resume');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z69','fcrf','feedback candidate resume forward');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6a','crfs','candidate resume forward search');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6b','rsm','resume screening menu');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6c','cres','candidate resume email search');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6d','rdft','resume detail feedback tab');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6e','rdht','resume detail history tab');
-- settings
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6f','stm','settings menu');
-- email template
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6g','etm','email template menu');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6h','etc','email template create');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6i','ete','email template edit');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6j','ets','email template search');
-- interview
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('n1StGXR8_Z6k','ipta','interview plan template admin');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6k','iptm','interview plan template menu');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('4RStGXR8_Z6k','iptc','interview plan template create');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6l','ipte','interview plan template edit');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6m','ipts','interview plan template search');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6n','ic','interview create');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6o','is','interview search');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6p','ifc','interview feedback create');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6q','ife','interview feedback edit');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6r','ifs','interview feedback search');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6s','mim','my interview menu');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6t','im','interviewer menu');

-- written test
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6u','wts','written test search');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6v','wtc','written test create');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6w','wte','written test edit');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6x','wtfs','written test forward search');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6y','wtfc','written test forward create');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6z','wtfe','written test forward edit');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('T1StGXR8_Z6A','wthrf','written test hr feedback');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('T1StGXR8_Z6B', 'wtrm','written test review menu');

-- user
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('3NStGXR8_Z6C','aus','all user search');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('3NStGXR8_Z6D','uc','user create');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('3NStGXR8_Z6E','ue','user edit');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('3NStGXR8_Z6F','umm','user management menu');
-- tag management
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('3NStGXR8_Z6G','tmm','tag management menu');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('3NStGXR8_Z6H','ats','all tag search');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('3NStGXR8_Z6I','te','tag edit');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('3NStGXR8_Z6J','td','tag delete');



-- role-permission
-- HR Admin
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'rpm', `id` FROM `ats_permission` WHERE `code` = 'rpm'; #not available to HR Recruiter-Recruitment project menu
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'rpe', `id` FROM `ats_permission` WHERE `code` = 'rpe'; #not available to HR Recruiter-Recruitment project edit
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'rps', `id` FROM `ats_permission` WHERE `code` = 'rps';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'jpm', `id` FROM `ats_permission` WHERE `code` = 'jpm';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'jpc', `id` FROM `ats_permission` WHERE `code` = 'jpc'; #not available to HR Recruiter-Job position create
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'jpe', `id` FROM `ats_permission` WHERE `code` = 'jpe';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'jps', `id` FROM `ats_permission` WHERE `code` = 'jps';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'tpm', `id` FROM `ats_permission` WHERE `code` = 'tpm';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'tpc', `id` FROM `ats_permission` WHERE `code` = 'tpc';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'tpe', `id` FROM `ats_permission` WHERE `code` = 'tpe';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'tps', `id` FROM `ats_permission` WHERE `code` = 'tps';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'crm', `id` FROM `ats_permission` WHERE `code` = 'crm';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'crc', `id` FROM `ats_permission` WHERE `code` = 'crc';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'cre', `id` FROM `ats_permission` WHERE `code` = 'cre';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'crs', `id` FROM `ats_permission` WHERE `code` = 'crs';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'fcr', `id` FROM `ats_permission` WHERE `code` = 'fcr';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'fcrf', `id` FROM `ats_permission` WHERE `code` = 'fcrf';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'crfs', `id` FROM `ats_permission` WHERE `code` = 'crfs';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'cres', `id` FROM `ats_permission` WHERE `code` = 'cres';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'rdft', `id` FROM `ats_permission` WHERE `code` = 'rdft';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'rdht', `id` FROM `ats_permission` WHERE `code` = 'rdht';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'stm', `id` FROM `ats_permission` WHERE `code` = 'stm';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'etm', `id` FROM `ats_permission` WHERE `code` = 'etm';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'etc', `id` FROM `ats_permission` WHERE `code` = 'etc'; #not available to HR Recruiter - email template create
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'ete', `id` FROM `ats_permission` WHERE `code` = 'ete'; #not available to HR Recruiter - email template edit
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'ets', `id` FROM `ats_permission` WHERE `code` = 'ets';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'ipta', `id` FROM `ats_permission` WHERE `code` = 'ipta'; #not available to HR Recruiter - interview plan template admin
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'iptm', `id` FROM `ats_permission` WHERE `code` = 'iptm'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'iptc', `id` FROM `ats_permission` WHERE `code` = 'iptc'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'ipte', `id` FROM `ats_permission` WHERE `code` = 'ipte'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'ipts', `id` FROM `ats_permission` WHERE `code` = 'ipts';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'ic', `id` FROM `ats_permission` WHERE `code` = 'ic';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'is', `id` FROM `ats_permission` WHERE `code` = 'is';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'ifc', `id` FROM `ats_permission` WHERE `code` = 'ifc';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'ifs', `id` FROM `ats_permission` WHERE `code` = 'ifs';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'mim', `id` FROM `ats_permission` WHERE `code` = 'mim';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'wts', `id` FROM `ats_permission` WHERE `code` = 'wts';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'wtc', `id` FROM `ats_permission` WHERE `code` = 'wtc';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'wte', `id` FROM `ats_permission` WHERE `code` = 'wte';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'wtfs', `id` FROM `ats_permission` WHERE `code` = 'wtfs';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'wtfc', `id` FROM `ats_permission` WHERE `code` = 'wtfc';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'wtfe', `id` FROM `ats_permission` WHERE `code` = 'wtfe';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'wthrf', `id` FROM `ats_permission` WHERE `code` = 'wthrf';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'aus', `id` FROM `ats_permission` WHERE `code` = 'aus';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'uc', `id` FROM `ats_permission` WHERE `code` = 'uc'; #not available to HR Recruiter - user create
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'ue', `id` FROM `ats_permission` WHERE `code` = 'ue'; #not available to HR Recruiter - user edit
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'umm', `id` FROM `ats_permission` WHERE `code` = 'umm'; #not available to HR Recruiter - user manage
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'tmm', `id` FROM `ats_permission` WHERE `code` = 'tmm'; #not available to HR Recruiter - tag manage
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'ats', `id` FROM `ats_permission` WHERE `code` = 'ats';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'te', `id` FROM `ats_permission` WHERE `code` = 'te'; #not available to HR Recruiter - tag edit
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'td', `id` FROM `ats_permission` WHERE `code` = 'td'; #not available to HR Recruiter - tag delete
Here is the updated SQL script with the dynamic `permission_id` subquery, formatted with the `INSERT` and `SELECT` on the same line and without empty lines between statements:

```sql
-- HR Recruiters
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'rps', `id` FROM `ats_permission` WHERE `code` = 'rps';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'jpm', `id` FROM `ats_permission` WHERE `code` = 'jpm';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'jpe', `id` FROM `ats_permission` WHERE `code` = 'jpe';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'jps', `id` FROM `ats_permission` WHERE `code` = 'jps';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'tpm', `id` FROM `ats_permission` WHERE `code` = 'tpm';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'tpc', `id` FROM `ats_permission` WHERE `code` = 'tpc';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'tpe', `id` FROM `ats_permission` WHERE `code` = 'tpe';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'tps', `id` FROM `ats_permission` WHERE `code` = 'tps';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'crm', `id` FROM `ats_permission` WHERE `code` = 'crm';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'crc', `id` FROM `ats_permission` WHERE `code` = 'crc';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'cre', `id` FROM `ats_permission` WHERE `code` = 'cre';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'crs', `id` FROM `ats_permission` WHERE `code` = 'crs';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'fcr', `id` FROM `ats_permission` WHERE `code` = 'fcr';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'fcrf', `id` FROM `ats_permission` WHERE `code` = 'fcrf';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'crfs', `id` FROM `ats_permission` WHERE `code` = 'crfs';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'cres', `id` FROM `ats_permission` WHERE `code` = 'cres';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'rdft', `id` FROM `ats_permission` WHERE `code` = 'rdft';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'rdht', `id` FROM `ats_permission` WHERE `code` = 'rdht';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'stm', `id` FROM `ats_permission` WHERE `code` = 'stm';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'etm', `id` FROM `ats_permission` WHERE `code` = 'etm';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'ets', `id` FROM `ats_permission` WHERE `code` = 'ets';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'iptm', `id` FROM `ats_permission` WHERE `code` = 'iptm'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'iptc', `id` FROM `ats_permission` WHERE `code` = 'iptc'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'ipte', `id` FROM `ats_permission` WHERE `code` = 'ipte'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'ipts', `id` FROM `ats_permission` WHERE `code` = 'ipts';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'ic', `id` FROM `ats_permission` WHERE `code` = 'ic';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'is', `id` FROM `ats_permission` WHERE `code` = 'is';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'ifc', `id` FROM `ats_permission` WHERE `code` = 'ifc';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'ifs', `id` FROM `ats_permission` WHERE `code` = 'ifs';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'mim', `id` FROM `ats_permission` WHERE `code` = 'mim';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'wts', `id` FROM `ats_permission` WHERE `code` = 'wts';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'wtc', `id` FROM `ats_permission` WHERE `code` = 'wtc';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'wte', `id` FROM `ats_permission` WHERE `code` = 'wte';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'wtfs', `id` FROM `ats_permission` WHERE `code` = 'wtfs';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'wtfc', `id` FROM `ats_permission` WHERE `code` = 'wtfc';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'wtfe', `id` FROM `ats_permission` WHERE `code` = 'wtfe';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'wthrf', `id` FROM `ats_permission` WHERE `code` = 'wthrf';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'aus', `id` FROM `ats_permission` WHERE `code` = 'aus';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'ats', `id` FROM `ats_permission` WHERE `code` = 'ats';

-- Hiring Managers
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 4, 'crs', `id` FROM `ats_permission` WHERE `code` = 'crs'; #candidate resume search
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 4, 'fcrf', `id` FROM `ats_permission` WHERE `code` = 'fcrf'; #forward candidate resume
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 4, 'crfs', `id` FROM `ats_permission` WHERE `code` = 'crfs'; #candidate resume forward search
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 4, 'rsm', `id` FROM `ats_permission` WHERE `code` = 'rsm'; #resume search menu
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 4, 'is', `id` FROM `ats_permission` WHERE `code` = 'is'; #interview search
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 4, 'ifc', `id` FROM `ats_permission` WHERE `code` = 'ifc'; #interview feedback create
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 4, 'ifs', `id` FROM `ats_permission` WHERE `code` = 'ifs'; #interview feedback search
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 4, 'mim', `id` FROM `ats_permission` WHERE `code` = 'mim'; #my interview menu
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 4, 'im', `id` FROM `ats_permission` WHERE `code` = 'im'; #interview menu
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 4, 'wts', `id` FROM `ats_permission` WHERE `code` = 'wts'; #written test forward search
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 4, 'wtfs', `id` FROM `ats_permission` WHERE `code` = 'wtfs'; #written test forward search
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 4, 'ats', `id` FROM `ats_permission` WHERE `code` = 'ats';

-- Interviewers
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 5, 'crs', `id` FROM `ats_permission` WHERE `code` = 'crs';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 5, 'fcrf', `id` FROM `ats_permission` WHERE `code` = 'fcrf';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 5, 'crfs', `id` FROM `ats_permission` WHERE `code` = 'crfs';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 5, 'rsm', `id` FROM `ats_permission` WHERE `code` = 'rsm';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 5, 'is', `id` FROM `ats_permission` WHERE `code` = 'is';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 5, 'ifc', `id` FROM `ats_permission` WHERE `code` = 'ifc';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 5, 'ifs', `id` FROM `ats_permission` WHERE `code` = 'ifs';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 5, 'mim', `id` FROM `ats_permission` WHERE `code` = 'mim';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 5, 'im', `id` FROM `ats_permission` WHERE `code` = 'im';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 5, 'wts', `id` FROM `ats_permission` WHERE `code` = 'wts';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 5, 'wtfs', `id` FROM `ats_permission` WHERE `code` = 'wtfs';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 5, 'wtfe', `id` FROM `ats_permission` WHERE `code` = 'wtfe';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 5, 'wtrm', `id` FROM `ats_permission` WHERE `code` = 'wtrm';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 5, 'ats', `id` FROM `ats_permission` WHERE `code` = 'ats';

```
-- Candidates

