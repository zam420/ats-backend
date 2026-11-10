package com.deepconnect.ats.job.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.job.service.JobPositionService;
import com.deepconnect.ats.job.dto.JobPositionReq;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.dto.JobFieldsData;
import com.deepconnect.ats.job.dto.JobPositionRes;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobPositionController {

    private final JobPositionService jobPositionService;

    @PreAuthorize("hasAuthority('jpc')")
    @PostMapping("/create")
    public ApiResult<Map<String, String>> create(@RequestBody @Valid JobPositionReq req) {
        String id = jobPositionService.create(req);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAuthority('jpc')")
    @PostMapping("/publish")
    public ApiResult<Boolean> publish(@RequestBody Map<String, Object> body) {
        String id = body.get("id").toString();
        Integer status = Integer.valueOf(body.get("status").toString());
        jobPositionService.publish(id, status);
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAuthority('jpe')")
    @PostMapping("/update")
    public ApiResult<Map<String, String>> update(@RequestBody JobPositionReq req) {
        String id = jobPositionService.update(req);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAuthority('jps')")
    @GetMapping("/all")
    public ApiResult<AtsPageRsp<JobPositionRes>> findAll(AtsPageQuery query) {
        return ApiResult.success(jobPositionService.findAll(query));
    }

    @PreAuthorize("hasAuthority('jps')")
    @GetMapping("/{id}")
    public ApiResult<JobPositionRes> findById(
        @PathVariable @NotBlank(message = "{field.common.id}{validation.notBlank}")String id) {
        return ApiResult.success(jobPositionService.findByPublicIdForTenant(id));
    }


    // @PreAuthorize("hasAuthority('jps')")
    // @GetMapping("/listJobPostionForCV")
    // public ApiResult<List<JobPositionRes>> listJobPositionForCV() {
    //     return ApiResult.success(jobPositionService.listActive());
    // }

    @PreAuthorize("hasAuthority('jps')")
    @GetMapping("/job/listAll")
    public ApiResult<AtsPageRsp<JobPosition>> listAll(AtsPageQuery query) {
        return ApiResult.success(jobPositionService.listJobPosition(query));
    }

    @PreAuthorize("hasAuthority('jps')")
    @GetMapping("/jobFieldsData")
    public ApiResult<JobFieldsData> getJobFieldsData() throws Exception {
        return ApiResult.success(jobPositionService.getJobFieldsData());
    }

}
