package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ats_resume_tags", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"candidate_resume_id", "tag_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "match_type", nullable = false)
    private Integer matchType;

    @Column(name = "candidate_resume_id", nullable = false)
    private Long candidateResumeId;

    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    @Column(name = "match_count", nullable = false)
    @Builder.Default
    private Integer matchCount = 1;
}
