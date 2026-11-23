-- Apply after sprint_21.sql and before deploying code that uses the renamed columns.
ALTER TABLE job_application
    CHANGE COLUMN rejected closed BOOLEAN NOT NULL DEFAULT FALSE,
    CHANGE COLUMN rejection_reason closed_reason VARCHAR(2000) NULL,
    CHANGE COLUMN rejected_at closed_at DATETIME(6) NULL,
    CHANGE COLUMN rejected_stage_name closed_at_stage_name VARCHAR(30) NULL,
    CHANGE COLUMN rejected_stage_type closed_at_stage_type VARCHAR(30) NULL,
    ADD COLUMN application_close_type VARCHAR(30) NULL;

UPDATE job_application SET application_close_type = 'REJECTED' WHERE closed = TRUE;
