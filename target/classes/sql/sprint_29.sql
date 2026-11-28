-- Apply after sprint_28.sql. Historical status-change times and durations are unknown.
ALTER TABLE candidate_evaluation_round
    ADD COLUMN duration_minutes INT NULL,
    ADD COLUMN status_changed_at DATETIME(6) NULL;
