package com.deepconnect.ats.candidate.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deepconnect.ats.candidate.dto.CandidateResMin;
import com.deepconnect.ats.job.service.JobPositionService;
import com.deepconnect.ats.jobapplication.model.JobApplication;
import com.deepconnect.ats.jobapplication.repo.JobApplicationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateService {

    private final JobPositionService jobPositionService;
    
    private final JobApplicationRepository jobApplicationRepository;
    /**
     * Get all candidates given job Public ID as a list. 
     * Used for selection dropdowns e.g. in job position forms
     * @param jobId the public ID of the job position
     * @return a list of minimal candidate representations for the given job position
     */
    @Transactional(readOnly = true)
    public List<CandidateResMin> findAllAsList(String jobId) {
        jobPositionService.assertReadAccess(jobId);
        List<JobApplication> applications = jobApplicationRepository.findByJobPositionPublicId(jobId);
        return applications.stream().map(app -> CandidateResMin.from(app)).toList();
    }

}
