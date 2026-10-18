package com.deepconnect.ats.company.repo;

import org.springframework.stereotype.Repository;

import com.deepconnect.ats.common.repo.BaseRepository;
import com.deepconnect.ats.company.model.Company;

@Repository
public interface CompanyRepository extends BaseRepository<Company, Long> {
    
}
