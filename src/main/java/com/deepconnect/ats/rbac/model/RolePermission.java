package com.deepconnect.ats.rbac.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ats_role_permission", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"role_id", "permission_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long roleId;

    @Column(nullable = false)
    private Long permissionId;

    @Column(nullable = false, length = 32)
    private String permissionCode;
}
