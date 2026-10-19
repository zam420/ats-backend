package com.deepconnect.ats.recruitmentproject.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.recruitmentproject.model.RecruitmentProject;
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
    @PostMapping("/save")
    public ApiResult<Map<String, Long>> saveProject(@RequestBody RecruitmentProject project) {
        Long id = projectService.saveProject(project);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAuthority('rpe')")
    @PostMapping("/deactivate")
    public ApiResult<Boolean> deactivate(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        if (id != null) {
            projectService.inactiveProject(id);
        }
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAuthority('rps')")
    @GetMapping("/listSummary")
    public ApiResult<List<RecruitmentProject>> listSummary() {
        return ApiResult.success(projectService.listActive());
    }

    @PreAuthorize("hasAuthority('rps')")
    @GetMapping("/list")
    public ApiResult<AtsPageRsp<RecruitmentProject>> listProject(AtsPageQuery query) {
        return ApiResult.success(projectService.listProject(query));
    }
}
