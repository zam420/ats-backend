package com.deepconnect.ats.rbac.model;

import com.deepconnect.ats.common.model.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rbac_permission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission extends BaseEntity {

    @Column(nullable = false, unique = true, length = 32)
    private String code;

    @Column(nullable = false, length = 256)
    private String name;
}
