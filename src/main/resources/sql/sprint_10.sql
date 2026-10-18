alter table ats_job_position modify job_desc varchar(2000) not null;
alter table ats_interview_record modify interview_template_id bigint null;

alter table ats_email_counter alter column mail_count set default 0;
alter table ats_candidate modify create_by_id bigint null;
alter table ats_candidate_resume modify created_at datetime(6) null;
alter table ats_candidate_resume modify updated_at datetime(6) null;
alter table ats_candidate_resume modify create_by_id bigint null;
alter table ats_interview_rounds modify email_template_id bigint null;
