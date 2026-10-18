package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ats_permission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String code;

    @Column(nullable = false, length = 256)
    private String name;
}
