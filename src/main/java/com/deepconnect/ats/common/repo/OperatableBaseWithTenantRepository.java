package com.deepconnect.ats.common.repo;

import java.util.Set;
import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OperatableBaseWithTenantRepository<T, ID extends Serializable> extends OperatableBaseRepository<T, ID> {
    Optional<T> findByPublicIdAndTenantIdAndDeactivatedFalse(String publicId, Long tenantId);
    Set<T> findByPublicIdInAndTenantIdAndDeactivatedFalse(Set<String> publicIds, Long tenantId);
    Page<T> findByTenantId(Long tenantId, Pageable pageable);
    Page<T> findByDeactivatedFalseAndTenantId(Long tenantId, Pageable pageable);
}
