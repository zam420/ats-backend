-- Close the interviews which job dose not match to what the candidate applied
UPDATE ats_interview_record itv
    INNER JOIN (
    SELECT
    air.interview_id,
    t0.candidate_resume_id,
    t0.applying_position_id
    FROM
    ats_interview_rounds air
    INNER JOIN (
    SELECT
    aic.id,
    aic.round_id,
    aic.candidate_resume_id,
    acr.applying_position_id
    FROM
    ats_interview_candidate aic
    INNER JOIN ats_candidate_resume acr ON aic.candidate_resume_id = acr.id
    ) t0 ON t0.round_id = air.id
    ) t1 ON itv.id = t1.interview_id
    SET itv.interview_status = 1
WHERE
    itv.target_job_id != t1.applying_position_id;

-- Close redundancy interviews
WITH latest_updates AS(
    SELECT t1.candidate_resume_id,MAX(itv.updated_at) AS max_update_time
    FROM ats_interview_record itv
             INNER JOIN (
        SELECT
            air.interview_id,
            t0.candidate_resume_id,
            t0.applying_position_id
        FROM ats_interview_rounds air
                 INNER JOIN (
            SELECT
                aic.id,
                aic.round_id,
                aic.candidate_resume_id,
                acr.applying_position_id
            FROM ats_interview_candidate aic
                     INNER JOIN ats_candidate_resume acr ON aic.candidate_resume_id = acr.id
        ) t0 GROUP BY air.interview_id, t0.candidate_resume_id
    ) t1 ON itv.id = t1.interview_id
    WHERE itv.target_job_id = t1.applying_position_id and itv.interview_status = 0
    GROUP BY t1.candidate_resume_id
)
UPDATE ats_interview_record airc
SET airc.interview_status = 1
WHERE airc.updated_at NOT IN(
    SELECT max_update_time FROM latest_updates
);
