package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "ats_history", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"target_model", "target_id", "is_subject"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "target_model", nullable = false, length = 100)
    private String targetModel;

    @Column(name = "target_id", nullable = false)
    private Integer targetId;

    @Column(name = "is_subject", nullable = false)
    @Builder.Default
    private Boolean isSubject = true;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "json")
    private Map<String, Object> history;
}
