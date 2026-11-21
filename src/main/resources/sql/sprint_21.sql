-- Apply after sprint_20.sql. Older rejections have no recorded rejection time.
-- If Hibernate already created any columns, omit those ADD COLUMN clauses.
-- The backfilled stage reflects its current name, which may have changed.
ALTER TABLE job_application
    ADD COLUMN rejected_at DATETIME(6) NULL,
    ADD COLUMN rejected_stage_name VARCHAR(30) NULL,
    ADD COLUMN rejected_stage_type VARCHAR(30) NULL;

UPDATE job_application a
JOIN job_recruitment_pipeline_round r ON r.id = a.recruitment_pipeline_stage
SET a.rejected_stage_name = r.name, a.rejected_stage_type = r.round_type
WHERE a.rejected = TRUE;
