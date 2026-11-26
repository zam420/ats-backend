-- Apply after sprint_23.sql and before deploying evaluation-plan versioning.
-- Older archived plans cannot reliably be reconstructed into versions.
ALTER TABLE job_evaluation_plan_round
    ADD COLUMN plan_version INT NULL,
    ADD INDEX idx_job_evaluation_plan_version (job_position, plan_version);

UPDATE job_evaluation_plan_round SET plan_version = 1 WHERE active = TRUE;

ALTER TABLE job_application ADD COLUMN evaluation_plan_version INT NULL;

-- Uninitialized applications use the current plan as their baseline. Already
-- initialized workflows retain their candidate rounds and an unknown plan version.
UPDATE job_application a
SET a.evaluation_plan_version = 1
WHERE a.evaluations_initialized = FALSE AND EXISTS (
    SELECT 1 FROM job_evaluation_plan_round r
    WHERE r.job_position = a.job_position_id AND r.plan_version = 1
);
