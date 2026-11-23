package com.deepconnect.ats.screeningquestionsform.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import com.deepconnect.ats.common.model.OperatableBaseWithTenantEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "screening_questions_form", uniqueConstraints = {
        @UniqueConstraint(name = "uk_screening_questions_form_name_tenant_deactivated",
                columnNames = {"name", "tenantId", "deactivated"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreeningQuestionsForm extends OperatableBaseWithTenantEntity {

    @Column(nullable = false, length = 100)
    @Length(max = 100, message = "{field.screeningQuestions.form.name}{validation.lengthMax}")
    @NotBlank(message = "{field.screeningQuestions.form.name}{validation.notBlank}")
    private String name;

    @Column(nullable = true, length = 300)
    @Length(max = 300, message = "{field.screeningQuestions.form.description}{validation.lengthMax}")
    private String description;

    @Column(columnDefinition = "tinyint(1) default false")
    @Builder.Default
    private Boolean deactivated = false;

    @Valid
    @JsonManagedReference("questions")
    @OneToMany(mappedBy = "screeningQuestionsForm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScreeningQuestion> questions;

    @JsonIgnore
    @Column(columnDefinition = "tinyint(1) default false")
    @Builder.Default
    private Boolean isPublicForAll = false;//created as template by ATS admins and accessible to all recruiters (or above) for all tenants

    @JsonIgnore
    @Column(columnDefinition = "tinyint(1) default false")
    @Builder.Default
    private Boolean isPublicForTenant = false;//created as template by Tenant users and accessible to all recruiters (or above) within the tenant

    @JsonIgnore
    @Column(columnDefinition = "tinyint(1) default false")
    @Builder.Default
    private Boolean isSharedWithinTenant = false;//created by Tenant users and shared with specific users within tenant

    @Valid
    @JsonManagedReference("sharedWithUsers")
    @OneToMany(mappedBy = "screeningQuestionsForm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScreeningQuestionsFormShare> sharedWithUsers;

}
