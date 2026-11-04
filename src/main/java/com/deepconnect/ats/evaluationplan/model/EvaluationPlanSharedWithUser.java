package com.deepconnect.ats.evaluationplan.model;

import jakarta.persistence.*;
import lombok.*;

import com.deepconnect.ats.common.model.BaseEntity;
import com.deepconnect.ats.user.model.User;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This entity describes many-many intermediate relation for EvaluationPlan and User
 * EvaluationPlanSharedWithUser
 */
@Entity
@Table(name = "evaluation_plan_shared_with_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"evaluation_plan", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationPlanSharedWithUser extends BaseEntity {
    
    @JsonBackReference("sharedWithUsers")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluationPlan", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private EvaluationPlan evaluationPlan;

    @Column(name = "evaluationPlan", nullable = false)
    private Long evaluationPlanId;

    @Column(name = "evaluationPlanPublicId", nullable = false)
    private String evaluationPlanPublicId;

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

