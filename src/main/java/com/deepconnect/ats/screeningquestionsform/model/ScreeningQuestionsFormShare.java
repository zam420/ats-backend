package com.deepconnect.ats.screeningquestionsform.model;

import jakarta.persistence.*;
import lombok.*;

import com.deepconnect.ats.common.model.BaseEntity;
import com.deepconnect.ats.user.model.User;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This entity describes many-many intermediate relation for ScreeningQuestionsForm and User. 
 * Owner of a screening questions form can share it with other users and this entity describes that relationship.
 * ScreeningQuestionTemplateShare
 */
@Entity
@Table(name = "screening_questions_form_share", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"screening_questions_form", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreeningQuestionsFormShare extends BaseEntity {
    
    @JsonBackReference("sharedWithUsers")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "screeningQuestionsForm", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private ScreeningQuestionsForm screeningQuestionsForm;

    @Column(name = "screeningQuestionsForm", nullable = false)
    private Long screeningQuestionsFormId;

    @Column(name = "screeningQuestionsFormPublicId", nullable = false)
    private String screeningQuestionsFormPublicId;

    // Not a parent/child link, so it must not be a second back reference
    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private User user;//user with whom its being shared

    @Column(name = "user", nullable = false)
    private Long userId;//user with whom its being shared

    @Column(name = "userPublicId", nullable = false)
    private String userPublicId;//public id of user with whom its being shared
    

}

