package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.JobPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobPositionRepository extends JpaRepository<JobPosition, Long>, JpaSpecificationExecutor<JobPosition> {
    List<JobPosition> findByProjectId(Long projectId);
    List<JobPosition> findByStatus(Integer status);
    Optional<JobPosition> findByTitleAndProjectId(String title, Long projectId);
}
