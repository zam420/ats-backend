use ats;

# 简历查重 step 3
# 匹配度达标的简历(>70%)
#-------------------------------------------------------------------------------------------------------------
DROP PROCEDURE if exists `resumeDuplicationSimilarMatch`;
DELIMITER $$
CREATE PROCEDURE `resumeDuplicationSimilarMatch`(
    IN candidateName text,
    IN highestSchool json,
    IN workExpArray json,
    IN resumeIds json
)
BEGIN
    DECLARE matched_resume_array json DEFAULT JSON_ARRAY();
    DECLARE highest_university TEXT;
    DECLARE highest_from TEXT;
    DECLARE highest_to TEXT;
    DECLARE id INT default 0;
    DECLARE candidate_id INT DEFAULT 0;
    DECLARE candidate_resume_id INT default 0;
    DECLARE name TEXT DEFAULT '';
    DECLARE applying_position_id INT DEFAULT 0;
    DECLARE applied_at DATETIME;
    DECLARE university TEXT DEFAULT '';
    DECLARE company_name TEXT DEFAULT '';
    DECLARE date_from DATE;
    DECLARE date_to DATE;
    DECLARE match_rate INT DEFAULT 0;
    /**
      query_json：key is resume id
    {
       "id1": {
           "id":1,
           "name":"candidate name",
           "edu": [{"university":"school name", "from":"2000-01-01", "to":"2002-12-01"}],
           "work":[{"company":"company name", "from":"2000-01-01", "to":"2002-12-01"}]
       }
     }
     */
    DECLARE query_json json DEFAULT JSON_OBJECT();
    DECLARE done INT DEFAULT FALSE;
    # 查询简历
    DECLARE resume_cur CURSOR FOR
        SELECT acr.id, acr.name, acr.applying_position_id, acr.applied_at, acr.candidate_id
        FROM ats.ats_candidate_resume acr
        WHERE acr.id in (SELECT r_id
                         from json_table(resumeIds, '$[*]'
                                         COLUMNS ( r_id INT PATH '$' ERROR ON ERROR)) as r_id_t1);
# 查询教育经历
    DECLARE edu_cur CURSOR FOR
        SELECT aceh.id, aceh.candidate_resume_id, aceh.university, aceh.date_from, aceh.date_to
        FROM ats.ats_candidate_education_history as aceh
        WHERE aceh.data_status = 1
          AND aceh.university = highest_university
          AND aceh.candidate_resume_id in (SELECT r_id
                                           from json_table(resumeIds, '$[*]'
                                                           COLUMNS ( r_id INT PATH '$' ERROR ON ERROR)) as r_id_t2);
# 查询工作经验
    DECLARE work_cur CURSOR FOR
        SELECT acwe.id, acwe.candidate_resume_id, acwe.company_name, acwe.date_from, acwe.date_to
        FROM ats.ats_candidate_work_experience acwe
        WHERE acwe.data_status = 1
          AND acwe.company_name in (SELECT c_name
                                    from json_table(workExpArray, '$[*]'
                                                    COLUMNS ( c_name TEXT PATH '$.company' ERROR ON ERROR )) as w_cname_t)
          AND acwe.candidate_resume_id in (SELECT r_id
                                           from json_table(resumeIds, '$[*]'
                                                           COLUMNS ( r_id INT PATH '$' ERROR ON ERROR)) as r_id_t3);
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    SET highest_university = JSON_UNQUOTE(JSON_EXTRACT(highestSchool, '$.university'));
    SET highest_from = JSON_UNQUOTE(JSON_EXTRACT(highestSchool, '$.from'));
    SET highest_to = JSON_UNQUOTE(JSON_EXTRACT(highestSchool, '$.to'));
    SET highest_from = SUBSTR(highest_from FROM 1 FOR 7);
    SET highest_to = SUBSTR(highest_to FROM 1 FOR 7);

    SET query_json = JSON_OBJECT();
    SET done = FALSE;
    OPEN resume_cur;
    read_resume:
    LOOP
        FETCH resume_cur INTO id,name,applying_position_id,applied_at,candidate_id;
        IF done THEN
            LEAVE read_resume;
        END IF;

        SET @resume_json = JSON_OBJECT();
        SET @resume_json = JSON_SET(@resume_json, '$.id', id);
        SET @resume_json = JSON_SET(@resume_json, '$.name', name);
        SET @resume_json = JSON_SET(@resume_json, '$.candidate_id', candidate_id);
        SET @resume_json = JSON_SET(@resume_json, '$.position_id', applying_position_id);
        SET @resume_json = JSON_SET(@resume_json, '$.applied_at', applied_at);
        SET @resume_json = JSON_SET(@resume_json, '$.edu', CAST(JSON_ARRAY() as JSON));
        SET @resume_json = JSON_SET(@resume_json, '$.work', CAST(JSON_ARRAY() as JSON));

        SET @resume_id_key = CONCAT('$.id', id);
        SET query_json = JSON_SET(query_json, @resume_id_key, CAST(@resume_json as JSON));
    end loop;
    CLOSE resume_cur;

    IF highest_university IS NOT NULL AND CHAR_LENGTH(highest_university) > 0 THEN
        SET done = FALSE;
        OPEN edu_cur;
        read_edu:
        LOOP
            FETCH edu_cur INTO id,candidate_resume_id,university,date_from,date_to;
            IF done THEN
                LEAVE read_edu;
            END IF;

            SET @school_json = JSON_OBJECT();
            SET @school_json = JSON_SET(@school_json, '$.university', university, '$.from', date_from, '$.to', date_to);
            SET @school_key = CONCAT('$.id', candidate_resume_id, '.edu');
            SET query_json = JSON_ARRAY_APPEND(query_json, @school_key, CAST(@school_json as JSON));
        end loop;
        CLOSE edu_cur;
    END IF;

    IF JSON_LENGTH(workExpArray) > 0 THEN
        SET done = FALSE;
        OPEN work_cur;
        read_work:
        LOOP
            FETCH work_cur INTO id,candidate_resume_id,company_name,date_from,date_to;
            IF done THEN
                LEAVE read_work;
            END IF;

            SET @work_json = JSON_OBJECT();
            SET @work_json = JSON_SET(@work_json, '$.company', company_name);
            SET @work_json = JSON_SET(@work_json, '$.from', date_from);
            SET @work_json = JSON_SET(@work_json, '$.to', date_to);
            SET @work_key = CONCAT('$.id', candidate_resume_id, '.work');
            SET query_json = JSON_ARRAY_APPEND(query_json, @work_key, CAST(@work_json as JSON));

        end loop;
        CLOSE work_cur;
    END IF;
    # query_json 组装结束，格式参看声明

    SET match_rate = 0;
    SET @resume_id_len = JSON_LENGTH(resumeIds);
    SET @resume_id_idx = 0;
    WHILE @resume_id_idx < @resume_id_len
        DO
            SET @name_match_rate = 0;
            SET @school_match_rate = 0;
            SET @school_from_match_rate = 0;
            SET @school_to_match_rate = 0;
            SET @work_match_rate = 0;
            SET @work_from_match_rate = 0;
            SET @work_to_match_rate = 0;

            SET @resume_id = JSON_UNQUOTE(JSON_EXTRACT(resumeIds, CONCAT('$[', @resume_id_idx, ']')));
            SET @resume_id_key = CONCAT('$.id', @resume_id);
            SET @resume_json = JSON_UNQUOTE(JSON_EXTRACT(query_json, @resume_id_key));
            SET @candidate_name = JSON_UNQUOTE(JSON_EXTRACT(@resume_json, '$.name'));
            SET @edu_array = JSON_UNQUOTE(JSON_EXTRACT(@resume_json, '$.edu'));
            SET @work_array = JSON_UNQUOTE(JSON_EXTRACT(@resume_json, '$.work'));

            if candidateName = @candidate_name then
                SET @name_match_rate = @name_match_rate + 40;
            else
                /*
                 举例: 若
                 candidateName = '王'
                 @candidate_name = '张三'
                 调用 SUBSTR 函数截取 @candidate_name，使其长度等于 candidateName, 即变成 '张'
                 */
                SET @candidate_name = SUBSTR(@candidate_name FROM 1 FOR CHAR_LENGTH(candidateName));
                if candidateName = @candidate_name then
                    SET @name_match_rate = @name_match_rate + 10;
                end if;
            end if;

            SET @edu_idx = 0;
            SET @edu_len = JSON_LENGTH(@edu_array);
            WHILE @edu_idx < @edu_len
                DO
                    SET @edu_json = JSON_UNQUOTE(JSON_EXTRACT(@edu_array, CONCAT('$[', @edu_idx, ']')));
                    SET @edu_name = JSON_UNQUOTE(JSON_EXTRACT(@edu_json, '$.university'));
                    if highest_university = @edu_name then
                        SET @school_match_rate = 20;
                        SET @edu_from = JSON_UNQUOTE(JSON_EXTRACT(@edu_json, '$.from'));
                        SET @edu_to = JSON_UNQUOTE(JSON_EXTRACT(@edu_json, '$.to'));
                        SET @edu_from = SUBSTR(@edu_from FROM 1 FOR 7);
                        SET @edu_to = SUBSTR(@edu_to FROM 1 FOR 7);
                        if highest_from = @edu_from then
                            SET @school_from_match_rate = 5;
                        end if;
                        if highest_to = @edu_to then
                            SET @school_to_match_rate = 5;
                        end if;
                        SET @edu_idx = @edu_len;
                    end if;
                    SET @edu_idx = @edu_idx + 1;
                end while;

            SET @work_idx = 0;
            SET @work_len = JSON_LENGTH(@work_array);
            SET @in_work_idx = 0;
            SET @in_work_len = JSON_LENGTH(workExpArray);
            WHILE @in_work_idx < @in_work_len
                DO
                    SET @in_work_json = JSON_UNQUOTE(JSON_EXTRACT(workExpArray, CONCAT('$[', @in_work_idx, ']')));
                    SET @in_work_name = JSON_UNQUOTE(JSON_EXTRACT(@in_work_json, '$.company'));
                    SET @in_work_from = JSON_UNQUOTE(JSON_EXTRACT(@in_work_json, '$.from'));
                    SET @in_work_to = JSON_UNQUOTE(JSON_EXTRACT(@in_work_json, '$.to'));
                    SET @in_work_from = SUBSTR(@in_work_from FROM 1 FOR 7);
                    SET @in_work_to = SUBSTR(@in_work_to FROM 1 FOR 7);

                    SET @work_idx = 0;
                    while @work_idx < @work_len
                        do
                            SET @work_json = JSON_UNQUOTE(JSON_EXTRACT(@work_array, CONCAT('$[', @work_idx, ']')));
                            SET @work_name = JSON_UNQUOTE(JSON_EXTRACT(@work_json, '$.company'));
                            SET @work_from = JSON_UNQUOTE(JSON_EXTRACT(@work_json, '$.from'));
                            SET @work_to = JSON_UNQUOTE(JSON_EXTRACT(@work_json, '$.to'));
                            SET @work_from = SUBSTR(@work_from FROM 1 FOR 7);
                            SET @work_to = SUBSTR(@work_to FROM 1 FOR 7);
                            if @in_work_name = @work_name then
                                SET @work_match_rate = @work_match_rate + 20;
                                if @in_work_from = @work_from then
                                    SET @work_from_match_rate = @work_from_match_rate + 5;
                                end if;
                                if @in_work_to = @work_to then
                                    SET @work_to_match_rate = @work_to_match_rate + 5;
                                end if;
                            end if;

                            SET @work_idx = @work_idx + 1;
                        end while;

                    SET @in_work_idx = @in_work_idx + 1;
                end while;

            SET match_rate = @name_match_rate + @school_match_rate + @school_from_match_rate + @school_to_match_rate +
                             @work_match_rate + @work_from_match_rate + @work_to_match_rate;
            if match_rate > 99 then
                SET match_rate = 99;
            end if;
            if match_rate >= 70 then
                SET @resume_json = JSON_SET(@resume_json, '$.match_rate', match_rate);
                SET @resume_json = JSON_SET(@resume_json, '$.name_match_rate', @name_match_rate);
                SET @resume_json = JSON_SET(@resume_json, '$.edu_match_rate', @school_match_rate);
                SET @resume_json = JSON_SET(@resume_json, '$.edu_from_match_rate', @school_from_match_rate);
                SET @resume_json = JSON_SET(@resume_json, '$.edu_to_match_rate', @school_to_match_rate);
                SET @resume_json = JSON_SET(@resume_json, '$.work_match_rate', @work_match_rate);
                SET @resume_json = JSON_SET(@resume_json, '$.work_from_match_rate', @work_from_match_rate);
                SET @resume_json = JSON_SET(@resume_json, '$.work_to_match_rate', @work_to_match_rate);

                SET matched_resume_array = JSON_ARRAY_APPEND(matched_resume_array, '$', @resume_json);
            end if;
            SET @resume_id_idx = @resume_id_idx + 1;
        end while;

    /**
      返回一个 列为 json 的表，json 的格式如下：
      {
        "id": 1, // resume id
        "name": "candidate name",
        "edu": [{"university":"school name", "from":"2000-01-01", "to":"2002-12-01"}],
        "work":[{"company":"company name", "from":"2000-01-01", "to":"2002-12-01"}],
        "match_rate": 99, // max is 99
        "name_match_rate": 40,
        "edu_match_rate": 20,
        "edu_from_match_rate": 5,
        "edu_to_match_rate": 5,
        "work_match_rate": 20,
        "work_from_match_rate": 5,
        "work_to_match_rate": 5
      }
      注意：因为 mysql 中往 json array 中 append json object，json object 会被自动转成字符串，因此客户端可能接受到的 json 为这种格式
      "{\"key\": \"value\"}"
      需要客户端自行处理
     */
    SELECT *
    from json_table(matched_resume_array, '$[*]' COLUMNS ( ret json PATH '$' ERROR ON ERROR)) as matched_resume;
END$$
DELIMITER ;
