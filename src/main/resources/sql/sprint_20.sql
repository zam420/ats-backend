-- Apply after sprint_19.sql. Existing applications remain active in their current stage.
ALTER TABLE job_application
    MODIFY recruitment_pipeline_stage BIGINT NULL,
    ADD COLUMN rejected BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN rejection_reason VARCHAR(2000) NULL,
    ADD COLUMN consider_for_other_openings BOOLEAN NULL;
