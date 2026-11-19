-- Back up the table and stop application writes before running this migration.
-- Legacy UNDER_REVIEW and ON_HOLD statuses must be resolved explicitly first.
DELIMITER //
CREATE PROCEDURE migrate_job_position_enum_ids()
BEGIN
    IF EXISTS (
        SELECT 1 FROM job_position
        WHERE status IS NULL OR BINARY status NOT IN ('DRAFT', 'OPEN', 'CLOSED')
           OR application_questions_display_mode IS NULL
           OR BINARY application_questions_display_mode NOT IN ('WITH_DESCRIPTION', 'SEPARATE_TABS')
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Unrecognized job status or display mode; resolve values before migration';
    END IF;

    ALTER TABLE job_position
        ADD COLUMN status_id_migration INT NULL,
        ADD COLUMN display_mode_id_migration INT NULL;

    UPDATE job_position
    SET status_id_migration = CASE BINARY status
            WHEN 'DRAFT' THEN 0 WHEN 'OPEN' THEN 1 WHEN 'CLOSED' THEN 2 END,
        display_mode_id_migration = CASE BINARY application_questions_display_mode
            WHEN 'WITH_DESCRIPTION' THEN 0 WHEN 'SEPARATE_TABS' THEN 1 END;

    ALTER TABLE job_position
        MODIFY COLUMN status_id_migration INT NOT NULL,
        MODIFY COLUMN display_mode_id_migration INT NOT NULL;
    ALTER TABLE job_position
        DROP COLUMN status,
        DROP COLUMN application_questions_display_mode,
        CHANGE COLUMN status_id_migration status INT NOT NULL,
        CHANGE COLUMN display_mode_id_migration application_questions_display_mode INT NOT NULL;
    CREATE INDEX idx_job_position_status ON job_position (status);
END//
DELIMITER ;
CALL migrate_job_position_enum_ids();
DROP PROCEDURE migrate_job_position_enum_ids;
