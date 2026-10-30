package com.deepconnect.ats.evaluationplan.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import com.deepconnect.ats.common.model.OperatableBaseWithTenantEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.deepconnect.ats.user.model.User;

@Entity
@Table(name = "evaluation_plan", uniqueConstraints = {
        @UniqueConstraint(name = "uk_evaluation_plan_name_tenant_deactivated",
                columnNames = {"name", "tenantId", "deactivated"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationPlan extends OperatableBaseWithTenantEntity {

    @Column(nullable = false, length = 100)
    @Length(max = 10, message = "{field.evaluationPlan.name}{validation.lengthMax}")
    @NotBlank(message = "{field.evaluationPlan.name}{validation.notBlank}")
    private String name;

    @Column(nullable = true, length = 300)
    @Length(max = 300, message = "{field.evaluationPlan.description}{validation.lengthMax}")
    private String description;

    @Column(columnDefinition = "tinyint(1) default false")
    @Builder.Default
    private Boolean deactivated = false;

    @Valid
    @JsonManagedReference
    @OneToMany(mappedBy = "evaluationPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvaluationPlanRound> rounds;

    @JsonIgnore
    @Column(columnDefinition = "tinyint(1) default false")
    @Builder.Default
    private Boolean isPublicForAll = false;//created as template by ATS admins and accessible to all recruiters (or above) for all tenants

    @JsonIgnore
    @Column(columnDefinition = "tinyint(1) default false")
    @Builder.Default
    private Boolean isPublicForTenant = false;//created as template by Tenant users and accessible to all recruiters (or above) within the tenant

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerId", referencedColumnName = "id", insertable = false, updatable = false)
    private User owner; // By default, the user who created this record. Can be changed.

    @Column(name = "ownerId", nullable = false)
    private Long ownerId;

    private String ownerPublicId;

}
