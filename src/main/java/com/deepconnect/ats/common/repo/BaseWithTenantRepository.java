package com.deepconnect.ats.common.repo;

import java.util.Set;
import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseWithTenantRepository<T, ID extends Serializable> extends BaseRepository<T, ID> {
    Optional<T> findByPublicIdAndTenantId(String publicId, Long tenantId);
    Set<T> findByPublicIdInAndTenantId(Set<String> publicIds, Long tenantId);
    Page<T> findByTenantId(Long tenantId, Pageable pageable);
}
