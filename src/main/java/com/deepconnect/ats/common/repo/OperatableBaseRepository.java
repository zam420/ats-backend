package com.deepconnect.ats.common.repo;

import java.util.Set;
import java.io.Serializable;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OperatableBaseRepository<T, ID extends Serializable> extends BaseRepository<T, ID> {
    Optional<T> findByDeactivatedFalseAndId(Long id);

    Optional<T> findByDeactivatedFalseAndPublicId(String publicId);
    Set<T> findByDeactivatedFalseAndPublicIdIn(Set<String> publicIds);

    Page<T> findByDeactivatedFalse(Pageable pageable);
}
