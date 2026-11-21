package com.deepconnect.ats.jobapplication.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.deepconnect.ats.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "job_application", uniqueConstraints = @UniqueConstraint(columnNames = {"job_position_id", "email"}))
@Getter
@Setter
@NoArgsConstructor
public class JobApplication extends BaseEntity {

    @Column(name = "job_position_id", nullable = false)
    private Long jobPositionId;

    @Column(name = "candidate_user_id", nullable = false)
    private Long candidateUserId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "preferred_name", length = 100)
    private String preferredName;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(name = "country_code", nullable = false, length = 10)
    private String countryCode;

    @Column(name = "phone_number", nullable = false, length = 40)
    private String phoneNumber;

    @Column(name = "current_location", nullable = false, length = 200)
    private String currentLocation;

    @Column(name = "resume_path", nullable = false, length = 255)
    private String resumePath;

    @Column(name = "resume_name", nullable = false, length = 255)
    private String resumeName;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
}
