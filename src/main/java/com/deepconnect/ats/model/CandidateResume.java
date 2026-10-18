package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "ats_candidate_resume")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateResume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    @Column(length = 500)
    private String name;

    private Integer gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private Integer age;

    @Column(name = "phone_number", length = 16)
    private String phoneNumber;

    @Column(name = "phone_country_code", length = 8)
    private String phoneCountryCode;

    @Column(length = 254)
    private String email;

    @Column(name = "current_place", length = 200)
    private String currentPlace;

    @Column(name = "native_place_province", length = 50)
    private String nativePlaceProvince;

    @Column(name = "native_place_city", length = 50)
    private String nativePlaceCity;

    @Column(name = "employment_status")
    private Integer employmentStatus;

    @Column(name = "recruitment_info_from")
    private Integer recruitmentInfoFrom;

    @Column(name = "expected_work_location", length = 100)
    private String expectedWorkLocation;

    @Column(name = "expected_salary", length = 50)
    private String expectedSalary;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<Map<String, Object>> skills;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<Map<String, Object>> languages;

    @Column(name = "self_assessment", length = 2000)
    private String selfAssessment;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "original_resume", columnDefinition = "json")
    private Map<String, Object> originalResume;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<Map<String, Object>> attachments;

    @Column(name = "attachment_links", length = 1024)
    private String attachmentLinks;

    @Column(name = "referral_code", length = 10)
    private String referralCode;

    @Column(name = "applying_position_id")
    private Long applyingPositionId;

    @Column(name = "recruitment_source")
    private Integer recruitmentSource;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;

    @Column(length = 10240)
    private String notes;

    @Column(nullable = false)
    @Builder.Default
    private Integer status = 0;

    @Column(name = "sub_status")
    private Integer subStatus;

    @Column(name = "data_status", nullable = false)
    @Builder.Default
    private Integer dataStatus = 0;

    @Column(name = "create_by_id")
    private Long createById;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extended_qa", columnDefinition = "json")
    private List<Map<String, Object>> extendedQa;

    @Column(name = "highest_degree")
    private Integer highestDegree;

    @Column(name = "highest_major", length = 100)
    private String highestMajor;

    @Column(name = "highest_university", length = 100)
    private String highestUniversity;

    @Column(name = "bachelor_university", length = 100)
    private String bachelorUniversity;

    @Column(name = "last_company_name", length = 100)
    private String lastCompanyName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "management_status", nullable = false)
    @Builder.Default
    private Integer managementStatus = 0;

    @Column(name = "management_sub_status")
    private Integer managementSubStatus;

    @Column(name = "privacy_notes", length = 500)
    private String privacyNotes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<Long> interviewers;

    @Column(name = "expect_onboarding_date")
    private LocalDate expectOnboardingDate;
}
