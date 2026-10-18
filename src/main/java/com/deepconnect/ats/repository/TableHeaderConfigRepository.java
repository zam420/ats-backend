package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.TableHeaderConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableHeaderConfigRepository extends JpaRepository<TableHeaderConfig, Long> {
    List<TableHeaderConfig> findByUserIdAndTableOrderBySortAsc(Long userId, String table);
    void deleteByUserIdAndTable(Long userId, String table);
}
