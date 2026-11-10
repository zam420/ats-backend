package com.deepconnect.ats.recruitmentpipeline.model;

import jakarta.persistence.*;
import lombok.*;

import com.deepconnect.ats.common.model.BaseEntity;
import com.deepconnect.ats.user.model.User;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This entity describes many-many intermediate relation for RecruitmentPipeline and User
 * Owner of a recruitment pipeline can share it with other users and this entity describes that relationship.
 * RecruitmentPipelineShare
 */
@Entity
@Table(name = "recruitment_pipeline_share", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"recruitment_pipeline", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentPipelineShare extends BaseEntity {
    
    @JsonBackReference("sharedWithUsers")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitmentPipeline", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private RecruitmentPipeline recruitmentPipeline;

    @Column(name = "recruitmentPipeline", nullable = false)
    private Long recruitmentPipelineId;

    @Column(name = "recruitmentPipelinePublicId", nullable = false)
    private String recruitmentPipelinePublicId;

    // Not a parent/child link, so it must not be a second back reference
    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private User user;

    @Column(name = "user", nullable = false)
    private Long userId;

    @Column(name = "userPublicId", nullable = false)
    private String userPublicId;

}

