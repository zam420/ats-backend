package com.deepconnect.ats.common.model;
import com.deepconnect.ats.user.model.User;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerId", referencedColumnName = "id", insertable = false, updatable = false)
    private User owner; // By default, the user who created this record. Can be changed.

    @Column(name = "ownerId", nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private String ownerPublicId;

}