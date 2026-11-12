package com.deepconnect.ats.tenant.model;

import org.hibernate.validator.constraints.Length;
import jakarta.validation.constraints.NotBlank;

import com.deepconnect.ats.common.model.OperatableBaseEntity;
import com.deepconnect.ats.user.model.User;
import com.deepconnect.ats.tenant.enums.TenantType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tenant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant extends OperatableBaseEntity {

    @Column(nullable = false, length = 100)
    @Length(max = 100, message = "{field.common.name}{validation.lengthMax}")
    @NotBlank(message = "{field.common.name}{validation.notBlank}")
    private String name;

    @Column(nullable = true, length = 300)
    @Length(max = 300, message = "{field.common.description}{validation.lengthMax}")
    private String description;

    @Column(columnDefinition = "tinyint(1) default false")
    @Builder.Default
    private Boolean deactivated = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TenantType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerId", referencedColumnName = "id", insertable = false, updatable = false)
    private User owner; // By default, the user who created this record. Can be changed.

    @Column(name = "ownerId", nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private String ownerPublicId;

}
