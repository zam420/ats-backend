package com.deepconnect.ats.screeningquestiontemplate.model;

import jakarta.persistence.*;
import lombok.*;

import com.deepconnect.ats.common.model.BaseEntity;
import com.deepconnect.ats.user.model.User;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This entity describes many-many intermediate relation for ScreeningQuestionTemplate and User. 
 * Owner of a screening question template can share it with other users and this entity describes that relationship.
 * ScreeningQuestionTemplateShare
 */
@Entity
@Table(name = "screening_question_template_share", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"screening_question_template", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreeningQuestionTemplateShare extends BaseEntity {
    
    @JsonBackReference("sharedWithUsers")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "screeningQuestionTemplate", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private ScreeningQuestionTemplate screeningQuestionTemplate;

    @Column(name = "screeningQuestionTemplate", nullable = false)
    private Long screeningQuestionTemplateId;

    @Column(name = "screeningQuestionTemplatePublicId", nullable = false)
    private String screeningQuestionTemplatePublicId;

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

