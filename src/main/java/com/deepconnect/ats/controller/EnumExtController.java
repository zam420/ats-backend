package com.deepconnect.ats.controller;

import com.deepconnect.ats.common.constant.enums.CandidateSubStatus;
import com.deepconnect.ats.dto.ApiResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Enum list endpoints registered via enum_api_register in Django; value/name = member value/member name. */
@RestController
@RequestMapping("/api/enum")
public class EnumExtController {

    private static List<Map<String, Object>> of(Object... pairs) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < pairs.length; i += 2) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("value", pairs[i + 1]);
            m.put("name", pairs[i]);
            list.add(m);
        }
        return list;
    }

    @GetMapping("/listCandidateStatus")
    public ApiResult<?> listCandidateStatus() {
        return ApiResult.success(of("TALENT_POOL", -1, "UN_SCREENED", 0, "RESUME_SCREENING", 1,
                "UNIDIRECTIONAL_VIDEO_INTERVIEW", 2, "WRITTEN_EXAM", 3, "HR_INTERVIEW", 4,
                "PROFESSIONAL_INTERVIEW", 5, "CEO_INTERVIEW", 6, "OFFER_MANAGEMENT", 7,
                "ONBOARDING_MANAGEMENT", 8));
    }

    @GetMapping("/listCandidateSubStatus")
    public ApiResult<?> listCandidateSubStatus() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CandidateSubStatus s : CandidateSubStatus.values()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("value", List.of(s.getCandidateStatus(), s.getCode()));
            m.put("name", s.name().toLowerCase());
            list.add(m);
        }
        return ApiResult.success(list);
    }

    @GetMapping("/listChannelType")
    public ApiResult<?> listChannelType() {
        return ApiResult.success(of("LIE_PIN", 0, "OFFICIAL_WEBSITE_CN", 1, "OFFICIAL_WEBSITE_EN", 2,
                "LINKED_IN", 3));
    }

    @GetMapping("/listEmploymentType")
    public ApiResult<?> listEmploymentType() {
        return ApiResult.success(of("FULL_TIME", 0, "PART_TIME", 1, "CONTRACT", 2, "INTERNSHIP", 3));
    }

    @GetMapping("/listInterviewRoundType")
    public ApiResult<?> listInterviewRoundType() {
        return ApiResult.success(of("WRITTEN_EXAM_ROUND", 0, "UNIDIRECTIONAL_VIDEO_ROUND", 1, "HR_ROUND", 2,
                "PROFESSIONAL_ROUND", 3, "CEO_ROUND", 4));
    }

    @GetMapping("/listJobType")
    public ApiResult<?> listJobType() {
        return ApiResult.success(of("SOC", 0, "AI", 1, "HARDWARE", 2, "SOFTWARE", 3, "GENERAL", 4,
                "BUSINESS", 5, "PRODUCTION", 6));
    }

    @GetMapping("/listRecruitmentType")
    public ApiResult<?> listRecruitmentType() {
        return ApiResult.success(of("CAMPUS", 0, "CORPORATE", 1, "INTERN", 2));
    }

    @GetMapping("/listResumeInbox")
    public ApiResult<?> listResumeInbox() {
        return ApiResult.success(of("CAMPUS", "campus@espressif.com", "RECRUIT", "recruit@espressif.com",
                "CAMPUS_IN", "campus.in@espressif.com", "RECRUIT_IN", "recruit.in@espressif.com",
                "RECRUIT_SGPR", "recruit.sgpr@espressif.com", "RECRUIT_CZ", "recruit.cz@espressif.com",
                "RECRUIT_BR", "recruit.br@espressif.com", "RECRUIT_JP", "recruit.jp@espressif.com"));
    }

    @GetMapping("/listWorkLocation")
    public ApiResult<?> listWorkLocation() {
        return ApiResult.success(of("REMOTE", 0, "CHINA", 1, "SHANGHAI", 2, "WUXI", 3, "HEFEI", 4,
                "SUZHOU", 5, "SHENZHEN", 6, "CZECH", 500, "BRNO", 501, "INDIA", 600, "PUNE", 601,
                "SINGAPORE", 700, "BRAZIL", 800, "CAMPINAS", 801, "JAPAN", 900, "TOKYO", 901,
                "OSAKA", 902, "YOKOHAMA", 903));
    }

    @GetMapping("/listPerformanceRank")
    public ApiResult<?> listPerformanceRank() {
        return ApiResult.success(of("TOP_TEN", 0, "TEN_TO_THIRTY", 1, "THIRTY_TO_FIFTY", 2,
                "FIFTY_TO_SEVENTY", 3, "SEVENTY_TO_HUNDRED", 4));
    }

    @GetMapping("/listEmploymentStatus")
    public ApiResult<?> listEmploymentStatus() {
        return ApiResult.success(of("EMPLOYED", 0, "TERMINATED", 1));
    }

    @GetMapping("/listForwardStatus")
    public ApiResult<?> listForwardStatus() {
        return ApiResult.success(of("PENDING", 0, "SUITABLE", 1, "UNSUITABLE", 2, "KEEP_IN_VIEW", 3));
    }

    @GetMapping("/listRecruitmentInfoChannel")
    public ApiResult<?> listRecruitmentInfoChannel() {
        return ApiResult.success(of("ESPRESSIF_WECHAT_OFFICIAL", 0, "ESPRESSIF_OFFICIAL_WEBSITE", 1,
                "REFERRAL_BY_PEERS", 2, "REFERRAL_BY_TEACHER", 3, "SCHOOL_JOB_INFORMATION", 4, "JOB51", 5,
                "CAMPUS_BBS", 6, "INTERNSHIP_MONK", 7, "ZHANGZONG_CAMPUS_RECRUITMENT", 8,
                "OFFER_GENTLEMAN", 9, "OTHER", 10));
    }

    @GetMapping("/listResumeSource")
    public ApiResult<?> listResumeSource() {
        return ApiResult.success(of("LIEPIN", 0, "OFFICIAL_WEBSITE", 1, "JOB51", 2, "MAIMAI", 3, "BOSS", 4,
                "LINKEDIN", 5, "NOWCODER", 6, "HEADHUNTER_REFERRAL", 7, "INTERNAL_EMPLOYEE_REFERRAL", 8,
                "CANDIDATE_REFERRAL", 9, "INTERNSHIP_MONK", 10, "INDEED", 11, "NAUKRI", 12, "JOBS_CZ", 13,
                "JAZZHR", 14, "MONSTER", 15, "OTHERS", 16));
    }
}
