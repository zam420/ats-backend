package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.TableButtonConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableButtonConfigRepository extends JpaRepository<TableButtonConfig, Long> {
    List<TableButtonConfig> findByUserIdAndTableOrderBySortAsc(Long userId, String table);
    void deleteByUserIdAndTable(Long userId, String table);
}
