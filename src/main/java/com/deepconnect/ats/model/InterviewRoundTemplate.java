package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "ats_interview_round_template", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ipt_id", "`order`"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewRoundTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "round_type", nullable = false)
    private Integer roundType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<Long> interviewers;

    @Column(name = "`order`", nullable = false)
    private Integer order;

    @Column(name = "ipt_id", nullable = false)
    private Long iptId;
}
