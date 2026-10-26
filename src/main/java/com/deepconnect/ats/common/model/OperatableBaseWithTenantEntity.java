package com.deepconnect.ats.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import com.deepconnect.ats.tenant.model.Tenant;

@Getter
@Setter
@MappedSuperclass
public abstract class OperatableBaseWithTenantEntity extends OperatableBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenantId", referencedColumnName = "id", insertable = false, updatable = false)
    private Tenant tenant; // The user who created this record

    @Column(name = "tenantId", nullable = false)
    private Long tenantId;

}