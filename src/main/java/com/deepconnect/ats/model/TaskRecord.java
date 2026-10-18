package com.deepconnect.ats.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ats_task_record", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"task_id", "sub_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false, length = 225)
    private String taskId;

    @Column(nullable = false, length = 225)
    private String record;

    @Column(name = "sub_id", length = 225)
    private String subId;
}
