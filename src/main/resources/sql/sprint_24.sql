-- Apply after sprint_18.sql and before deploying question-set versioning.
-- Archived questions predate versioning and their original sets cannot be reconstructed.
ALTER TABLE job_screening_question
    ADD COLUMN question_set_version INT NULL,
    ADD INDEX idx_job_question_set_version (job_position, question_set_version);

UPDATE job_screening_question SET question_set_version = 1 WHERE active = TRUE;
