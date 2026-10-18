package com.deepconnect.ats.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.model.JobPosition;
import com.deepconnect.ats.service.JobPositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recruitment")
@RequiredArgsConstructor
public class JobPositionController {

    private final JobPositionService jobPositionService;

    @PreAuthorize("hasAnyRole('sa','hrd','proa')")
    @PostMapping("/createJobPosition")
    public ApiResult<Map<String, Long>> createJobPosition(@RequestBody JobPosition position) {
        Long id = jobPositionService.createJobPosition(position);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr')")
    @PostMapping("/updateJobPosition")
    public ApiResult<Map<String, Long>> updateJobPosition(@RequestBody JobPosition position) {
        Long id = jobPositionService.updateJobPosition(position);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @GetMapping("/listJobPositionSummary")
    public ApiResult<List<JobPosition>> listJobPositionSummary() {
        return ApiResult.success(jobPositionService.listActive());
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @GetMapping("/listJobPostionForCV")
    public ApiResult<List<JobPosition>> listJobPositionForCV() {
        return ApiResult.success(jobPositionService.listActive());
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @GetMapping("/getJobPositionDetail")
    public ApiResult<JobPosition> getJobPositionDetail(@RequestParam Long id) {
        return ApiResult.success(jobPositionService.getById(id).orElse(null));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr')")
    @PostMapping("/publishJob")
    public ApiResult<Boolean> publishJob(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        Integer status = Integer.valueOf(body.get("status").toString());
        jobPositionService.publishJob(id, status);
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @GetMapping("/job/listAll")
    public ApiResult<AtsPageRsp<JobPosition>> listAll(AtsPageQuery query) {
        return ApiResult.success(jobPositionService.listJobPosition(query));
    }
}
