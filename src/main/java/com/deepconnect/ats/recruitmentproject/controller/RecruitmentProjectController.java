package com.deepconnect.ats.recruitmentproject.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageQueryWithStatus;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.recruitmentproject.model.RecruitmentProject;
import com.deepconnect.ats.recruitmentproject.dto.RecruitmentProjectReqRes;
import com.deepconnect.ats.recruitmentproject.service.RecruitmentProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class RecruitmentProjectController {

    private final RecruitmentProjectService projectService;

    @PreAuthorize("hasAuthority('rpe')")
    @PostMapping("/create")
    public ApiResult<Map<String, String>> create(@RequestBody RecruitmentProjectReqRes project) {
        String id = projectService.create(project);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAuthority('rpe')")
    @PostMapping("/update")
    public ApiResult<Map<String, String>> update(@RequestBody RecruitmentProjectReqRes project) {
        String id = projectService.update(project);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAuthority('rpe')")
    @PostMapping("/deactivate/{id}/{status}")
    public ApiResult<Boolean> deactivate(
        @PathVariable @NotBlank (message = "{field.common.id}{validation.notBlank}") String id,
        @PathVariable @NotNull(message = "{field.common.status}{validation.notBlank}") Integer status) {
        projectService.deactivate(id, status);
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAuthority('rps')")
    @GetMapping("/list")
    public ApiResult<AtsPageRsp<RecruitmentProjectReqRes>> listProject(AtsPageQueryWithStatus query) {
        return ApiResult.success(projectService.getAllByPage(query));
    }
}
