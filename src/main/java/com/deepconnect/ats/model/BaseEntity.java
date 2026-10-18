package com.deepconnect.ats.model;
import com.deepconnect.ats.common.security.PublicIdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Internal BIGINT primary key for database relations

    @Column(name = "public_id", nullable = false, unique = true, length = 12, updatable = false)
    private String publicId; // Public URL identifier

    @PrePersist
    public void prePersist() {
        if (this.publicId == null) {
            this.publicId = PublicIdGenerator.generate(12);
        }
    }

    public Long getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }
}