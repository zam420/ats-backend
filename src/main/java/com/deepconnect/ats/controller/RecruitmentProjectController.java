package com.deepconnect.ats.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.model.RecruitmentProject;
import com.deepconnect.ats.service.RecruitmentProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recruitment")
@RequiredArgsConstructor
public class RecruitmentProjectController {

    private final RecruitmentProjectService projectService;

    @PreAuthorize("hasAnyRole('sa','hrd')")
    @PostMapping("/saveProject")
    public ApiResult<Map<String, Long>> saveProject(@RequestBody RecruitmentProject project) {
        Long id = projectService.saveProject(project);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAnyRole('sa','hrd')")
    @PostMapping("/inactiveProject")
    public ApiResult<Boolean> inactiveProject(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        if (id != null) {
            projectService.inactiveProject(id);
        }
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @GetMapping("/listProjectSummary")
    public ApiResult<List<RecruitmentProject>> listProjectSummary() {
        return ApiResult.success(projectService.listActive());
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @GetMapping("/listProject")
    public ApiResult<AtsPageRsp<RecruitmentProject>> listProject(AtsPageQuery query) {
        return ApiResult.success(projectService.listProject(query));
    }
}
