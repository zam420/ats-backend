package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.AtsEmailRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtsEmailRecordRepository extends JpaRepository<AtsEmailRecord, Long>, JpaSpecificationExecutor<AtsEmailRecord> {
    List<AtsEmailRecord> findByStatus(Integer status);
    List<AtsEmailRecord> findByFromUserId(Long fromUserId);
}
