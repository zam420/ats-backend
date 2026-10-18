package com.deepconnect.ats.rbac.model;
import com.deepconnect.ats.common.model.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ats_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    @Column(nullable = false, length = 256)
    private String name;

    @Column(nullable = false, unique = true, length = 32)
    private String code;
}
