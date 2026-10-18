CREATE TABLE `ats_resume_view_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  candidate_resume_id bigint NOT NULL,
  view_by bigint NOT NULL,
  view_at datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ats_resume_view_record_uniq` (`candidate_resume_id`,`view_by`),
  CONSTRAINT `ats_resume_view_record_fk_candidate_resume` FOREIGN KEY (`candidate_resume_id`) REFERENCES `ats_candidate_resume` (`id`),
  CONSTRAINT `ats_resume_view_record_fk_view_by` FOREIGN KEY (`view_by`) REFERENCES `ats_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

