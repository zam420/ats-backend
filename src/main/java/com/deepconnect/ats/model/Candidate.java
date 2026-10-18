package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "ats_candidate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(length = 10240)
    private String notes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<Object> tags;

    @Column(name = "highest_degree")
    private Integer highestDegree;

    @Column(name = "data_status", nullable = false)
    @Builder.Default
    private Integer dataStatus = 0;

    @Column(name = "create_by_id")
    private Long createById;
}
