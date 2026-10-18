use ats;

# 简历查重 step 2
# 符合单条匹配的简历
# data_status = 1, 即为提交状态
# 不包含完全匹配的候选人
#-------------------------------------------------------------------------------------------------------------
DROP PROCEDURE if exists `querySimilarResumeIds`;
DELIMITER $$
CREATE PROCEDURE `querySimilarResumeIds`(
    IN candidateName text,
    IN highestSchool text,
    IN workExpArray json,
    IN notInCandidateIds json
)
BEGIN
    IF (highestSchool IS NOT NULL AND CHAR_LENGTH(highestSchool) > 0) AND JSON_LENGTH(workExpArray) > 0 THEN
        # highestSchool is not empty and workExpArray is not empty
        SELECT acr.id
        FROM ats.ats_candidate_resume acr
        WHERE acr.`data_status` = 1
          AND acr.candidate_id NOT in (SELECT c_id
                                       from json_table(notInCandidateIds, '$[*]'
                                                       COLUMNS ( c_id INT PATH '$' ERROR ON ERROR)) as c_id_t)
          AND (acr.name LIKE candidateName
            OR acr.id IN (SELECT aceh.candidate_resume_id
                          FROM ats.ats_candidate_education_history aceh
                          WHERE aceh.university = highestSchool
                            AND aceh.candidate_id NOT IN (SELECT c1_id
                                                          from json_table(notInCandidateIds, '$[*]'
                                                                          COLUMNS ( c1_id INT PATH '$' ERROR ON ERROR)) as c_id_t1))
            OR acr.id IN (SELECT acwe.candidate_resume_id
                          FROM ats.ats_candidate_work_experience acwe
                          WHERE acwe.company_name in (SELECT com_name
                                                      from json_table(workExpArray, '$[*]'
                                                                      COLUMNS ( com_name TEXT PATH '$.company_name')) as w_com_t)
                            AND acwe.candidate_id NOT IN (SELECT c2_id
                                                          from json_table(notInCandidateIds, '$[*]'
                                                                          COLUMNS ( c2_id INT PATH '$' ERROR ON ERROR)) as c_id_t2))
            );
    END IF;
    IF (highestSchool IS NULL or CHAR_LENGTH(highestSchool) = 0) AND JSON_LENGTH(workExpArray) = 0 THEN
        # highestSchool is empty and workExpArray is empty
        SELECT acr.id
        FROM ats.ats_candidate_resume acr
        WHERE acr.`data_status` = 1
          AND acr.candidate_id NOT in (SELECT c_id
                                       from json_table(notInCandidateIds, '$[*]'
                                                       COLUMNS ( c_id INT PATH '$' ERROR ON ERROR)) as c_id_t)
          AND (acr.name LIKE candidateName);
    END IF;
    IF (highestSchool IS NOT NULL AND CHAR_LENGTH(highestSchool) > 0) AND JSON_LENGTH(workExpArray) = 0 THEN
        # highestSchool is not empty and workExpArray is empty
        SELECT acr.id
        FROM ats.ats_candidate_resume acr
        WHERE acr.`data_status` = 1
          AND acr.candidate_id NOT in (SELECT c_id
                                       from json_table(notInCandidateIds, '$[*]'
                                                       COLUMNS ( c_id INT PATH '$' ERROR ON ERROR)) as c_id_t)
          AND (acr.name LIKE candidateName
            OR acr.id IN (SELECT aceh.candidate_resume_id
                          FROM ats.ats_candidate_education_history aceh
                          WHERE aceh.university = highestSchool
                            AND aceh.candidate_id NOT IN (SELECT c_id
                                                          from json_table(notInCandidateIds, '$[*]'
                                                                          COLUMNS ( c_id INT PATH '$' ERROR ON ERROR)) as c_id_t1))
            );
    END IF;
    IF (highestSchool IS NULL OR CHAR_LENGTH(highestSchool) = 0) AND JSON_LENGTH(workExpArray) > 0 THEN
        # highestSchool is empty and workExpArray is not empty
        SELECT acr.id
        FROM ats.ats_candidate_resume acr
        WHERE acr.`data_status` = 1
          AND acr.candidate_id NOT in (SELECT c_id
                                       from json_table(notInCandidateIds, '$[*]'
                                                       COLUMNS ( c_id INT PATH '$' ERROR ON ERROR)) as c_id_t)
          AND (acr.name LIKE candidateName
            OR acr.id IN (SELECT acwe.candidate_resume_id
                          FROM ats.ats_candidate_work_experience acwe
                          WHERE acwe.company_name in (SELECT com_name
                                                      from json_table(workExpArray, '$[*]'
                                                                      COLUMNS ( com_name TEXT PATH '$.company_name')) as w_com_t)
                            AND acwe.candidate_id NOT IN (SELECT c2_id
                                                          from json_table(notInCandidateIds, '$[*]'
                                                                          COLUMNS ( c2_id INT PATH '$' ERROR ON ERROR)) as c_id_t2))
            );
    END IF;
END$$
DELIMITER ;
