package com.deepconnect.ats.common.repo;

import java.util.Set;
import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
    Optional<T> findByPublicId(String publicId);
    Set<T> findByPublicIdIn(Set<String> publicIds);
}
