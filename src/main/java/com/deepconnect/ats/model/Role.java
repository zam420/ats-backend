package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ats_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 256)
    private String name;

    @Column(nullable = false, unique = true, length = 32)
    private String code;
}
