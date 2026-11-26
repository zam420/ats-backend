-- Apply after sprint_26.sql and before deploying the new evaluation result enum.
-- Previous binary results did not record strength; the selected migration maps them to strong ratings.
UPDATE candidate_evaluation_round SET result = 'STRONG_YES' WHERE result = 'PASSED';
UPDATE candidate_evaluation_round SET result = 'STRONG_NO' WHERE result = 'FAILED';
