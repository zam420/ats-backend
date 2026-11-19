package com.deepconnect.ats.job.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.job.service.JobPositionService;
import com.deepconnect.ats.job.dto.JobPositionReq;
import com.deepconnect.ats.job.dto.JobPositionSearchQuery;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.dto.JobFiltersRes;
import com.deepconnect.ats.job.dto.JobPositionRes;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
@Validated
public class JobPositionController {

    private final JobPositionService jobPositionService;

    @PreAuthorize("hasAuthority('jpc')")
    @PostMapping("/create")
    public ApiResult<Map<String, String>> create(@RequestBody @Valid JobPositionReq req) {
        String id = jobPositionService.create(req);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAuthority('jpc')")
    @PostMapping("/saveas")
    public ApiResult<Map<String, String>> saveAs(@RequestBody @Valid JobPositionReq req) {
        return ApiResult.success(Collections.singletonMap("id", jobPositionService.create(req)));
    }

    @PreAuthorize("hasAuthority('jpe')")
    @PostMapping("/update")
    public ApiResult<Map<String, String>> update(@RequestBody @Valid JobPositionReq req) {
        String id = jobPositionService.update(req);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAuthority('jpe')")
    @PostMapping("/deactivate/{id}")
    public ApiResult<Boolean> deactivate(
        @PathVariable @NotBlank(message = "{field.common.id}{validation.notBlank}") String id) {
        jobPositionService.deactivate(id);
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAuthority('jpe')")
    @PostMapping("/publish/{id}")
    public ApiResult<Boolean> publish(
        @PathVariable @NotBlank(message = "{field.common.id}{validation.notBlank}") String id) {
        jobPositionService.publish(id);
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAuthority('jpe')")
    @PostMapping("/close/{id}")
    public ApiResult<Boolean> close(
        @PathVariable @NotBlank(message = "{field.common.id}{validation.notBlank}") String id) {
        jobPositionService.close(id);
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAuthority('jps')")
    @GetMapping("/all")
    public ApiResult<AtsPageRsp<JobPositionRes>> findAll(@Valid JobPositionSearchQuery query) {
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
    @GetMapping("/listAll")
    public ApiResult<AtsPageRsp<JobPosition>> listAll(AtsPageQuery query) {
        return ApiResult.success(jobPositionService.listJobPosition(query));
    }

    @PreAuthorize("hasAuthority('jps')")
    @GetMapping("/filters")
    public ApiResult<JobFiltersRes> getJobFilters() throws Exception {
        return ApiResult.success(jobPositionService.getFilters());
    }

}
