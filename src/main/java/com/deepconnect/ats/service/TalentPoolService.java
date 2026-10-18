package com.deepconnect.ats.service;

import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.dto.SaveTalentReq;
import com.deepconnect.ats.model.TalentPool;
import com.deepconnect.ats.repository.TalentPoolRepository;
import com.deepconnect.ats.service.auth.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TalentPoolService {

    private final TalentPoolRepository talentPoolRepository;

    public AtsPageRsp<TalentPool> listTalents(AtsPageQuery query) {
        Page<TalentPool> page = talentPoolRepository.findAll(query.toPageable());
        return AtsPageRsp.of(page);
    }

    public Optional<TalentPool> getByResumeId(Long resumeId) {
        return talentPoolRepository.findByCandidateResumeId(resumeId);
    }

    @Transactional
    public Long saveTalent(SaveTalentReq req) {
        Long currentUserId = AuthService.getLoggedInUserId();
        if (currentUserId == null) currentUserId = 1L;

        TalentPool talent = talentPoolRepository.findByCandidateResumeId(req.getCandidateResumeId())
                .orElse(TalentPool.builder()
                        .candidateResumeId(req.getCandidateResumeId())
                        .pooledById(currentUserId)
                        .build());

        talent.setPooledReason(req.getPooledReason());
        talent.setComments(req.getComments());

        return talentPoolRepository.save(talent).getId();
    }

    @Transactional
    public void resetTalent(Long candidateResumeId) {
        talentPoolRepository.deleteByCandidateResumeId(candidateResumeId);
    }
}
