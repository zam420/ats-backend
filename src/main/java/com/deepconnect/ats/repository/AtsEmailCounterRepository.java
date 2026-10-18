package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.AtsEmailCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AtsEmailCounterRepository extends JpaRepository<AtsEmailCounter, Long> {
    Optional<AtsEmailCounter> findByCounterTag(String counterTag);
}

