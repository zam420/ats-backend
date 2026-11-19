package com.deepconnect.ats.tenant.controller;
import com.deepconnect.ats.common.security.SecurityConfig;
import com.deepconnect.ats.tenant.service.TenantLogoStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(SecurityConfig.PUBLIC_LOGO_PATH)
@RequiredArgsConstructor
public class PublicTenantLogoController {
    private final TenantLogoStorage storage;

    @GetMapping("/{name}")
    public ResponseEntity<FileSystemResource> logo(@PathVariable String name) {
        Path path = storage.getPath(name);
        MediaType type = name.endsWith(".svg") ? MediaType.parseMediaType("image/svg+xml")
            : name.endsWith(".png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok()
            .contentType(type)
            .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic().immutable())
            .header("Content-Security-Policy", "default-src 'none'; sandbox")
            .header("X-Content-Type-Options", "nosniff")
            .body(new FileSystemResource(path));
    }
}
