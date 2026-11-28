-- Apply before deploying interviewer evaluation feedback.
CREATE TABLE candidate_evaluation_feedback (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    public_id VARCHAR(12) NOT NULL UNIQUE,
    candidate_evaluation_round_id BIGINT NOT NULL,
    interviewer_id VARCHAR(12) NOT NULL,
    result VARCHAR(30) NULL,
    overall_feedback VARCHAR(1000) NULL,
    strengths VARCHAR(1000) NULL,
    areas_for_improvement VARCHAR(1000) NULL,
    submitted BOOLEAN NOT NULL DEFAULT FALSE,
    submitted_at DATETIME(6) NULL,
    UNIQUE KEY uk_candidate_evaluation_feedback (candidate_evaluation_round_id, interviewer_id),
    FOREIGN KEY (candidate_evaluation_round_id) REFERENCES candidate_evaluation_round(id)
);
