package com.deepconnect.ats.tenant.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import com.deepconnect.ats.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.tenant.dto.TenantDetailsRes;
import com.deepconnect.ats.tenant.dto.TenantReq;
import com.deepconnect.ats.tenant.dto.TenantRes;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
@Validated // 1. Required at the class level for path variable/request param validation
public class TenantController {

    private final TenantService tenantService;

    @PreAuthorize("hasAuthority('clnc')")
    @PostMapping("/create")
    public ApiResult<Map<String, String>> create(@RequestBody @Valid TenantReq req) {
        return ApiResult.success(Collections.singletonMap("id", tenantService.create(req)));
    }

    @PreAuthorize("hasAuthority('clne')")
    @PostMapping("/update")
    public ApiResult<Map<String, String>> update(@RequestBody @Valid TenantReq req) {
        return ApiResult.success(Collections.singletonMap("id", tenantService.update(req)));
    }

    @PreAuthorize("hasAuthority('clne')")
    @PostMapping("/deactivate/{id}")
    public ApiResult<Boolean> deactivate(
        @PathVariable @NotBlank(message = "{field.common.id}{validation.notBlank}") String id) {
        return ApiResult.success(tenantService.deactivate(id));
    }

    @PreAuthorize("hasAuthority('clns')")
    @GetMapping("/all")
    public ApiResult<AtsPageRsp<TenantRes>> findAll(AtsPageQuery query) {
        return ApiResult.success(tenantService.findAll(query));
    }

    @PreAuthorize("hasAuthority('clns')")
    @GetMapping("/{id}")
    public ApiResult<TenantDetailsRes> findById(
        @PathVariable @NotBlank(message = "{field.common.id}{validation.notBlank}")String id) {
        return ApiResult.success(tenantService.findByPublicId(id));
    }

    /**
     * Get all interview plans for the current tenant as a list. 
     * Used for selection dropdowns e.g. in job position forms
     * @param query
     * @return
     */
    // @PreAuthorize("hasAuthority('clns')")
    // @GetMapping("/list")
    // public ApiResult<List<TenantRes>> findAllAsList() {
    //     return ApiResult.success(tenantService.findAllAsList());
    // }

}
