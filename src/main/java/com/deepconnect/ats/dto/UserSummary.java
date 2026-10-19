package com.deepconnect.ats.dto;

import com.deepconnect.ats.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummary {
    private String id;
    private String name;
    private String email;

    public static UserSummary from(User user) {
        if (user == null) return null;
        return UserSummary.builder()
                .id(user.getPublicId())
                .name(user.getFullName())
                .email(user.getEmail())
                .build();
    }
}
