package com.deepconnect.ats.user.model;

import com.deepconnect.ats.common.model.BaseWithTenantEntity;
import com.deepconnect.ats.company.model.Company;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter 
@Setter
@Entity
@Table(name = "ats_user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseWithTenantEntity {

    @Column(nullable = true, length = 50)
    @Length(min = 2, max = 50, message = "{field.user.firstName}{validation.length}")
    private String firstName;

    @Column(nullable = true, length = 50)
    @Length(min = 2, max = 50, message = "{field.user.lastName}{validation.length}")
    private String lastName;

    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message = "{field.user.email}{validation.notBlank}")
    @Length(min = 7, max = 50, message = "{field.user.email}{validation.length}")
    @Email(message = "{field.user.email}{validation.email}")
    private String email;

    @Column(length = 64)
    private String phone;

    @JsonIgnore
    @Column(length = 256)
    private String password;

    @JsonIgnore
    private String salt;

    @Column(nullable = false)
    @Builder.Default
    private Integer type = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer status = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime joinDate;

    @JsonIgnore
    public String getFullName() {
        return ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
    }

    @JsonIgnore
    @Column(columnDefinition = "tinyint(1) default false")
    @Builder.Default
    private Boolean deactivated = false;

    // Indicates whether the user is locked or unlocked. A locked user cannot be authenticated
    @JsonIgnore
    @Column(columnDefinition = "tinyint(1) default false")
    @Builder.Default
    private Boolean locked = false;

    // Indicates whether the user's credentials (password) has expired. Expired credentials prevent authentication.
    @JsonIgnore
    @Column(columnDefinition = "tinyint(1) default false")
    @Builder.Default
    private Boolean credentialsExpired = false;

    // Indicates whether the user logged in via Google account
    @JsonIgnore
    @Column(columnDefinition = "tinyint(1) default false")
    @Builder.Default
    private Boolean isGoogleLogin = false;

    private String activationCode;

    @JsonIgnore
    @Column(nullable = true)
    private LocalDateTime activationCodeSentDateTime;

    @JsonIgnore
    private String passwordResetCode;

    @JsonIgnore
    @Column(nullable = true)
    private LocalDateTime passwordResetCodeSentDateTime;

    @JsonIgnore
    private LocalDateTime creationDateTime;

    @JsonIgnore
    @Column(nullable = true)
    private LocalDateTime activationDateTime;

    @JsonIgnore
    @Column(nullable = true)
    private LocalDateTime lockedDateTime;

    @JsonIgnore
    @Column(nullable = true)
    private LocalDateTime modifiedDateTime; // last modified

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lockedByUserId", referencedColumnName = "id", insertable = false, updatable = false)
    private User lockedByUser; // The user who locked this record

    @Column(name = "lockedByUserId", nullable = true)
    private Long lockedByUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyId", referencedColumnName = "id", insertable = false, updatable = false)
    private Company company; // The company associated with the corporate users. Null for candidates

    @Column(name = "companyId", nullable = true)
    private Long companyId; // The company associated with the corporate users. Null for candidates

    @Column(nullable = true)
    private String lockedByUserName; // Denormalized for quick access

    public String getName() {
        return ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
    }
}
