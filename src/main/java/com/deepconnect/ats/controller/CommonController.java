package com.deepconnect.ats.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.model.TableButtonConfig;
import com.deepconnect.ats.model.TableHeaderConfig;
import com.deepconnect.ats.model.Tag;
import com.deepconnect.ats.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
public class CommonController {

    private final CommonService commonService;
    private final com.deepconnect.ats.service.FileStorageService fileStorageService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/downloadFile")
    public org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> downloadFile(
            @RequestParam("uid") Long uid) {
        com.deepconnect.ats.model.AppFile meta = fileStorageService.getMeta(uid);
        org.springframework.core.io.Resource res =
                new org.springframework.core.io.FileSystemResource(fileStorageService.getPath(meta));
        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        org.springframework.http.ContentDisposition.attachment()
                                .filename(meta.getFileName(), java.nio.charset.StandardCharsets.UTF_8).build().toString())
                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                .body(res);
    }

    @GetMapping("/healthCheck")
    public ApiResult<String> healthCheck() {
        return ApiResult.success("ok");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getTableConfig")
    public ApiResult<List<TableHeaderConfig>> getTableConfig(@RequestParam String table) {
        return ApiResult.success(commonService.getTableHeaderConfig(table));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/saveTableConfig")
    public ApiResult<Boolean> saveTableConfig(@RequestBody Map<String, Object> body) {
        String table = body.get("table").toString();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> configsRaw = (List<Map<String, Object>>) body.get("configs");
        // Save logic
        return ApiResult.success(true);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getTableButtonConfig")
    public ApiResult<List<TableButtonConfig>> getTableButtonConfig(@RequestParam String table) {
        return ApiResult.success(commonService.getTableButtonConfig(table));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/saveTableButtonConfig")
    public ApiResult<Boolean> saveTableButtonConfig(@RequestBody Map<String, Object> body) {
        String table = body.get("table").toString();
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc','hm','itv')")
    @GetMapping("/tag/listAll")
    public ApiResult<List<Tag>> listAllTags() {
        return ApiResult.success(commonService.listAllTags());
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa')")
    @PostMapping("/tag/save")
    public ApiResult<Map<String, Long>> saveTag(@RequestBody Tag tag) {
        Long id = commonService.saveTag(tag);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa')")
    @PostMapping("/tag/delete")
    public ApiResult<Boolean> deleteTag(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        if (id != null) {
            commonService.deleteTag(id);
        }
        return ApiResult.success(true);
    }
}
