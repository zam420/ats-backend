-- roles: see ats.common.constant.enums.AtsSystemRole
INSERT INTO `ats_role` (`id`, `public_id`, `code`, `name`) VALUES (1, 'V1StPuN8_Z5k', 'sa', 'System Admin');
INSERT INTO `ats_role` (`id`, `public_id`, `code`, `name`) VALUES(2, 'V3mGXR8_Z5l', 'ta', 'Tenant Admin');
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
-- evaluation plan
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('n3StGXR8_Z6k','iptsa','evaluation plan template super admin');#write access to system templates for evaluation plans (formaly called interview process)
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('n1StGXR8_Z6k','ipta','evaluation plan template admin');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6k','iptm','evaluation plan template menu');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('4RStGXR8_Z6k','iptc','evaluation plan template create');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6l','ipte','evaluation plan template edit');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6m','ipts','evaluation plan template search');
-- recruitment pipeline
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('t3StGXR8_Z6k','rptsa','recruitment pipeline template super admin');#write access to system templates for recruitment pipeline
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('6fStGXR8_Z6k','rpta','recruitment pipeline template admin');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('ulStGXR8_Z6k','rptm','recruitment pipeline template menu');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('aRStGXR8_Z6k','rptc','recruitment pipeline template create');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('q1ynGXR8_Z6l','rpte','recruitment pipeline template edit');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('b1sgGXR8_Z6m','rpts','recruitment pipeline template search');
-- screening question template
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('n3StGXR8_Z6k','sqtsa','screening question template super admin');#write access to system templates for screening questions
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('n1StGXR8_Z6k','sqta','screening question template admin');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6k','sqtm','screening question template menu');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('4RStGXR8_Z6k','sqtc','screening question template create');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6l','sqte','screening question template edit');
INSERT INTO `ats_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6m','sqts','screening question template search');
-- interview
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
-- System admin
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rpm', `id` FROM `ats_permission` WHERE `code` = 'rpm'; #not available to HR Recruiter-Recruitment project menu
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rpe', `id` FROM `ats_permission` WHERE `code` = 'rpe'; #not available to HR Recruiter-Recruitment project edit
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rps', `id` FROM `ats_permission` WHERE `code` = 'rps';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'jpm', `id` FROM `ats_permission` WHERE `code` = 'jpm';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'jpa', `id` FROM `ats_permission` WHERE `code` = 'jpa'; #not available to HR Recruiter - Job position admin
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'jpc', `id` FROM `ats_permission` WHERE `code` = 'jpc'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'jpe', `id` FROM `ats_permission` WHERE `code` = 'jpe';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'jps', `id` FROM `ats_permission` WHERE `code` = 'jps';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'tpm', `id` FROM `ats_permission` WHERE `code` = 'tpm';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'tpc', `id` FROM `ats_permission` WHERE `code` = 'tpc';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'tpe', `id` FROM `ats_permission` WHERE `code` = 'tpe';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'tps', `id` FROM `ats_permission` WHERE `code` = 'tps';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'crm', `id` FROM `ats_permission` WHERE `code` = 'crm';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'crc', `id` FROM `ats_permission` WHERE `code` = 'crc';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'cre', `id` FROM `ats_permission` WHERE `code` = 'cre';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'crs', `id` FROM `ats_permission` WHERE `code` = 'crs';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'fcr', `id` FROM `ats_permission` WHERE `code` = 'fcr';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'fcrf', `id` FROM `ats_permission` WHERE `code` = 'fcrf';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'crfs', `id` FROM `ats_permission` WHERE `code` = 'crfs';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'cres', `id` FROM `ats_permission` WHERE `code` = 'cres';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rdft', `id` FROM `ats_permission` WHERE `code` = 'rdft';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rdht', `id` FROM `ats_permission` WHERE `code` = 'rdht';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'stm', `id` FROM `ats_permission` WHERE `code` = 'stm';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'etm', `id` FROM `ats_permission` WHERE `code` = 'etm';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'etc', `id` FROM `ats_permission` WHERE `code` = 'etc'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'ete', `id` FROM `ats_permission` WHERE `code` = 'ete'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'ets', `id` FROM `ats_permission` WHERE `code` = 'ets';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'iptsa', `id` FROM `ats_permission` WHERE `code` = 'iptsa'; #not available to HR admin - interview plan template super admin - write access to system termplates for interview plan
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'ipta', `id` FROM `ats_permission` WHERE `code` = 'ipta'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'iptm', `id` FROM `ats_permission` WHERE `code` = 'iptm'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'iptc', `id` FROM `ats_permission` WHERE `code` = 'iptc'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'ipte', `id` FROM `ats_permission` WHERE `code` = 'ipte'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'ipts', `id` FROM `ats_permission` WHERE `code` = 'ipts';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rptsa', `id` FROM `ats_permission` WHERE `code` = 'rptsa'; #not available to HR admin - interview plan template super admin - write access to system termplates for interview plan
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rpta', `id` FROM `ats_permission` WHERE `code` = 'rpta'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rptm', `id` FROM `ats_permission` WHERE `code` = 'rptm'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rptc', `id` FROM `ats_permission` WHERE `code` = 'rptc'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rpte', `id` FROM `ats_permission` WHERE `code` = 'rpte'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rpts', `id` FROM `ats_permission` WHERE `code` = 'rpts';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'sqtsa', `id` FROM `ats_permission` WHERE `code` = 'sqtsa'; #not available to HR admin - screening question template super admin - write access to system termplates for interview plan
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'sqta', `id` FROM `ats_permission` WHERE `code` = 'sqta'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'sqtm', `id` FROM `ats_permission` WHERE `code` = 'sqtm'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'sqtc', `id` FROM `ats_permission` WHERE `code` = 'sqtc'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'sqte', `id` FROM `ats_permission` WHERE `code` = 'sqte'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'sqts', `id` FROM `ats_permission` WHERE `code` = 'sqts';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'ic', `id` FROM `ats_permission` WHERE `code` = 'ic';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'is', `id` FROM `ats_permission` WHERE `code` = 'is';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'ifc', `id` FROM `ats_permission` WHERE `code` = 'ifc';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'ifs', `id` FROM `ats_permission` WHERE `code` = 'ifs';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'mim', `id` FROM `ats_permission` WHERE `code` = 'mim';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'wts', `id` FROM `ats_permission` WHERE `code` = 'wts';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'wtc', `id` FROM `ats_permission` WHERE `code` = 'wtc';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'wte', `id` FROM `ats_permission` WHERE `code` = 'wte';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'wtfs', `id` FROM `ats_permission` WHERE `code` = 'wtfs';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'wtfc', `id` FROM `ats_permission` WHERE `code` = 'wtfc';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'wtfe', `id` FROM `ats_permission` WHERE `code` = 'wtfe';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'wthrf', `id` FROM `ats_permission` WHERE `code` = 'wthrf';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'aus', `id` FROM `ats_permission` WHERE `code` = 'aus';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'uc', `id` FROM `ats_permission` WHERE `code` = 'uc'; #not available to HR Recruiter - user create
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'ue', `id` FROM `ats_permission` WHERE `code` = 'ue'; #not available to HR Recruiter - user edit
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'umm', `id` FROM `ats_permission` WHERE `code` = 'umm'; #not available to HR Recruiter - user manage
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'tmm', `id` FROM `ats_permission` WHERE `code` = 'tmm'; #not available to HR Recruiter - tag manage
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'ats', `id` FROM `ats_permission` WHERE `code` = 'ats';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'te', `id` FROM `ats_permission` WHERE `code` = 'te'; #not available to HR Recruiter - tag edit
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'td', `id` FROM `ats_permission` WHERE `code` = 'td'; #not available to HR Recruiter - tag delete

-- HR Admin
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'rpm', `id` FROM `ats_permission` WHERE `code` = 'rpm'; #not available to HR Recruiter-Recruitment project menu
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'rpe', `id` FROM `ats_permission` WHERE `code` = 'rpe'; #not available to HR Recruiter-Recruitment project edit
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'rps', `id` FROM `ats_permission` WHERE `code` = 'rps';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'jpa', `id` FROM `ats_permission` WHERE `code` = 'jpa'; #not available to HR Recruiter - Job position admin
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'jpm', `id` FROM `ats_permission` WHERE `code` = 'jpm';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'jpc', `id` FROM `ats_permission` WHERE `code` = 'jpc'; 
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
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'rpta', `id` FROM `ats_permission` WHERE `code` = 'rpta'; #not available to HR Recruiter - recruitment pipeline template admin
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'rptm', `id` FROM `ats_permission` WHERE `code` = 'rptm'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'rptc', `id` FROM `ats_permission` WHERE `code` = 'rptc'; #not available to HR Recruiter - recruitment pipeline template create
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'rpte', `id` FROM `ats_permission` WHERE `code` = 'rpte'; #not available to HR Recruiter - recruitment pipeline template edit
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'rpts', `id` FROM `ats_permission` WHERE `code` = 'rpts'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'sqta', `id` FROM `ats_permission` WHERE `code` = 'sqta'; #not available to HR Recruiter - screening question template admin
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'sqtm', `id` FROM `ats_permission` WHERE `code` = 'sqtm'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'sqtc', `id` FROM `ats_permission` WHERE `code` = 'sqtc'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'sqte', `id` FROM `ats_permission` WHERE `code` = 'sqte'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'sqts', `id` FROM `ats_permission` WHERE `code` = 'sqts';
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

```sql
-- HR Recruiters
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'rps', `id` FROM `ats_permission` WHERE `code` = 'rps';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'jpm', `id` FROM `ats_permission` WHERE `code` = 'jpm';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'jpc', `id` FROM `ats_permission` WHERE `code` = 'jpc'; 
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
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'rptm', `id` FROM `ats_permission` WHERE `code` = 'rptm';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'rpts', `id` FROM `ats_permission` WHERE `code` = 'rpts';  
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'sqtm', `id` FROM `ats_permission` WHERE `code` = 'sqtm'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'sqtc', `id` FROM `ats_permission` WHERE `code` = 'sqtc'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'sqte', `id` FROM `ats_permission` WHERE `code` = 'sqte'; 
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 3, 'sqts', `id` FROM `ats_permission` WHERE `code` = 'sqts';
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
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 4, 'jpm', `id` FROM `ats_permission` WHERE `code` = 'jpm';
INSERT INTO `ats_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 4, 'jps', `id` FROM `ats_permission` WHERE `code` = 'jps';
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


--Tenants
# NOTE: While creating tenants, there will be a problem that is that the first user creation requires tenant, but tenant creation in turn requires user
# TO SOLVE: Temporarily make the created_by_user_id field in tenant table to be nullable
INSERT INTO ats.tenant(id, public_id, name, description, type, deactivated, created_at, created_by_user_id ) VALUES (1, 'V1StGXR8_Z5k', 'Owner', 'For ATS owning entity', 'OWNER', 0, NOW(), 1);
INSERT INTO ats.tenant(id, public_id, name, description, type, deactivated, created_at, created_by_user_id ) VALUES (2, 'V1StGXR8_Z5j', 'Public', 'For public e.g. for candidates', 'PUBLIC', 0, NOW(), 1);

-- Evaluation plan round types
INSERT INTO ats.evaluation_plan_round_type (`id`,`description`,`name`,`is_custom`) VALUES (1,'HR evaluation','HR', 0);
INSERT INTO ats.evaluation_plan_round_type (`id`,`description`,`name`,`is_custom`) VALUES (2,'Functional/Skills evaluation','Functional', 0);
INSERT INTO ats.evaluation_plan_round_type (`id`,`description`,`name`,`is_custom`) VALUES (3,'Executive evaluation','Executive', 0);
INSERT INTO ats.evaluation_plan_round_type (`id`,`description`,`name`,`is_custom`) VALUES (4,'Evaluation of any other type','Custom', 1);

-- Recruitment Pipeline round types
INSERT INTO ats.recruitment_pipeline_round_type (`id`,`description`,`name`,`is_custom`) VALUES (1,'Candidates applied','Applied', 0);
INSERT INTO ats.recruitment_pipeline_round_type (`id`,`description`,`name`,`is_custom`) VALUES (2,'Under application review','Screening', 0);
INSERT INTO ats.recruitment_pipeline_round_type (`id`,`description`,`name`,`is_custom`) VALUES (3,'Under evaluations','Evaluation', 0);
INSERT INTO ats.recruitment_pipeline_round_type (`id`,`description`,`name`,`is_custom`) VALUES (4,'Candidates offered','Offer', 0);
INSERT INTO ats.recruitment_pipeline_round_type (`id`,`description`,`name`,`is_custom`) VALUES (5,'Candidates hired','Hired', 0);
INSERT INTO ats.recruitment_pipeline_round_type (`id`,`description`,`name`,`is_custom`) VALUES (6,'Candidates onboarded','Onboarded', 0);
INSERT INTO ats.recruitment_pipeline_round_type (`id`,`description`,`name`,`is_custom`) VALUES (1,'Custom stage','Custom', 1);

-- Job Employment Types
INSERT INTO `ats`.`job_employment_type` (`name`) VALUES ('Full Time');
INSERT INTO `ats`.`job_employment_type` (`name`) VALUES ('Part Time');
INSERT INTO `ats`.`job_employment_type` (`name`) VALUES ('Contract');
INSERT INTO `ats`.`job_employment_type` (`name`) VALUES ('Intern');
INSERT INTO `ats`.`job_employment_type` (`name`) VALUES ('Temporary');
INSERT INTO `ats`.`job_employment_type` (`name`) VALUES ('Volunteer');

-- Job Work Arrangement Types
INSERT INTO `ats`.`job_work_arrangement_type` (`name`) VALUES ('Onsite');
INSERT INTO `ats`.`job_work_arrangement_type` (`name`) VALUES ('Remote');
INSERT INTO `ats`.`job_work_arrangement_type` (`name`) VALUES ('Hybrid');

-- Users
# System Virtual User
INSERT INTO ats.ats_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (1,'V1StGtR8_vbu','no-reply@fsworkpaper.ai',NULL,'$2a$10$OVNWgRHLRvM6zDn6PDkjv.xzyh1n.zJvhun75xTZ554SgkIFDAxaW',0,0,'2026-10-18 19:22:51.634567',NULL,NULL,'2026-10-18 19:22:51.450781','2026-10-18 19:22:51.450781',0,0,'System',0,NULL,0,NULL,NULL,NULL,NULL,'$2a$10$nuwwU.V9vhBp0LfCxqyeHu',NULL,NULL,1);
# Tenant Users
INSERT INTO ats.ats_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (11,'V1StGXR8_Z5j','antoine@fsworkpaper.ai',NULL,NULL,0,0,'2026-10-18 15:36:35.000000',NULL,NULL,NULL,NULL,0,0,'Antoine',0,'Boquen',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO ats.ats_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (12,'TnStGXR8_Z5k','ats_admin@fsworkpaper.ai',NULL,NULL,0,0,'2026-10-18 15:36:35.000000',NULL,NULL,NULL,NULL,0,0,'Admin',0,'User',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO ats.ats_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (13,'k5StGXR8_uqP','nadia@fsworkpaper.ai',NULL,NULL,0,0,'2026-10-18 15:36:35.000000',NULL,NULL,NULL,NULL,0,0,'Nadia',0,'P.',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO ats.ats_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (14,'V2InXR8_Z5n','nan.li@fsworkpaper.ai',NULL,NULL,0,0,'2026-10-18 15:36:35.000000',NULL,NULL,NULL,NULL,0,0,'Nan',0,'Li',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO ats.ats_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (15,'V1StGXR8_Z5o','ats_rec2@fsworkpaper.ai',NULL,NULL,0,0,'2026-10-18 15:36:35.000000',NULL,NULL,NULL,NULL,0,0,'Rec 2',0,'User',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO ats.ats_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (16,'V1StGXR8_Z5t','hrd2@fsworkpaper.ai',NULL,NULL,0,0,'2026-10-18 15:36:35.000000',NULL,NULL,NULL,NULL,0,0,'HRD 2',0,'User',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO ats.ats_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (17,'V1StGXR8_Z5u','ats_rec4@fsworkpaper.ai',NULL,NULL,0,0,'2026-10-18 15:36:35.000000',NULL,NULL,NULL,NULL,0,0,'Rec 4',0,'User',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO ats.ats_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (18,'V1StGXR8_vbu','damien@fsworkpaper.ai',NULL,'$2a$10$OVNWgRHLRvM6zDn6PDkjv.xzyh1n.zJvhun75xTZ554SgkIFDAxaW',0,0,'2026-10-18 15:36:35.000000',NULL,NULL,'2026-10-18 19:25:00.450781','2026-10-18 19:22:51.450781',0,0,'Damien',0,'Polegato',0,NULL,NULL,NULL,NULL,'$2a$10$nuwwU.V9vhBp0LfCxqyeHu',NULL,NULL,1);
INSERT INTO ats.ats_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (19,'V1StGtR8_vbu','juan@fsworkpaper.ai',NULL,'$2a$10$OVNWgRHLRvM6zDn6PDkjv.xzyh1n.zJvhun75xTZ554SgkIFDAxaW',0,0,'2026-10-18 15:36:35.000000',NULL,NULL,'2026-10-18 19:25:00.450781','2026-10-18 19:22:51.450781',0,0,'Juan',0,'Pablo',0,NULL,NULL,NULL,NULL,'$2a$10$nuwwU.V9vhBp0LfCxqyeHu',NULL,NULL,1);
INSERT INTO ats.ats_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (20,'OzqnguSD0CPf','zam420@live.com',NULL,'$2a$10$OVNWgRHLRvM6zDn6PDkjv.xzyh1n.zJvhun75xTZ554SgkIFDAxaW',0,0,'2026-10-18 19:22:51.634567','wGdHynhQ','2026-10-18 19:22:51.450781','2026-10-18 19:25:00.450781','2026-10-18 19:22:51.450781',0,0,'Zam',0,'Chaudhry',0,NULL,NULL,NULL,NULL,'$2a$10$nuwwU.V9vhBp0LfCxqyeHu',NULL,NULL,1);
# Candidate Users
INSERT INTO ats.ats_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (1000,'OzqngmSD0CPf','candidate@fsworkpaper.ai',NULL,'$2a$10$OVNWgRHLRvM6zDn6PDkjv.xzyh1n.zJvhun75xTZ554SgkIFDAxaW',1,0,'2026-10-18 19:22:51.634567','wGdHynhQ','2026-10-18 19:22:51.450781','2026-10-18 19:25:00.450781','2026-10-18 19:22:51.450781',0,0,'Poor',0,'Candidate',0,NULL,NULL,NULL,NULL,'$2a$10$nuwwU.V9vhBp0LfCxqyeHu',NULL,NULL,2);

# User Roles
INSERT INTO ats.ats_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5a','sa',1,12,'TnStGXR8_Z5k');
INSERT INTO ats.ats_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5b','itv',7,12,'TnStGXR8_Z5k');
INSERT INTO ats.ats_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5c','hrd',2,13,'k5StGXR8_uqP');
INSERT INTO ats.ats_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5d','itv',7,13,'k5StGXR8_uqP');
INSERT INTO ats.ats_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5h','rec',3,14,'V2InXR8_Z5n');
INSERT INTO ats.ats_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5i','itv',7,14,'V2InXR8_Z5n');
INSERT INTO ats.ats_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5j','rec',3,15,'V1StGXR8_Z5o');
INSERT INTO ats.ats_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5k','itv',7,15,'V1StGXR8_Z5o');
INSERT INTO ats.ats_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5q','hrd',2,16,'V1StGXR8_Z5t');
INSERT INTO ats.ats_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5r','rec',3,16,'V1StGXR8_Z5t');
INSERT INTO ats.ats_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5s','itv',7,16,'V1StGXR8_Z5t');
INSERT INTO ats.ats_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5t','hrr',4,17,'V1StGXR8_Z5u');
INSERT INTO ats.ats_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5u','itv',7,17,'V1StGXR8_Z5u');
INSERT INTO ats.ats_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('IrOQeOfgK89F','itv',7,18,'V1StGXR8_vbu');
INSERT INTO ats.ats_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('IrOQeOfmn89F','itv',7,19,'V1StGtR8_vbu');
INSERT INTO ats.ats_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('IrOQeOfgK7VF','hra',2,20,'OzqnguSD0CPf');
INSERT INTO ats.ats_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('IbnveOfmn89F','cdt',6,1000,'OzqngmSD0CPf');


-- Add System Tasks
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('email_resume_parser', NOW(), 'recruit@espressif.com');
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('email_resume_parser', NOW(), 'campus@espressif.com');
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('email_resume_parser', NOW(), 'campus.in@espressif.com');
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('email_resume_parser', NOW(), 'recruit.in@espressif.com');
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('email_resume_parser', NOW(), 'recruit.cz@espressif.com');
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('resume_duplication_schedule', NOW(), NULL);
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('interview_meeting_rsp_handler_task', NOW(), NULL);
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('exam_update_nowcoder_schedule', NOW(), NULL);
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('auto_tag_resumes_for_fresh_resumes', NOW(), NULL);
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('auto_tag_resumes_for_fresh_tags', NOW(), NULL);
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('email_task_schedule', '1', NULL);
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('interview_time_task', '1', NULL);
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('onboarding_time_watch_dog', '1', NULL);
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('mail_counter_init', '1', NULL);

-- history resume's tag management
INSERT IGNORE INTO ats_tag(`name`,`created_by_id`,`created_at`,`updated_at`)
SELECT
    DISTINCT(JSON_UNQUOTE(JSON_EXTRACT(tags,CONCAT('$[', help_topic.help_topic_id, ']')))) AS `name`,t.id as `created_by_id`,NOW() as `created_at`,NOW() as `updated_at`
FROM
    ats_candidate
    JOIN mysql.help_topic ON help_topic.help_topic_id < JSON_LENGTH(tags)
		JOIN (select id from ats_user where type=2 limit 1)t
WHERE
    JSON_VALID(tags) AND JSON_LENGTH(tags);


INSERT INTO ats_resume_tags(match_type,candidate_resume_id,tag_id,match_count)
SELECT 0,t.resume_id,ats_tag.id,1 FROM(
    SELECT ats_candidate_resume.id AS resume_id,JSON_UNQUOTE(JSON_EXTRACT(ats_candidate.tags,CONCAT('$[', help_topic.help_topic_id, ']'))) AS tag_name
    FROM ats_candidate_resume
        JOIN mysql.help_topic ON help_topic.help_topic_id < JSON_LENGTH(ats_candidate.tags)
        INNER JOIN ats_candidate ON ats_candidate_resume.candidate_id = ats_candidate.id
    WHERE JSON_VALID(ats_candidate.tags) AND JSON_LENGTH(ats_candidate.tags)
) t INNER JOIN ats_tag ON t.tag_name = ats_tag.`name`;

