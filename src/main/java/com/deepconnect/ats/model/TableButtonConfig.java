package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ats_table_button_config", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "table", "state", "button"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableButtonConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(name = "`table`", nullable = false, length = 100)
    private String table;

    @Column(nullable = false, length = 100)
    private String button;

    @Column(nullable = false)
    private Integer sort;

    @Column(name = "user_id", nullable = false)
    private Long userId;
}
