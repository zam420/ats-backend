package com.deepconnect.ats.interviewprocess.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;

@Entity
@Table(name = "interview_process_round", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ipt_id", "`order`"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewProcessRound {

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

    @JsonBackReference
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewProcess", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private InterviewProcess interviewProcess;

    @Column(name = "interviewProcess", nullable = false)
    private Long interviewProcessId;
}
