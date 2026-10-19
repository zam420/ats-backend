package com.deepconnect.ats.interviewprocess.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.deepconnect.ats.common.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "interview_process")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewProcess extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "work_location", nullable = false)
    private Integer workLocation;

    @Column(length = 100)
    private String notes;

    @Column(nullable = false)
    private Integer status;

    @JsonManagedReference
    @OneToMany(mappedBy = "interviewProcess", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterviewProcessRound> rounds;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by_id", nullable = false)
    private Long createdById;

    @Column(name = "modified_by_id", nullable = false)
    private Long modifiedById;
}
