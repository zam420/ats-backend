package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ats_table_header_config", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "table", "field"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableHeaderConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Boolean display;

    @Column(name = "`table`", nullable = false, length = 100)
    private String table;

    @Column(nullable = false, length = 100)
    private String field;

    @Column(nullable = false)
    private Integer sort;
}
