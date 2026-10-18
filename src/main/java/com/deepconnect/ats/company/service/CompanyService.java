package com.deepconnect.ats.company.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.deepconnect.ats.company.model.Company;
import com.deepconnect.ats.company.repo.CompanyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public Optional<Company> findById(Long id) {
        return companyRepository.findById(id);
    }

    public Set<Company> findAllById(Set<Long> companyIds) {
        return new HashSet<>(companyRepository.findAllById(companyIds));
    }
}

