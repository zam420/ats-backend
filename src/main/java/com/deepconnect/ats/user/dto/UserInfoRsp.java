package com.deepconnect.ats.user.dto;

import com.deepconnect.ats.notification.model.Notification;
import com.deepconnect.ats.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoRsp {

    private String id;
    private String name;
    private String email;
    private String phone;
    private Integer type;

    @Builder.Default
    private Set<String> roles = new HashSet<>();

    @Builder.Default
    private Set<String> permissions = new HashSet<>();

    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();

    @Builder.Default
    private List<Notification> tasks = new ArrayList<>();

    public static UserInfoRsp from(User user) {
        if (user == null) return null;
        return UserInfoRsp.builder()
                .id(user.getPublicId())
                .name(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .type(user.getType())
                .build();
    }
}
