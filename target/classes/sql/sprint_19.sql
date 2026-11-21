-- Before running this migration, ensure every job with applications has exactly
-- one APPLIED stage and that it is the first stage in that job's pipeline.
-- Run this query separately and resolve every returned row before continuing.
SELECT a.job_position_id, COUNT(DISTINCT r.id) AS applied_stage_count
FROM job_application a
LEFT JOIN job_recruitment_pipeline_round r
    ON r.job_position = a.job_position_id AND r.round_type = 'APPLIED'
GROUP BY a.job_position_id
HAVING COUNT(DISTINCT r.id) <> 1 OR MIN(r.`order`) <>
    (SELECT MIN(first_round.`order`) FROM job_recruitment_pipeline_round first_round
     WHERE first_round.job_position = a.job_position_id);

ALTER TABLE job_application ADD COLUMN recruitment_pipeline_stage BIGINT NULL;

UPDATE job_application a
JOIN (
    SELECT r.job_position, MIN(r.id) AS stage_id
    FROM job_recruitment_pipeline_round r
    WHERE r.round_type = 'APPLIED'
    GROUP BY r.job_position
    HAVING COUNT(*) = 1 AND MIN(r.`order`) =
        (SELECT MIN(first_round.`order`) FROM job_recruitment_pipeline_round first_round
         WHERE first_round.job_position = r.job_position)
) applied ON applied.job_position = a.job_position_id
SET a.recruitment_pipeline_stage = applied.stage_id;

-- This fails if any applications could not be assigned; resolve those jobs first.
ALTER TABLE job_application MODIFY recruitment_pipeline_stage BIGINT NOT NULL;
ALTER TABLE job_application
    ADD CONSTRAINT fk_job_application_pipeline_stage
    FOREIGN KEY (recruitment_pipeline_stage) REFERENCES job_recruitment_pipeline_round(id);
