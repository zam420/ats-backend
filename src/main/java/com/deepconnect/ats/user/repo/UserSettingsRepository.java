package com.deepconnect.ats.user.repo;

import com.deepconnect.ats.user.model.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    Optional<UserSettings> findByUserIdAndType(Long userId, Integer type);
}
