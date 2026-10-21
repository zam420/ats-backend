package com.deepconnect.ats.recruitmentproject.model;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.deepconnect.ats.common.model.OperatableBaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ats_recruitment_project")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentProject extends OperatableBaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private Integer status = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", nullable = true)
    private List<String> projectOwners;

}
