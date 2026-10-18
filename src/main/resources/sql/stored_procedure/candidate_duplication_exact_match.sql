use ats;

# 简历查重 step 1
# 完全匹配的候选人
#-------------------------------------------------------------------------------------------------------------
DROP PROCEDURE if exists `candidateDuplicationExactMatch`;
DELIMITER $$
CREATE PROCEDURE `candidateDuplicationExactMatch`(
    IN candidatePhone text,
    IN candidateEmail text
)
BEGIN
    DECLARE candidate_phone text;
    DECLARE candidate_email text;
    SET candidate_phone = candidatePhone;
    SET candidate_email = candidateEmail;

    if (candidate_phone is not null and CHAR_LENGTH(candidate_phone) > 0) and
       (candidate_email is not null and CHAR_LENGTH(candidate_email) > 0) then
        SELECT ac.`id`, ac.`name`, ac.`gender`, ac.`phone_number`, ac.`email`
        FROM `ats`.`ats_candidate` ac
        WHERE ac.`data_status` = 1
          and (ac.`phone_number` = candidate_phone or ac.`email` = candidate_email);
    end if;
    if (candidate_phone is null or CHAR_LENGTH(candidate_phone) = 0) and
       (candidate_email is not null and CHAR_LENGTH(candidate_email) > 0) then
        SELECT ac.`id`, ac.`name`, ac.`gender`, ac.`phone_number`, ac.`email`
        FROM `ats`.`ats_candidate` ac
        WHERE ac.`data_status` = 1
          and `email` = candidate_email;
    end if;
    if (candidate_phone is not null and CHAR_LENGTH(candidate_phone) > 0) and
       (candidate_email is null or CHAR_LENGTH(candidate_email) = 0) then
        SELECT ac.`id`, ac.`name`, ac.`gender`, ac.`phone_number`, ac.`email`
        FROM `ats`.`ats_candidate` ac
        WHERE ac.`data_status` = 1
          and ac.`phone_number` = candidate_phone;
    end if;
    if (candidate_phone is null or CHAR_LENGTH(candidate_phone) = 0) and
       (candidate_email is null or CHAR_LENGTH(candidate_email) = 0) then
        SELECT `id`, `name`, `gender`, `phone_number`, `email` FROM `ats`.`ats_candidate` WHERE `id` = 0;
    end if;
END$$
DELIMITER ;
