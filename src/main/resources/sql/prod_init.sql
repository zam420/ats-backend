-- roles: see ats.common.constant.enums.AtsSystemRole
INSERT INTO `rbac_role` (`id`, `public_id`, `code`, `name`) VALUES (1, 'V1StPuN8_Z5k', 'sa', 'System Admin');
INSERT INTO `rbac_role` (`id`, `public_id`, `code`, `name`) VALUES (2, 'eNStPuN8_Z5k', 'clna', 'Client Admin');
INSERT INTO `rbac_role` (`id`, `public_id`, `code`, `name`) VALUES(101, 'V3mGXR8_Z5l', 'ta', 'Tenant Admin');
INSERT INTO `rbac_role` (`id`, `public_id`, `code`, `name`) VALUES(102, 'V9YtGXR8_Z5n', 'rec', 'Recruiter');
INSERT INTO `rbac_role` (`id`, `public_id`, `code`, `name`) VALUES(103, 'V8pRGBH8_Z5w', 'hm', 'Hiring Manager');
INSERT INTO `rbac_role` (`id`, `public_id`, `code`, `name`) VALUES(104, 'V1StGFN8_Z5b', 'itv', 'Interviewer');
INSERT INTO `rbac_role` (`id`, `public_id`, `code`, `name`) VALUES(200, 'V1StPos8_Z5r', 'cdt', 'Candidate');

# permissions

-- Tenant
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('kEStPos8_Z5r','clnsa','Client System Admin');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('wbStPos8_Z5r','clna','Client Admin');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('R1StPos8_Z5r','clnm','Client Menu');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('VwStGFN8_Z5b','clnc','Client Create'); 
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('uzpRGBH8_Anw','clne','Client Edit'); 
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V9YtXRG8_Z5n','clns','Client Search');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('VpTtXRG8_Z5n','clncur','Client Current');#get logged in user's tenant's own logo-available to all roles except Candidate
-- job position
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StPos8_Z5r','jpm','job position menu');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V9StGFN8_Z5b','jpc','job position create'); 
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V8pRGBH8_Anw','jpe','job position edit'); 
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V9YtGXR8_Z5n','jps','job position search');
-- job position
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StPos8_Z5r','jpm','job position menu');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V9StGFN8_Z5b','jpc','job position create'); 
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V8pRGBH8_Anw','jpe','job position edit'); 
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V9YtGXR8_Z5n','jps','job position search');
-- talent pool
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1SpnTR8_Zbz','tpm','talent pool menu');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES(9,'V3lOyXR8_Z60','tpc','talent pool create');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V0OPqXR8_Z61','tpe','talent pool edit');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V5StGXR8_Z62','tps','talent pool search');
-- candidate resume
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V9pQGXR8_Z63','crm','candidate resume menu');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('VL3nGXR8_Z64','crc','candidate resume create');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z65','cre','candidate resume edit');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z66','crd','candidate resume delete');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z67','crs','candidate resume search');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z68','fcr','forward candidate resume');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z69','fcrf','feedback candidate resume forward');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6a','crfs','candidate resume forward search');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6b','rsm','resume screening menu');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6c','cres','candidate resume email search');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6d','rdft','resume detail feedback tab');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6e','rdht','resume detail history tab');
-- settings
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6f','stm','settings menu');
-- email template
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6g','etm','email template menu');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6h','etc','email template create');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6i','ete','email template edit');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6j','ets','email template search');
-- evaluation plan
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('n3StGXR8_Z6k','iptsa','evaluation plan template super admin');#write access to system templates for evaluation plans (formaly called interview process)
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('n1StGXR8_Z6k','ipta','evaluation plan template admin');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6k','iptm','evaluation plan template menu');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('4RStGXR8_Z6k','iptc','evaluation plan template create');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6l','ipte','evaluation plan template edit');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6m','ipts','evaluation plan template search');
-- recruitment pipeline
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('t3StGXR8_Z6k','rptsa','recruitment pipeline template super admin');#write access to system templates for recruitment pipeline
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('6fStGXR8_Z6k','rpta','recruitment pipeline template admin');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('ulStGXR8_Z6k','rptm','recruitment pipeline template menu');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('aRStGXR8_Z6k','rptc','recruitment pipeline template create');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('q1ynGXR8_Z6l','rpte','recruitment pipeline template edit');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('b1sgGXR8_Z6m','rpts','recruitment pipeline template search');
-- screening question template
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('n3StGaR8_Z6k','sqtsa','screening question forms super admin');#write access to system formss for screening questions
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('n1StGvR8_Z6k','sqta','screening question forms admin');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StaXR8_Z6k','sqtm','screening question forms menu');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('4RStGeR8_Z6k','sqtc','screening question forms create');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1SvGXR8_Z6l','sqte','screening question forms edit');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXt8_Z6m','sqts','screening question forms search');
-- interview
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6n','ic','interview create');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6o','is','interview search');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6p','ifc','interview feedback create');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6q','ife','interview feedback edit');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6r','ifs','interview feedback search');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6s','mim','my interview menu');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6t','im','interviewer menu');

-- written test
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6u','wts','written test search');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6v','wtc','written test create');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6w','wte','written test edit');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6x','wtfs','written test forward search');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6y','wtfc','written test forward create');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('V1StGXR8_Z6z','wtfe','written test forward edit');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('T1StGXR8_Z6A','wthrf','written test hr feedback');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('T1StGXR8_Z6B', 'wtrm','written test review menu');

-- user
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('3NStGXR8_Z6C','aus','all user search');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('3NStGXR8_Z6D','uc','user create');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('3NStGXR8_Z6E','ue','user edit');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('3NStGXR8_Z6F','umm','user management menu');
-- tag management
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('3NStGXR8_Z6G','tmm','tag management menu');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('3NStGXR8_Z6H','ats','all tag search');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('3NStGXR8_Z6I','te','tag edit');
INSERT INTO `rbac_permission`(`public_id`, `code`, `name`) VALUES('3NStGXR8_Z6J','td','tag delete');



-- role-permission
-- System admin
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'clnsa', `id` FROM `rbac_permission` WHERE `code` = 'clnsa'; #Only available to System Admin - Client System admin
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'clnm', `id` FROM `rbac_permission` WHERE `code` = 'clnm'; #Only available to Client Admin and System Admin - Client admin
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'clna', `id` FROM `rbac_permission` WHERE `code` = 'clna'; #Only available to Client Admin and System Admin - Client admin
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'clnc', `id` FROM `rbac_permission` WHERE `code` = 'clnc'; #Only available to Client Admin and System Admin - Client create
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'clne', `id` FROM `rbac_permission` WHERE `code` = 'clne'; #Only available to Client Admin and System Admin - Client edit
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'clns', `id` FROM `rbac_permission` WHERE `code` = 'clns'; #Only available to Client Admin and System Admin - Client search
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'clncur', `id` FROM `rbac_permission` WHERE `code` = 'clncur'; #available to all roles except Candidate
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rpm', `id` FROM `rbac_permission` WHERE `code` = 'rpm'; #not available to HR Recruiter-Recruitment project menu
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rpe', `id` FROM `rbac_permission` WHERE `code` = 'rpe'; #not available to HR Recruiter-Recruitment project edit
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rps', `id` FROM `rbac_permission` WHERE `code` = 'rps';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'jpm', `id` FROM `rbac_permission` WHERE `code` = 'jpm';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'jpa', `id` FROM `rbac_permission` WHERE `code` = 'jpa'; #not available to HR Recruiter - Job position admin
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'jpc', `id` FROM `rbac_permission` WHERE `code` = 'jpc'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'jpe', `id` FROM `rbac_permission` WHERE `code` = 'jpe';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'jps', `id` FROM `rbac_permission` WHERE `code` = 'jps';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'tpm', `id` FROM `rbac_permission` WHERE `code` = 'tpm';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'tpc', `id` FROM `rbac_permission` WHERE `code` = 'tpc';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'tpe', `id` FROM `rbac_permission` WHERE `code` = 'tpe';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'tps', `id` FROM `rbac_permission` WHERE `code` = 'tps';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'crm', `id` FROM `rbac_permission` WHERE `code` = 'crm';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'crc', `id` FROM `rbac_permission` WHERE `code` = 'crc';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'cre', `id` FROM `rbac_permission` WHERE `code` = 'cre';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'crs', `id` FROM `rbac_permission` WHERE `code` = 'crs';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'fcr', `id` FROM `rbac_permission` WHERE `code` = 'fcr';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'fcrf', `id` FROM `rbac_permission` WHERE `code` = 'fcrf';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'crfs', `id` FROM `rbac_permission` WHERE `code` = 'crfs';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'cres', `id` FROM `rbac_permission` WHERE `code` = 'cres';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rdft', `id` FROM `rbac_permission` WHERE `code` = 'rdft';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rdht', `id` FROM `rbac_permission` WHERE `code` = 'rdht';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'stm', `id` FROM `rbac_permission` WHERE `code` = 'stm';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'etm', `id` FROM `rbac_permission` WHERE `code` = 'etm';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'etc', `id` FROM `rbac_permission` WHERE `code` = 'etc'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'ete', `id` FROM `rbac_permission` WHERE `code` = 'ete'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'ets', `id` FROM `rbac_permission` WHERE `code` = 'ets';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'iptsa', `id` FROM `rbac_permission` WHERE `code` = 'iptsa'; #not available to HR admin - interview plan template super admin - write access to system termplates for interview plan
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'ipta', `id` FROM `rbac_permission` WHERE `code` = 'ipta'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'iptm', `id` FROM `rbac_permission` WHERE `code` = 'iptm'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'iptc', `id` FROM `rbac_permission` WHERE `code` = 'iptc'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'ipte', `id` FROM `rbac_permission` WHERE `code` = 'ipte'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'ipts', `id` FROM `rbac_permission` WHERE `code` = 'ipts';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rptsa', `id` FROM `rbac_permission` WHERE `code` = 'rptsa'; #not available to HR admin - interview plan template super admin - write access to system termplates for interview plan
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rpta', `id` FROM `rbac_permission` WHERE `code` = 'rpta'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rptm', `id` FROM `rbac_permission` WHERE `code` = 'rptm'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rptc', `id` FROM `rbac_permission` WHERE `code` = 'rptc'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rpte', `id` FROM `rbac_permission` WHERE `code` = 'rpte'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'rpts', `id` FROM `rbac_permission` WHERE `code` = 'rpts';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'sqtsa', `id` FROM `rbac_permission` WHERE `code` = 'sqtsa'; #not available to HR admin - screening question template super admin - write access to system termplates for interview plan
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'sqta', `id` FROM `rbac_permission` WHERE `code` = 'sqta'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'sqtm', `id` FROM `rbac_permission` WHERE `code` = 'sqtm'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'sqtc', `id` FROM `rbac_permission` WHERE `code` = 'sqtc'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'sqte', `id` FROM `rbac_permission` WHERE `code` = 'sqte'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'sqts', `id` FROM `rbac_permission` WHERE `code` = 'sqts';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'ic', `id` FROM `rbac_permission` WHERE `code` = 'ic';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'is', `id` FROM `rbac_permission` WHERE `code` = 'is';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'ifc', `id` FROM `rbac_permission` WHERE `code` = 'ifc';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'ifs', `id` FROM `rbac_permission` WHERE `code` = 'ifs';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'mim', `id` FROM `rbac_permission` WHERE `code` = 'mim';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'wts', `id` FROM `rbac_permission` WHERE `code` = 'wts';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'wtc', `id` FROM `rbac_permission` WHERE `code` = 'wtc';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'wte', `id` FROM `rbac_permission` WHERE `code` = 'wte';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'wtfs', `id` FROM `rbac_permission` WHERE `code` = 'wtfs';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'wtfc', `id` FROM `rbac_permission` WHERE `code` = 'wtfc';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'wtfe', `id` FROM `rbac_permission` WHERE `code` = 'wtfe';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'wthrf', `id` FROM `rbac_permission` WHERE `code` = 'wthrf';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'aus', `id` FROM `rbac_permission` WHERE `code` = 'aus';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'uc', `id` FROM `rbac_permission` WHERE `code` = 'uc'; #not available to HR Recruiter - user create
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'ue', `id` FROM `rbac_permission` WHERE `code` = 'ue'; #not available to HR Recruiter - user edit
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'umm', `id` FROM `rbac_permission` WHERE `code` = 'umm'; #not available to HR Recruiter - user manage
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'tmm', `id` FROM `rbac_permission` WHERE `code` = 'tmm'; #not available to HR Recruiter - tag manage
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'ats', `id` FROM `rbac_permission` WHERE `code` = 'ats';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'te', `id` FROM `rbac_permission` WHERE `code` = 'te'; #not available to HR Recruiter - tag edit
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 1, 'td', `id` FROM `rbac_permission` WHERE `code` = 'td'; #not available to HR Recruiter - tag delete

--Client Admin
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'clna', `id` FROM `rbac_permission` WHERE `code` = 'clna'; #Only available to Client Admin and System Admin - Client admin
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'clnm', `id` FROM `rbac_permission` WHERE `code` = 'clnm'; #Only available to Client Admin and System Admin - Client admin
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'clnc', `id` FROM `rbac_permission` WHERE `code` = 'clnc'; #Only available to Client Admin and System Admin - Client create
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'clne', `id` FROM `rbac_permission` WHERE `code` = 'clne'; #Only available to Client Admin and System Admin - Client edit
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'clns', `id` FROM `rbac_permission` WHERE `code` = 'clns'; #Only available to Client Admin and System Admin - Client search
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 2, 'clncur', `id` FROM `rbac_permission` WHERE `code` = 'clncur'; #available to all roles except Candidate

-- HR Admin
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'clncur', `id` FROM `rbac_permission` WHERE `code` = 'clncur'; #available to all roles except Candidate
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'rpm', `id` FROM `rbac_permission` WHERE `code` = 'rpm'; #not available to HR Recruiter-Recruitment project menu
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'rpe', `id` FROM `rbac_permission` WHERE `code` = 'rpe'; #not available to HR Recruiter-Recruitment project edit
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'rps', `id` FROM `rbac_permission` WHERE `code` = 'rps';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'jpa', `id` FROM `rbac_permission` WHERE `code` = 'jpa'; #not available to HR Recruiter - Job position admin
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'jpm', `id` FROM `rbac_permission` WHERE `code` = 'jpm';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'jpc', `id` FROM `rbac_permission` WHERE `code` = 'jpc'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'jpe', `id` FROM `rbac_permission` WHERE `code` = 'jpe';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'jps', `id` FROM `rbac_permission` WHERE `code` = 'jps';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'tpm', `id` FROM `rbac_permission` WHERE `code` = 'tpm';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'tpc', `id` FROM `rbac_permission` WHERE `code` = 'tpc';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'tpe', `id` FROM `rbac_permission` WHERE `code` = 'tpe';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'tps', `id` FROM `rbac_permission` WHERE `code` = 'tps';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'crm', `id` FROM `rbac_permission` WHERE `code` = 'crm';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'crc', `id` FROM `rbac_permission` WHERE `code` = 'crc';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'cre', `id` FROM `rbac_permission` WHERE `code` = 'cre';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'crs', `id` FROM `rbac_permission` WHERE `code` = 'crs';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'fcr', `id` FROM `rbac_permission` WHERE `code` = 'fcr';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'fcrf', `id` FROM `rbac_permission` WHERE `code` = 'fcrf';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'crfs', `id` FROM `rbac_permission` WHERE `code` = 'crfs';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'cres', `id` FROM `rbac_permission` WHERE `code` = 'cres';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'rdft', `id` FROM `rbac_permission` WHERE `code` = 'rdft';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'rdht', `id` FROM `rbac_permission` WHERE `code` = 'rdht';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'stm', `id` FROM `rbac_permission` WHERE `code` = 'stm';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'etm', `id` FROM `rbac_permission` WHERE `code` = 'etm';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'etc', `id` FROM `rbac_permission` WHERE `code` = 'etc'; #not available to HR Recruiter - email template create
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'ete', `id` FROM `rbac_permission` WHERE `code` = 'ete'; #not available to HR Recruiter - email template edit
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'ets', `id` FROM `rbac_permission` WHERE `code` = 'ets';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'ipta', `id` FROM `rbac_permission` WHERE `code` = 'ipta'; #not available to HR Recruiter - interview plan template admin
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'iptm', `id` FROM `rbac_permission` WHERE `code` = 'iptm'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'iptc', `id` FROM `rbac_permission` WHERE `code` = 'iptc'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'ipte', `id` FROM `rbac_permission` WHERE `code` = 'ipte'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'ipts', `id` FROM `rbac_permission` WHERE `code` = 'ipts';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'rpta', `id` FROM `rbac_permission` WHERE `code` = 'rpta'; #not available to HR Recruiter - recruitment pipeline template admin
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'rptm', `id` FROM `rbac_permission` WHERE `code` = 'rptm'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'rptc', `id` FROM `rbac_permission` WHERE `code` = 'rptc'; #not available to HR Recruiter - recruitment pipeline template create
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'rpte', `id` FROM `rbac_permission` WHERE `code` = 'rpte'; #not available to HR Recruiter - recruitment pipeline template edit
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'rpts', `id` FROM `rbac_permission` WHERE `code` = 'rpts'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'sqta', `id` FROM `rbac_permission` WHERE `code` = 'sqta'; #not available to HR Recruiter - screening question template admin
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'sqtm', `id` FROM `rbac_permission` WHERE `code` = 'sqtm'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'sqtc', `id` FROM `rbac_permission` WHERE `code` = 'sqtc'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'sqte', `id` FROM `rbac_permission` WHERE `code` = 'sqte'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'sqts', `id` FROM `rbac_permission` WHERE `code` = 'sqts';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'ic', `id` FROM `rbac_permission` WHERE `code` = 'ic';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'is', `id` FROM `rbac_permission` WHERE `code` = 'is';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'ifc', `id` FROM `rbac_permission` WHERE `code` = 'ifc';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'ifs', `id` FROM `rbac_permission` WHERE `code` = 'ifs';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'mim', `id` FROM `rbac_permission` WHERE `code` = 'mim';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'wts', `id` FROM `rbac_permission` WHERE `code` = 'wts';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'wtc', `id` FROM `rbac_permission` WHERE `code` = 'wtc';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'wte', `id` FROM `rbac_permission` WHERE `code` = 'wte';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'wtfs', `id` FROM `rbac_permission` WHERE `code` = 'wtfs';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'wtfc', `id` FROM `rbac_permission` WHERE `code` = 'wtfc';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'wtfe', `id` FROM `rbac_permission` WHERE `code` = 'wtfe';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'wthrf', `id` FROM `rbac_permission` WHERE `code` = 'wthrf';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'aus', `id` FROM `rbac_permission` WHERE `code` = 'aus';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'uc', `id` FROM `rbac_permission` WHERE `code` = 'uc'; #not available to HR Recruiter - user create
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'ue', `id` FROM `rbac_permission` WHERE `code` = 'ue'; #not available to HR Recruiter - user edit
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'umm', `id` FROM `rbac_permission` WHERE `code` = 'umm'; #not available to HR Recruiter - user manage
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'tmm', `id` FROM `rbac_permission` WHERE `code` = 'tmm'; #not available to HR Recruiter - tag manage
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'ats', `id` FROM `rbac_permission` WHERE `code` = 'ats';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'te', `id` FROM `rbac_permission` WHERE `code` = 'te'; #not available to HR Recruiter - tag edit
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 101, 'td', `id` FROM `rbac_permission` WHERE `code` = 'td'; #not available to HR Recruiter - tag delete

```sql
-- HR Recruiters
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'clncur', `id` FROM `rbac_permission` WHERE `code` = 'clncur'; #available to all roles except Candidate
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'rps', `id` FROM `rbac_permission` WHERE `code` = 'rps';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'jpm', `id` FROM `rbac_permission` WHERE `code` = 'jpm';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'jpc', `id` FROM `rbac_permission` WHERE `code` = 'jpc'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'jpe', `id` FROM `rbac_permission` WHERE `code` = 'jpe';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'jps', `id` FROM `rbac_permission` WHERE `code` = 'jps';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'tpm', `id` FROM `rbac_permission` WHERE `code` = 'tpm';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'tpc', `id` FROM `rbac_permission` WHERE `code` = 'tpc';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'tpe', `id` FROM `rbac_permission` WHERE `code` = 'tpe';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'tps', `id` FROM `rbac_permission` WHERE `code` = 'tps';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'crm', `id` FROM `rbac_permission` WHERE `code` = 'crm';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'crc', `id` FROM `rbac_permission` WHERE `code` = 'crc';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'cre', `id` FROM `rbac_permission` WHERE `code` = 'cre';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'crs', `id` FROM `rbac_permission` WHERE `code` = 'crs';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'fcr', `id` FROM `rbac_permission` WHERE `code` = 'fcr';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'fcrf', `id` FROM `rbac_permission` WHERE `code` = 'fcrf';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'crfs', `id` FROM `rbac_permission` WHERE `code` = 'crfs';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'cres', `id` FROM `rbac_permission` WHERE `code` = 'cres';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'rdft', `id` FROM `rbac_permission` WHERE `code` = 'rdft';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'rdht', `id` FROM `rbac_permission` WHERE `code` = 'rdht';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'stm', `id` FROM `rbac_permission` WHERE `code` = 'stm';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'etm', `id` FROM `rbac_permission` WHERE `code` = 'etm';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'ets', `id` FROM `rbac_permission` WHERE `code` = 'ets';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'iptm', `id` FROM `rbac_permission` WHERE `code` = 'iptm'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'iptc', `id` FROM `rbac_permission` WHERE `code` = 'iptc'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'ipte', `id` FROM `rbac_permission` WHERE `code` = 'ipte'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'ipts', `id` FROM `rbac_permission` WHERE `code` = 'ipts';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'rptm', `id` FROM `rbac_permission` WHERE `code` = 'rptm';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'rpts', `id` FROM `rbac_permission` WHERE `code` = 'rpts';  
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'sqtm', `id` FROM `rbac_permission` WHERE `code` = 'sqtm'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'sqtc', `id` FROM `rbac_permission` WHERE `code` = 'sqtc'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'sqte', `id` FROM `rbac_permission` WHERE `code` = 'sqte'; 
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'sqts', `id` FROM `rbac_permission` WHERE `code` = 'sqts';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'ic', `id` FROM `rbac_permission` WHERE `code` = 'ic';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'is', `id` FROM `rbac_permission` WHERE `code` = 'is';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'ifc', `id` FROM `rbac_permission` WHERE `code` = 'ifc';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'ifs', `id` FROM `rbac_permission` WHERE `code` = 'ifs';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'mim', `id` FROM `rbac_permission` WHERE `code` = 'mim';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'wts', `id` FROM `rbac_permission` WHERE `code` = 'wts';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'wtc', `id` FROM `rbac_permission` WHERE `code` = 'wtc';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'wte', `id` FROM `rbac_permission` WHERE `code` = 'wte';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'wtfs', `id` FROM `rbac_permission` WHERE `code` = 'wtfs';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'wtfc', `id` FROM `rbac_permission` WHERE `code` = 'wtfc';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'wtfe', `id` FROM `rbac_permission` WHERE `code` = 'wtfe';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'wthrf', `id` FROM `rbac_permission` WHERE `code` = 'wthrf';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'aus', `id` FROM `rbac_permission` WHERE `code` = 'aus';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 102, 'ats', `id` FROM `rbac_permission` WHERE `code` = 'ats';

-- Hiring Managers
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 103, 'clncur', `id` FROM `rbac_permission` WHERE `code` = 'clncur'; #available to all roles except Candidate
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 103, 'jpm', `id` FROM `rbac_permission` WHERE `code` = 'jpm';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 103, 'jps', `id` FROM `rbac_permission` WHERE `code` = 'jps';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 103, 'crs', `id` FROM `rbac_permission` WHERE `code` = 'crs'; #candidate resume search
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 103, 'fcrf', `id` FROM `rbac_permission` WHERE `code` = 'fcrf'; #forward candidate resume
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 103, 'crfs', `id` FROM `rbac_permission` WHERE `code` = 'crfs'; #candidate resume forward search
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 103, 'rsm', `id` FROM `rbac_permission` WHERE `code` = 'rsm'; #resume search menu
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 103, 'is', `id` FROM `rbac_permission` WHERE `code` = 'is'; #interview search
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 103, 'ifc', `id` FROM `rbac_permission` WHERE `code` = 'ifc'; #interview feedback create
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 103, 'ifs', `id` FROM `rbac_permission` WHERE `code` = 'ifs'; #interview feedback search
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 103, 'mim', `id` FROM `rbac_permission` WHERE `code` = 'mim'; #my interview menu
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 103, 'im', `id` FROM `rbac_permission` WHERE `code` = 'im'; #interview menu
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 103, 'wts', `id` FROM `rbac_permission` WHERE `code` = 'wts'; #written test forward search
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 103, 'wtfs', `id` FROM `rbac_permission` WHERE `code` = 'wtfs'; #written test forward search
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 103, 'ats', `id` FROM `rbac_permission` WHERE `code` = 'ats';

-- Interviewers
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 104, 'clncur', `id` FROM `rbac_permission` WHERE `code` = 'clncur'; #available to all roles except Candidate
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 104, 'crs', `id` FROM `rbac_permission` WHERE `code` = 'crs';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 104, 'fcrf', `id` FROM `rbac_permission` WHERE `code` = 'fcrf';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 104, 'crfs', `id` FROM `rbac_permission` WHERE `code` = 'crfs';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 104, 'rsm', `id` FROM `rbac_permission` WHERE `code` = 'rsm';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 104, 'is', `id` FROM `rbac_permission` WHERE `code` = 'is';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 104, 'ifc', `id` FROM `rbac_permission` WHERE `code` = 'ifc';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 104, 'ifs', `id` FROM `rbac_permission` WHERE `code` = 'ifs';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 104, 'mim', `id` FROM `rbac_permission` WHERE `code` = 'mim';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 104, 'im', `id` FROM `rbac_permission` WHERE `code` = 'im';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 104, 'wts', `id` FROM `rbac_permission` WHERE `code` = 'wts';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 104, 'wtfs', `id` FROM `rbac_permission` WHERE `code` = 'wtfs';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 104, 'wtfe', `id` FROM `rbac_permission` WHERE `code` = 'wtfe';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 104, 'wtrm', `id` FROM `rbac_permission` WHERE `code` = 'wtrm';
INSERT INTO `rbac_role_permission` (`role_id`, `permission_code`, `permission_id`) SELECT 104, 'ats', `id` FROM `rbac_permission` WHERE `code` = 'ats';

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

-- Screening Question Types
INSERT INTO ats.screening_question_type (`description`,`name`,`code`) VALUES (NULL,'Inputbox','INPUT');
INSERT INTO ats.screening_question_type (`description`,`name`,`code`) VALUES (NULL,'Input Area','INPUTAREA');
INSERT INTO ats.screening_question_type (`description`,`name`,`code`) VALUES (NULL,'Checkbox','CHK');
INSERT INTO ats.screening_question_type (`description`,`name`,`code`) VALUES ('Multiple choice','Radio buttons','RDO');
INSERT INTO ats.screening_question_type (`description`,`name`,`code`) VALUES ('Single or multiple selection','Dropdown','DD');
INSERT INTO ats.screening_question_type (`description`,`name`,`code`) VALUES (NULL,'Heading','HN');
INSERT INTO ats.screening_question_type (`description`,`name`,`code`) VALUES (NULL,'Label','LBL');

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
INSERT INTO ats.rbac_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (1,'V1StGtR8_vbu','no-reply@fsworkpaper.ai',NULL,'$2a$10$OVNWgRHLRvM6zDn6PDkjv.xzyh1n.zJvhun75xTZ554SgkIFDAxaW',0,0,'2026-10-18 19:22:51.634567',NULL,NULL,'2026-10-18 19:22:51.450781','2026-10-18 19:22:51.450781',0,0,'System',0,NULL,0,NULL,NULL,NULL,NULL,'$2a$10$nuwwU.V9vhBp0LfCxqyeHu',NULL,NULL,1);
# Tenant Users
INSERT INTO ats.rbac_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (11,'V1StGXR8_Z5j','antoine@fsworkpaper.ai',NULL,NULL,0,0,'2026-10-18 15:36:35.000000',NULL,NULL,NULL,NULL,0,0,'Antoine',0,'Boquen',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO ats.rbac_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (12,'TnStGXR8_Z5k','ats_admin@fsworkpaper.ai',NULL,NULL,0,0,'2026-10-18 15:36:35.000000',NULL,NULL,NULL,NULL,0,0,'Admin',0,'User',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO ats.rbac_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (13,'k5StGXR8_uqP','nadia@fsworkpaper.ai',NULL,NULL,0,0,'2026-10-18 15:36:35.000000',NULL,NULL,NULL,NULL,0,0,'Nadia',0,'P.',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO ats.rbac_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (14,'V2InXR8_Z5n','nan.li@fsworkpaper.ai',NULL,NULL,0,0,'2026-10-18 15:36:35.000000',NULL,NULL,NULL,NULL,0,0,'Nan',0,'Li',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO ats.rbac_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (15,'V1StGXR8_Z5o','ats_rec2@fsworkpaper.ai',NULL,NULL,0,0,'2026-10-18 15:36:35.000000',NULL,NULL,NULL,NULL,0,0,'Rec 2',0,'User',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO ats.rbac_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (16,'V1StGXR8_Z5t','hrd2@fsworkpaper.ai',NULL,NULL,0,0,'2026-10-18 15:36:35.000000',NULL,NULL,NULL,NULL,0,0,'HRD 2',0,'User',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO ats.rbac_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (17,'V1StGXR8_Z5u','ats_rec4@fsworkpaper.ai',NULL,NULL,0,0,'2026-10-18 15:36:35.000000',NULL,NULL,NULL,NULL,0,0,'Rec 4',0,'User',0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO ats.rbac_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (18,'V1StGXR8_vbu','damien@fsworkpaper.ai',NULL,'$2a$10$OVNWgRHLRvM6zDn6PDkjv.xzyh1n.zJvhun75xTZ554SgkIFDAxaW',0,0,'2026-10-18 15:36:35.000000',NULL,NULL,'2026-10-18 19:25:00.450781','2026-10-18 19:22:51.450781',0,0,'Damien',0,'Polegato',0,NULL,NULL,NULL,NULL,'$2a$10$nuwwU.V9vhBp0LfCxqyeHu',NULL,NULL,1);
INSERT INTO ats.rbac_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (19,'V1StGtR8_vbu','juan@fsworkpaper.ai',NULL,'$2a$10$OVNWgRHLRvM6zDn6PDkjv.xzyh1n.zJvhun75xTZ554SgkIFDAxaW',0,0,'2026-10-18 15:36:35.000000',NULL,NULL,'2026-10-18 19:25:00.450781','2026-10-18 19:22:51.450781',0,0,'Juan',0,'Pablo',0,NULL,NULL,NULL,NULL,'$2a$10$nuwwU.V9vhBp0LfCxqyeHu',NULL,NULL,1);
INSERT INTO ats.rbac_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (20,'OzqnguSD0CPf','zam420@live.com',NULL,'$2a$10$OVNWgRHLRvM6zDn6PDkjv.xzyh1n.zJvhun75xTZ554SgkIFDAxaW',0,0,'2026-10-18 19:22:51.634567','wGdHynhQ','2026-10-18 19:22:51.450781','2026-10-18 19:25:00.450781','2026-10-18 19:22:51.450781',0,0,'Zam',0,'Chaudhry',0,NULL,NULL,NULL,NULL,'$2a$10$nuwwU.V9vhBp0LfCxqyeHu',NULL,NULL,1);
# Candidate Users
INSERT INTO ats.rbac_user (`id`,`public_id`,`email`,`phone`,`password`,`type`,`status`,`join_date`,`activation_code`,`activation_code_sent_date_time`,`activation_date_time`,`creation_date_time`,`credentials_expired`,`deactivated`,`first_name`,`is_google_login`,`last_name`,`locked`,`locked_by_user_id`,`locked_by_user_name`,`locked_date_time`,`modified_date_time`,`salt`,`password_reset_code`,`password_reset_code_sent_date_time`,`tenant_id`,`company_id`) VALUES (1000,'OzqngmSD0CPf','candidate@fsworkpaper.ai',NULL,'$2a$10$OVNWgRHLRvM6zDn6PDkjv.xzyh1n.zJvhun75xTZ554SgkIFDAxaW',1,0,'2026-10-18 19:22:51.634567','wGdHynhQ','2026-10-18 19:22:51.450781','2026-10-18 19:25:00.450781','2026-10-18 19:22:51.450781',0,0,'Poor',0,'Candidate',0,NULL,NULL,NULL,NULL,'$2a$10$nuwwU.V9vhBp0LfCxqyeHu',NULL,NULL,2);

# User Roles
INSERT INTO ats.rbac_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5a','sa',1,2,'TnStGXR8_Z5k');
INSERT INTO ats.rbac_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5b','itv',104,2,'TnStGXR8_Z5k');
INSERT INTO ats.rbac_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5c','rec',102,3,'k5StGXR8_uqP');
INSERT INTO ats.rbac_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5d','itv',104,3,'k5StGXR8_uqP');
INSERT INTO ats.rbac_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5h','rec',102,4,'V2InXR8_Z5n');
INSERT INTO ats.rbac_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5i','itv',104,4,'V2InXR8_Z5n');
INSERT INTO ats.rbac_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5j','rec',102,5,'V1StGXR8_Z5o');
INSERT INTO ats.rbac_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5k','itv',104,5,'V1StGXR8_Z5o');
INSERT INTO ats.rbac_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5q','hrd',101,6,'V1StGXR8_Z5t');
INSERT INTO ats.rbac_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5r','rec',102,6,'V1StGXR8_Z5t');
INSERT INTO ats.rbac_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5s','itv',104,6,'V1StGXR8_Z5t');
INSERT INTO ats.rbac_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5t','hm',103,8,'V1StGXR8_vbu');
INSERT INTO ats.rbac_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('TnStGXR8_Z5u','itv',104,7,'V1StGXR8_Z5u');
INSERT INTO ats.rbac_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('IrOQeOfgK7VF','hra',101,34,'OzqnguSD0CPf');
INSERT INTO ats.rbac_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('IrOQeOfgK89F','itv',104,8,'V1StGXR8_vbu');
INSERT INTO ats.rbac_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('IrOQeOfmn89F','itv',104,9,'V1StGtR8_vbu');
INSERT INTO ats.rbac_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('IbnveOfmn89F','cdt',105,10,'OzqngmSD0CPf');
INSERT INTO ats.rbac_user_role (`public_id`,`role_code`,`role_id`,`user_id`,`user_public_id`) VALUES ('IrOedOfmn89F','hm',103,9,'V1StGtR8_vbu');



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

