package com.deepconnect.ats.tenant.model;

import com.deepconnect.ats.common.model.OperatableBaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import com.deepconnect.ats.tenant.enums.TenantType;

@Entity
@Table(name = "tenant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant extends OperatableBaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String name;

     @Column(nullable = true, length = 400)
    private String description;

    @JsonIgnore
    @Column(columnDefinition = "tinyint(1) default false")
    @Builder.Default
    private Boolean deactivated = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TenantType type;

}
