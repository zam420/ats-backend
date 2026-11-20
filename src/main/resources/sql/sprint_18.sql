ALTER TABLE job_screening_question ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;

CREATE TABLE job_application (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    public_id VARCHAR(12) NOT NULL UNIQUE,
    job_position_id BIGINT NOT NULL,
    candidate_user_id BIGINT NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    preferred_name VARCHAR(100),
    email VARCHAR(254) NOT NULL,
    country_code VARCHAR(10) NOT NULL,
    phone_number VARCHAR(40) NOT NULL,
    current_location VARCHAR(200) NOT NULL,
    resume_path VARCHAR(255) NOT NULL,
    resume_name VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    UNIQUE KEY uk_job_application_email (job_position_id, email),
    FOREIGN KEY (job_position_id) REFERENCES job_position(id)
);

CREATE TABLE job_application_screening_question_answers (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    public_id VARCHAR(12) NOT NULL UNIQUE,
    job_application_id BIGINT NOT NULL,
    job_screening_question_id BIGINT NOT NULL,
    answer JSON NOT NULL,
    UNIQUE KEY uk_application_question (job_application_id, job_screening_question_id),
    FOREIGN KEY (job_application_id) REFERENCES job_application(id),
    FOREIGN KEY (job_screening_question_id) REFERENCES job_screening_question(id)
);
