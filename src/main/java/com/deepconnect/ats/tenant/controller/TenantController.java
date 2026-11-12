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
import java.util.List;
import java.util.Map;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
@Validated // 1. Required at the class level for path variable/request param validation
public class TenantController {

    private final TenantService tenantService;

    @PreAuthorize("hasAuthority('clnc')")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResult<Map<String, String>> create(@RequestBody @Valid TenantReq req) {
        return ApiResult.success(Collections.singletonMap("id", tenantService.create(req)));
    }

    @PreAuthorize("hasAuthority('clne')")
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResult<Map<String, String>> update(@RequestBody @Valid TenantReq req) {
        return ApiResult.success(Collections.singletonMap("id", tenantService.update(req)));
    }

    @PreAuthorize("hasAuthority('clnc')")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResult<Map<String, String>> createWithLogo(@RequestPart("tenant") @Valid TenantReq req,
            @RequestPart(value = "logo", required = false) MultipartFile logo) {
        return ApiResult.success(Collections.singletonMap("id", tenantService.create(req, logo)));
    }

    @PreAuthorize("hasAuthority('clne')")
    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResult<Map<String, String>> updateWithLogo(@RequestPart("tenant") @Valid TenantReq req,
            @RequestPart(value = "logo", required = false) MultipartFile logo) {
        return ApiResult.success(Collections.singletonMap("id", tenantService.update(req, logo)));
    }

    @PreAuthorize("hasAuthority('clns')")
    @GetMapping("/{id}/logo")
    public ResponseEntity<FileSystemResource> logo(@PathVariable @NotBlank String id) {
        Path path = tenantService.findLogoByDeactivatedFalseAndPublicId(id);
        String fileName = path.getFileName().toString();
        MediaType type = fileName.endsWith(".svg") ? MediaType.parseMediaType("image/svg+xml")
                : fileName.endsWith(".png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok().contentType(type)
                .header("Content-Security-Policy", "default-src 'none'; sandbox")
                .header("X-Content-Type-Options", "nosniff")
                .body(new FileSystemResource(path));
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
        return ApiResult.success(tenantService.findByDeactivatedFalseAndPublicId(id));
    }

    @PreAuthorize("hasAuthority('clncur')")
    @GetMapping("/current")
    public ApiResult<TenantDetailsRes> findCurrent() {
        return ApiResult.success(tenantService.findCurrent());
    }

    @PreAuthorize("hasAuthority('clncur')")
    @GetMapping("/current/logo")
    public ResponseEntity<byte[]> currentLogo() throws IOException, NoSuchAlgorithmException {
        Path path = tenantService.findCurrentLogo();
        byte[] content = Files.readAllBytes(path);
        String fileName = path.getFileName().toString();
        MediaType type = fileName.endsWith(".svg") ? MediaType.parseMediaType("image/svg+xml")
                : fileName.endsWith(".png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok()
                .contentType(type)
                .cacheControl(CacheControl.noCache().cachePrivate().mustRevalidate())
                .varyBy(HttpHeaders.AUTHORIZATION)
                .eTag(HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(content)))
                .header("Content-Security-Policy", "default-src 'none'; sandbox")
                .header("X-Content-Type-Options", "nosniff")
                .body(content);
    }

    /**
     * Get all interview plans for the current tenant as a list. 
     * Used for selection dropdowns e.g. in job position forms
     * @param query
     * @return
     */
    @PreAuthorize("hasAuthority('clns')")
    @GetMapping("/list")
    public ApiResult<List<TenantRes>> findAllAsList() {
        return ApiResult.success(tenantService.findAllAsList());
    }

}
