package com.deepconnect.ats.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.model.EmailTemplate;
import com.deepconnect.ats.service.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/emailTemplate")
@RequiredArgsConstructor
public class EmailTemplateController {

    private final EmailTemplateService emailTemplateService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/save")
    public ApiResult<Map<String, Long>> save(@RequestBody EmailTemplate template) {
        Long id = emailTemplateService.save(template);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @GetMapping("/list")
    public ApiResult<AtsPageRsp<EmailTemplate>> list(AtsPageQuery query) {
        return ApiResult.success(emailTemplateService.listTemplates(query));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @GetMapping("/getDetails")
    public ApiResult<EmailTemplate> getDetails(@RequestParam Long id) {
        return ApiResult.success(emailTemplateService.getById(id).orElse(null));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa')")
    @PostMapping("/changeStatus")
    public ApiResult<Boolean> changeStatus(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        Integer status = Integer.valueOf(body.get("status").toString());
        emailTemplateService.changeStatus(id, status);
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa')")
    @PostMapping("/saveSharing")
    public ApiResult<Boolean> saveSharing(@RequestBody Map<String, Object> body) {
        Long templateId = Long.valueOf(body.get("templateId").toString());
        @SuppressWarnings("unchecked")
        List<Long> shareTo = (List<Long>) body.get("shareTo");
        emailTemplateService.saveSharing(templateId, shareTo);
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @GetMapping("/listByJob")
    public ApiResult<List<EmailTemplate>> listByJob(@RequestParam(required = false) Long jobId) {
        return ApiResult.success(emailTemplateService.listActive());
    }
}
