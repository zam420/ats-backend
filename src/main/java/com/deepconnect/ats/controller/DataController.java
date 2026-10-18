package com.deepconnect.ats.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.deepconnect.ats.dto.ApiResult;

import java.util.Map;

@RestController
@RequestMapping("/api/data")
public class DataController {

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/ping")
    public ApiResult<String> ping() {
        return ApiResult.success("pong");
    }

    @PostMapping("/userNameUpdate")
    public ApiResult<Boolean> userNameUpdate(@RequestBody(required = false) Map<String, Object> body) {
        return ApiResult.success(true);
    }

    @PostMapping("/sprint12/updateExamWebReportUrl")
    public ApiResult<Boolean> updateExamWebReportUrl(@RequestBody(required = false) Map<String, Object> body) {
        return ApiResult.success(true);
    }
}
