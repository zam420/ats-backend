package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.AppFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppFileRepository extends JpaRepository<AppFile, Long> {
    List<AppFile> findByUploadUserId(Long uploadUserId);
}
