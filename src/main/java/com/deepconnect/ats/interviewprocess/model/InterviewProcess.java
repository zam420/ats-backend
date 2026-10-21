package com.deepconnect.ats.interviewprocess.model;

import jakarta.persistence.*;
import lombok.*;

import com.deepconnect.ats.common.model.OperatableBaseEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

@Entity
@Table(name = "interview_process")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewProcess extends OperatableBaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "work_location", nullable = false)
    private Integer workLocation;

    @Column(length = 100)
    private String notes;

    @Column(nullable = false)
    private Integer status;

    @JsonManagedReference
    @OneToMany(mappedBy = "interviewProcess", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterviewProcessRound> rounds;

}
