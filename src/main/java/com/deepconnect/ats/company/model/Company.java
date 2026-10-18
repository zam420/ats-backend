package com.deepconnect.ats.company.model;

import com.deepconnect.ats.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "company")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company extends BaseEntity{

    @Column(nullable = false, length = 256)
    private String name;
    
}
