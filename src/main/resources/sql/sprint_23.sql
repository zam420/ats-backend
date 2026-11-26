-- Apply after sprint_22.sql, before deploying the evaluation-round archival changes.
-- Existing rounds remain active.
ALTER TABLE job_evaluation_plan_round
    ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN lineage_id VARCHAR(12) NULL;

UPDATE job_evaluation_plan_round SET lineage_id = public_id WHERE lineage_id IS NULL;

ALTER TABLE job_evaluation_plan_round MODIFY lineage_id VARCHAR(12) NOT NULL;

ALTER TABLE job_application
    ADD COLUMN evaluations_initialized BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN evaluation_decision VARCHAR(30) NULL;

CREATE TABLE candidate_evaluation_round (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    public_id VARCHAR(12) NOT NULL UNIQUE,
    job_application_id BIGINT NOT NULL,
    plan_round_id BIGINT NULL,
    plan_lineage_id VARCHAR(12) NULL,
    round_order INT NOT NULL,
    name VARCHAR(30) NOT NULL,
    description VARCHAR(100) NULL,
    round_type VARCHAR(30) NOT NULL,
    interviewers JSON NULL,
    status VARCHAR(30) NOT NULL,
    result VARCHAR(30) NULL,
    scheduled_at DATETIME(6) NULL,
    UNIQUE KEY uk_candidate_evaluation_lineage (job_application_id, plan_lineage_id),
    INDEX idx_candidate_evaluation_order (job_application_id, round_order),
    FOREIGN KEY (job_application_id) REFERENCES job_application(id),
    FOREIGN KEY (plan_round_id) REFERENCES job_evaluation_plan_round(id)
);
