ALTER TABLE `ats_job_position` MODIFY COLUMN `title` varchar(100) NOT NULL;

insert into ats_resume_comments (candidate_resume_id, commentator_id, share_to, `comment`, comment_at)
select candidate_resume_id, pooled_by_id, '[-1]',
case pooled_reason
when 0 then
    (case when isnull(comments) then 'CV Fail.' else concat('CV Fail. ',comments) end)
when 1 then
    (case when isnull(comments) then 'Cold Call Fail.' else concat('Cold Call Fail. ',comments) end)
when 2 then
    (case when isnull(comments) then 'Test Fail.' else concat('Test Fail. ',comments) end)
when 3 then
    (case when isnull(comments) then 'Interview Fail.' else concat('Interview Fail. ',comments) end)
when 4 then
    (case when isnull(comments) then 'Reject Offer.' else concat('Reject Offer. ',comments) end)
when 5 then
    (case when isnull(comments) then 'Cancel Offer.' else concat('Cancel Offer. ',comments) end)
when 6 then
    (case when isnull(comments) then 'Future Contact.' else concat('Future Contact. ',comments) end)
when 7 then
    (case when isnull(comments) then 'No Suitable Position.' else concat('No Suitable Position. ',comments) end)
when 8 then
    (case when isnull(comments) then 'Cancel Onboarding.' else concat('Cancel Onboarding. ',comments) end)
when 9 then
    (case when isnull(comments) then 'Black List.' else concat('Black List. ',comments) end)
when 10 then
    (case when isnull(comments) then 'Archived Resumes.' else concat('Archived Resumes. ',comments) end)
when 11 then
    (case when isnull(comments) then 'Candidate Withdrawn.' else concat('Candidate Withdrawn. ',comments) end)
end as `comment`,
pooled_at
from ats_talent_pool;


CREATE TABLE `ats_table_button_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `state` varchar(100) NOT NULL,
  `table` varchar(100) NOT NULL,
  `button` varchar(100) NOT NULL,
  `sort` int NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_table_button_config_uniq` (`user_id`,`table`,`state`,`button`),
  CONSTRAINT `ats_table_button_config_user_id_fk_ats_user_id` FOREIGN KEY (`user_id`) REFERENCES `ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


ALTER TABLE ats_exam ADD COLUMN web_report_url VARCHAR(4096);

## 补充 2025-02-25
INSERT INTO `ats`.`ats_task_record` (`task_id`, `record`, `sub_id`) VALUES ('email_resume_parser', '2025-02-24 00:00:00', 'recruit.jp@espressif.com');
