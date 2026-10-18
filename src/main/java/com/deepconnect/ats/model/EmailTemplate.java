package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "ats_email_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String subject;

    @Column(nullable = false, length = 5000)
    private String content;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "recruitment_type", nullable = false, columnDefinition = "json")
    private List<Integer> recruitmentType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "work_location", nullable = false, columnDefinition = "json")
    private List<Integer> workLocation;

    @Column(length = 2000)
    private String notes;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "last_modified_time")
    private LocalDateTime lastModifiedTime;

    @Column(nullable = false)
    private Integer status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> fields;

    @Column(name = "create_by_id", nullable = false)
    private Long createById;

    @Column(name = "last_modified_by_id")
    private Long lastModifiedById;
}
