package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "ats_resume_operation_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeOperationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidate_resume_id", nullable = false)
    private Long candidateResumeId;

    @Column(name = "operator_id", nullable = false)
    private Long operatorId;

    @Column(name = "operation_type", nullable = false)
    private Integer operationType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "operate_args", columnDefinition = "json")
    private Map<String, Object> operateArgs;

    @CreationTimestamp
    @Column(name = "operate_at", nullable = false, updatable = false)
    private LocalDateTime operateAt;
}
