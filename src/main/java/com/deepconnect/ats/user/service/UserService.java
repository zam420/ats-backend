package com.deepconnect.ats.user.service;

import com.deepconnect.ats.auth.dto.ActivationReq;
import com.deepconnect.ats.auth.dto.ForgotPasswordReq;
import com.deepconnect.ats.auth.dto.RegisterReq;
import com.deepconnect.ats.auth.dto.ResetPasswordReq;
import com.deepconnect.ats.auth.service.RefreshTokenService;
import com.deepconnect.ats.common.constant.enums.CommonStatus;
import com.deepconnect.ats.common.email.EmailContent;
import com.deepconnect.ats.common.email.EmailLocalizationService;
import com.deepconnect.ats.common.email.EmailService;
import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.enums.EmailType;
import com.deepconnect.ats.common.enums.RoleType;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.common.exception.UnauthorizedException;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.security.PasswordEncoderService;
import com.deepconnect.ats.notification.model.Notification;
import com.deepconnect.ats.notification.repo.NotificationRepository;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.user.dto.SaveUserReq;
import com.deepconnect.ats.user.dto.UserSummary;
import com.deepconnect.ats.user.model.*;
import com.deepconnect.ats.rbac.model.Role;
import com.deepconnect.ats.rbac.service.RoleService;
import com.deepconnect.ats.user.repo.*;
import com.deepconnect.ats.rbac.model.UserRole;
import com.deepconnect.ats.rbac.model.RolePermission;
import com.deepconnect.ats.tenant.enums.TenantType;

import com.deepconnect.ats.rbac.repo.UserRoleRepository;
import com.deepconnect.ats.rbac.repo.RolePermissionRepository;
import com.deepconnect.ats.rbac.service.UserRoleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${account.activationCodeLength}")
    private Integer activationCodeLength;

    @Value("${account.passwordResetCodeLength:8}")
    private Integer passwordResetCodeLength;

    @Value("${account.passwordResetCodeValidMinutes:15}")
    private Integer passwordResetCodeValidMinutes;

    private final PasswordEncoderService passwordEncoderService;
    private final RefreshTokenService refreshTokenService;
    private final EmailLocalizationService emailLocalizationService;
    private final EmailService emailService;
    private final UserRoleService userRoleService;
    private final RoleService roleService;

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final NotificationRepository notificationRepository;

    private String generateActivationCode(int length) {
        String CHARACTERS = "ABCDEFGHJKL12345abcdMNPQRSTUVWXYZefghijk6789mnpqrstuvwxyz";//we don't include L, l, 0, O and o to avoid confusion for user
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

    public User register(RegisterReq request) {
        String email = request.getEmail().trim(); 
        Optional<User> existingUser = this.findByEmail(email);
        if(existingUser.isPresent()){
            throw new CustomException(CustomExceptionType.REGISTER_EMAIL_IN_USE);
        }
        User user = new User();
        user.setSalt(BCrypt.gensalt());
        user.setPassword(passwordEncoderService.saltAndHash(request.getPassword()));
        user.setEmail(email);
        user.setFirstName(request.getFirstName() == null ? null : request.getFirstName().trim());
        user.setLastName(request.getLastName() == null ? null : request.getLastName().trim());
        user.setCreationDateTime(LocalDateTime.now());
        user.setActivationCode(this.generateActivationCode(activationCodeLength));
        user.setActivationCodeSentDateTime(LocalDateTime.now());
        user.setDeactivated(true);
        user.setTenantId(TenantType.PUBLIC.getValue());
        user = userRepository.save(user);
        userRoleService.addUserToRole(RoleType.CANDIDATE, user.getId());

        try{
            //this.sendActivationEmail(email, user.getActivationCode());//TODO: enable this
            log.info("Email sent successfully!");
        } catch (Exception ex) {
            log.error(ex.getMessage() + ex.getStackTrace().toString());
            userRepository.deleteUser(email);
            throw new CustomException(CustomExceptionType.REGISTER_ACTIVATION_EMAIL_FAILURE);
        }
        //TODO: store email sent info in DB

        return user;
    }

    public User registerOrLoginGoogle(String email, String firstName, String lastName) {
        User user = this.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setIsGoogleLogin(true);
            newUser.setCreationDateTime(LocalDateTime.now());
            newUser.setActivationDateTime(LocalDateTime.now());
            newUser.setTenantId(TenantType.PUBLIC.getValue());
            newUser = userRepository.saveAndFlush(newUser);
            userRoleService.addUserToRole(RoleType.CANDIDATE, newUser.getId());
            return newUser;
        });

        return user;
    }

    public void activate(ActivationReq request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UnauthorizedException());
        if(user.getDeactivated() == false){
            return;
        }

        if (!user.getActivationCode().equals(request.getActivationCode())) {
            throw new CustomException(CustomExceptionType.AUTH_INVALID_ACTIVATION_CODE);
        }

        user.setDeactivated(false);
        user.setActivationCode(null);
        user.setActivationDateTime(LocalDateTime.now());
        user = userRepository.save(user);
    }

    /**
     * Generates a password reset code and emails it. Unknown emails are ignored silently so the
     * endpoint cannot be used to discover which addresses are registered.
     */
    public void forgotPassword(ForgotPasswordReq request) {
        String email = request.getEmail() == null ? "" : request.getEmail().trim();
        Optional<User> found = this.findByEmail(email);
        if (found.isEmpty()) {
            return;
        }
        User user = found.get();
        user.setPasswordResetCode(this.generateActivationCode(passwordResetCodeLength));
        user.setPasswordResetCodeSentDateTime(LocalDateTime.now());
        user = userRepository.save(user);

        try {
            this.sendPasswordResetEmail(email, user.getPasswordResetCode());
        } catch (Exception ex) {
            log.error("Failed to send password reset email", ex);
            user.setPasswordResetCode(null);
            user.setPasswordResetCodeSentDateTime(null);
            userRepository.save(user);
            throw new CustomException(CustomExceptionType.AUTH_RESET_EMAIL_FAILURE);
        }
    }

    public void resetPassword(ResetPasswordReq request) {
        String email = request.getEmail() == null ? "" : request.getEmail().trim();
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new IllegalArgumentException("New password must not be blank");
        }
        User user = this.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomExceptionType.AUTH_INVALID_RESET_CODE));

        boolean valid = user.getPasswordResetCode() != null
                && request.getResetCode() != null
                && MessageDigest.isEqual(
                        user.getPasswordResetCode().getBytes(StandardCharsets.UTF_8),
                        request.getResetCode().trim().getBytes(StandardCharsets.UTF_8))
                && user.getPasswordResetCodeSentDateTime() != null
                && user.getPasswordResetCodeSentDateTime().plusMinutes(passwordResetCodeValidMinutes).isAfter(LocalDateTime.now());
        if (!valid) {
            throw new CustomException(CustomExceptionType.AUTH_INVALID_RESET_CODE);
        }

        user.setSalt(BCrypt.gensalt());
        user.setPassword(passwordEncoderService.saltAndHash(request.getNewPassword()));
        user.setCredentialsExpired(false);
        user.setModifiedDateTime(LocalDateTime.now());
        // The code is single-use
        user.setPasswordResetCode(null);
        user.setPasswordResetCodeSentDateTime(null);
        userRepository.save(user);
        // Sign the user out everywhere: any existing session may belong to whoever knew the old password
        refreshTokenService.deleteByUserId(user.getId());
    }

    private void sendPasswordResetEmail(String email, String resetCode) {
        HashMap<String, Object> emailContextVariables = new HashMap<>();
        emailContextVariables.put("resetCode", resetCode);
        emailContextVariables.put("validMinutes", passwordResetCodeValidMinutes);
        EmailContent emailContent = emailLocalizationService.getEmailContent(EmailType.ACCOUNT_PASSWORD_RESET, emailContextVariables);
        emailService.sendEmail(email, emailContent.getSubject(), emailContent.getBody());
    }

    private void sendActivationEmail(String email, String activationCode){
        HashMap<String, Object> emailContextVariables = new HashMap<>();
        //emailContextVariables.put("userName", userName);
        emailContextVariables.put("activationCode", activationCode);
        EmailContent emailContent = emailLocalizationService.getEmailContent(EmailType.ACCOUNT_ACTIVATION, emailContextVariables);
        emailService.sendEmail(email, emailContent.getSubject(), emailContent.getBody());
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public Set<String> getRoleCodesByUserId(Long userId) {
        Set<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        if (userRoles.isEmpty()) return new HashSet<>();
        return userRoles.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
    }

    public Set<Role> getRolesByUserId(Long userId) {
        Set<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        if (userRoles.isEmpty()) return new HashSet<>();
        Set<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        return new HashSet<>(roleService.findAllById(roleIds));
    }

    public Set<String> getPermissionCodesByRoleIds(Set<Long> roleIds) {
        if (roleIds.isEmpty()) return Set.of();
        Set<RolePermission> rolePermissions = rolePermissionRepository.findByRoleIdIn(roleIds);
        return rolePermissions.stream().map(RolePermission::getPermissionCode).distinct().collect(Collectors.toSet());
    }

    public Set<String> getPermissionCodesByUserId(Long userId) {
        Set<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        if (userRoles.isEmpty()) return Set.of();
        Set<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        Set<RolePermission> rolePermissions = rolePermissionRepository.findByRoleIdIn(roleIds);
        return rolePermissions.stream().map(RolePermission::getPermissionCode).distinct().collect(Collectors.toSet());
    }

    // roleCode may be a comma-separated list; users in any of the roles are returned
    public List<UserSummary> listByRole(String roleCode) {
        Set<Long> userIds = new HashSet<>();
        for (String code : roleCode.split(",")) {
            Role role = roleService.findByCode(code.trim()).orElse(null);
            if (role == null) continue;
            userRoleRepository.findByRoleId(role.getId()).forEach(ur -> userIds.add(ur.getUserId()));
        }
        if (userIds.isEmpty()) return new ArrayList<>();
        return userRepository.findByIdIn(userIds).stream()
                .filter(u -> CommonStatus.ACTIVE.getValue().equals(u.getStatus()))
                .map(UserSummary::from)
                .collect(Collectors.toList());
    }

    public List<UserSummary> listActiveUsers() {
        return userRepository.findByStatus(CommonStatus.ACTIVE.getValue()).stream()
        .filter(user -> user.getType() == 0)//return only corporate users
                .map(UserSummary::from)
                .collect(Collectors.toList());
    }

    public AtsPageRsp<User> listUsers(AtsPageQuery query) {
        Page<User> page = userRepository.findAll(query.toPageable());
        return AtsPageRsp.of(page);
    }

    @Transactional
    public Long saveUser(SaveUserReq req) {
        User user;
        if (req.getId() != null) {
            user = userRepository.findById(req.getId())
                    .orElseThrow(() -> new InternalServerException("User not found with id: " + req.getId()));
        } else {
            user = new User();
        }

        if (req.getName() != null) {
            String[] parts = req.getName().trim().split("\\s+", 2);
            user.setFirstName(parts[0]);
            user.setLastName(parts.length > 1 ? parts[1] : parts[0]);
        }
        if (req.getEmail() != null) user.setEmail(req.getEmail());
        if (req.getPhone() != null) user.setPhone(req.getPhone());
        if (req.getType() != null) user.setType(req.getType());
        if (req.getStatus() != null) user.setStatus(req.getStatus());

        User savedUser = userRepository.save(user);

        if (req.getRoleIds() != null) {
            userRoleRepository.deleteByUserId(savedUser.getId());
            for (Long roleId : req.getRoleIds()) {
                userRoleRepository.save(UserRole.builder()
                        .userId(savedUser.getId())
                        .roleId(roleId)
                        .build());
            }
        }

        return savedUser.getId();
    }

    @Transactional
    public void changeStatus(Long userId, Integer status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InternalServerException("User not found with id: " + userId));
        user.setStatus(status);
        userRepository.save(user);
    }

    @Transactional
    public void saveUserSettings(Long userId, Integer type, Map<String, Object> settings) {
        UserSettings userSettings = userSettingsRepository.findByUserIdAndType(userId, type)
                .orElse(UserSettings.builder()
                        .userId(userId)
                        .type(type)
                        .build());
        userSettings.setSettings(settings);
        userSettingsRepository.save(userSettings);
    }

    public Map<String, Object> getUserSettings(Long userId, Integer type) {
        return userSettingsRepository.findByUserIdAndType(userId, type)
                .map(UserSettings::getSettings)
                .orElse(null);
    }

    public List<Notification> getNotifications(Long userId, boolean task) {
        return notificationRepository.findByUserIdOrderByCreateTimeDesc(userId).stream()
                .filter(n -> Boolean.valueOf(task).equals(n.getTask()) && !Boolean.TRUE.equals(n.getComplete()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeNotificationById(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setComplete(true);
            notificationRepository.save(n);
        });
    }

    @Transactional
    public void removeAllNotifications(Long userId) {
        List<Notification> list = notificationRepository.findByUserIdOrderByCreateTimeDesc(userId);
        for (Notification n : list) {
            n.setComplete(true);
        }
        notificationRepository.saveAll(list);
    }

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUserId(Long userId){
        return userRepository.findById(userId);
    }

    public Optional<User> findByPublicId(String publicId){
        return userRepository.findByPublicId(publicId);
    }
}
