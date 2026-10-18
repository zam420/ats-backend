package com.deepconnect.ats.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.dto.SaveTalentReq;
import com.deepconnect.ats.model.TalentPool;
import com.deepconnect.ats.service.TalentPoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/talentPool")
@RequiredArgsConstructor
public class TalentPoolController {

    private final TalentPoolService talentPoolService;

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @GetMapping("/list")
    public ApiResult<AtsPageRsp<TalentPool>> list(AtsPageQuery query) {
        return ApiResult.success(talentPoolService.listTalents(query));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @GetMapping("/getTalentDetails")
    public ApiResult<TalentPool> getTalentDetails(@RequestParam Long candidateResumeId) {
        return ApiResult.success(talentPoolService.getByResumeId(candidateResumeId).orElse(null));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/saveTalent")
    public ApiResult<Map<String, Long>> saveTalent(@RequestBody SaveTalentReq req) {
        Long id = talentPoolService.saveTalent(req);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @PostMapping("/resetTalent")
    public ApiResult<Boolean> resetTalent(@RequestBody Map<String, Long> body) {
        Long resumeId = body.get("candidateResumeId");
        if (resumeId != null) {
            talentPoolService.resetTalent(resumeId);
        }
        return ApiResult.success(true);
    }
}
