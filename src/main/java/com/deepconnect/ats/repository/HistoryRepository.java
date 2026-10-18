package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    Optional<History> findByTargetModelAndTargetIdAndIsSubject(String targetModel, Integer targetId, Boolean isSubject);
}
