-- Apply after sprint_25.sql and before deploying the removal of evaluation lineage.
ALTER TABLE candidate_evaluation_round
    ADD UNIQUE KEY uk_candidate_evaluation_plan_round (job_application_id, plan_round_id);

ALTER TABLE candidate_evaluation_round
    DROP INDEX uk_candidate_evaluation_lineage,
    DROP COLUMN plan_lineage_id;

ALTER TABLE job_evaluation_plan_round DROP COLUMN lineage_id;
