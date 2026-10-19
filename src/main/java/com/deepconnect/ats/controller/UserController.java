package com.deepconnect.ats.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Value;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.auth.dto.RolesPermissionsPair;
import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.dto.ApiResult;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.dto.ChangeUserStatusReq;
import com.deepconnect.ats.dto.SaveUserReq;
import com.deepconnect.ats.dto.UserInfoRsp;
import com.deepconnect.ats.dto.UserSummary;
import com.deepconnect.ats.model.User;
import com.deepconnect.ats.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Value("${jwt.authoritiesRolePrefix}")
    private String authoritiesRolePrefix;
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getUserInfo")
    public ApiResult<UserInfoRsp> getUserInfo() {
        LoggedInUserDetails userDetails = AuthService.getLoggedInUser();
        if (userDetails == null) {
            return ApiResult.fail(ApiResult.UNAUTHORIZED_CODE, "Unauthorized");
        }
        User user = userService.getById(userDetails.getUserId());
        UserInfoRsp rsp = UserInfoRsp.from(user);
        RolesPermissionsPair rolesPermissionsPair = userDetails.getRolePermissionsCodes(authoritiesRolePrefix);
        rsp.setRoles(rolesPermissionsPair.roleCodes());
        rsp.setPermissions(rolesPermissionsPair.permissionCodes());
        rsp.setNotifications(userService.getNotifications(userDetails.getUserId(), false));
        rsp.setTasks(userService.getNotifications(userDetails.getUserId(), true));
        return ApiResult.success(rsp);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/listUserByRole")
    public ApiResult<List<UserSummary>> listUserByRole(@RequestParam String role) {
        return ApiResult.success(userService.listByRole(role));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/removeAllNotifications")
    public ApiResult<Boolean> removeAllNotifications() {
        Long userId = AuthService.getLoggedInUserId();
        if (userId != null) {
            userService.removeAllNotifications(userId);
        }
        return ApiResult.success(true);
    }

    @PreAuthorize("@notificationAccess.canAccess(#body)")
    @PostMapping("/removeNotification")
    public ApiResult<Boolean> removeNotification(@RequestBody Map<String, Object> body) {
        Object idObj = body.get("id");
        if (idObj != null) {
            Long id = Long.valueOf(idObj.toString());
            userService.removeNotificationById(id);
        }
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAuthority('aus')")
    @GetMapping("/getUserFor")
    public ApiResult<List<UserSummary>> getUserFor(@RequestParam(name = "for", required = false) Integer useFor) {
        return ApiResult.success(userService.listActiveUsers());
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @GetMapping("/listUser")
    public ApiResult<AtsPageRsp<User>> listUser(AtsPageQuery query) {
        return ApiResult.success(userService.listUsers(query));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/saveUser")
    public ApiResult<Map<String, Long>> saveUser(@RequestBody SaveUserReq req) {
        Long id = userService.saveUser(req);
        return ApiResult.success(Collections.singletonMap("id", id));
    }

    @PreAuthorize("hasAnyRole('sa','hrd')")
    @PostMapping("/changeUserStatus")
    public ApiResult<Boolean> changeUserStatus(@RequestBody ChangeUserStatusReq req) {
        if (req.getId() == null || req.getStatus() == null) {
            throw new InternalServerException("Invalid parameters");
        }
        userService.changeStatus(req.getId(), req.getStatus());
        return ApiResult.success(true);
    }

    @PreAuthorize("hasAnyRole('sa','hrd','proa','hrr','hrrc')")
    @GetMapping("/listInnerUser")
    public ApiResult<List<UserSummary>> listInnerUser() {
        return ApiResult.success(userService.listActiveUsers());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/saveUserSettings")
    public ApiResult<Boolean> saveUserSettings(@RequestBody Map<String, Object> body) {
        Long userId = AuthService.getLoggedInUserId();
        if (userId == null) {
            return ApiResult.fail(ApiResult.UNAUTHORIZED_CODE, "Unauthorized");
        }
        Integer type = (Integer) body.get("type");
        @SuppressWarnings("unchecked")
        Map<String, Object> settings = (Map<String, Object>) body.get("settings");
        userService.saveUserSettings(userId, type, settings);
        return ApiResult.success(true);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getUserSettings")
    public ApiResult<Map<String, Object>> getUserSettings(@RequestParam Integer type) {
        Long userId = AuthService.getLoggedInUserId();
        if (userId == null) {
            return ApiResult.fail(ApiResult.UNAUTHORIZED_CODE, "Unauthorized");
        }
        return ApiResult.success(userService.getUserSettings(userId, type));
    }
}
