package com.deepconnect.ats.common.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import com.deepconnect.ats.model.User;

@Getter
@Setter
@MappedSuperclass
public abstract class OperatableBaseEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdByUserId", referencedColumnName = "id", insertable = false, updatable = false)
    private User createdByUser; // The user who created this record

    @Column(name = "createdByUserId", nullable = false)
    private Long createdByUserId;

    @CreationTimestamp
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modifiedByUserId", referencedColumnName = "id", insertable = false, updatable = false)
    private User modifiedByUser; // The user who modified this record

    @Column(name = "modifiedByUserId", nullable = true)
    private Long modifiedByUserId;

    @UpdateTimestamp
    @Column(name = "modifiedAt", nullable = true)
    private LocalDateTime modifiedAt;
}