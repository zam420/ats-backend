alter table ats_candidate modify column `name` varchar(500) NULL;
alter table ats_candidate_resume modify column `name` varchar(500) NULL;
alter table `ats_candidate_project_experience` modify column `project_name` varchar(500) DEFAULT NULL;
