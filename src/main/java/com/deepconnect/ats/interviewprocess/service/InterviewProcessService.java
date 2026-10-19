package com.deepconnect.ats.interviewprocess.service;
import org.springframework.transaction.annotation.Transactional;
import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.interviewprocess.dto.InterviewProcessReq;
import com.deepconnect.ats.interviewprocess.model.InterviewProcess;
import com.deepconnect.ats.interviewprocess.model.InterviewProcessRound;
import com.deepconnect.ats.interviewprocess.repo.InterviewProcessRepository;
import com.deepconnect.ats.interviewprocess.repo.InterviewProcessRoundRepository;
import com.deepconnect.ats.dto.AtsPageQueryWithStatus;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.interviewprocess.dto.InterviewProcessRes;
import com.deepconnect.ats.interviewprocess.dto.InterviewProcessDetailsRes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewProcessService {

    private final InterviewProcessRepository interviewProcessRepository;
    private final InterviewProcessRoundRepository interviewProcessRoundRepository;

    public AtsPageRsp<InterviewProcessRes> getAllByPage(AtsPageQueryWithStatus query) {
        Page<InterviewProcess> page;
        if(query.getStatus() == null) {
            page = interviewProcessRepository.findAll(query.toPageable());
        } else {
            page = interviewProcessRepository.findByStatus(query.getStatus(), query.toPageable());
        }
        return AtsPageRsp.of(page.map(InterviewProcessRes::from));
    }

    public List<InterviewProcessRes> getAllActive() {
        List<InterviewProcess> entities = interviewProcessRepository.findByStatus(0);
        return InterviewProcessRes.fromList(entities);
    }

    @Transactional(readOnly = true)
    public Optional<InterviewProcessDetailsRes> findByPublicId(String publicId) {
        return interviewProcessRepository.findByPublicId(publicId).map(InterviewProcessDetailsRes::from);
    }

    private String save(InterviewProcess interviewProcess, InterviewProcessReq req) {
        interviewProcess.setModifiedById(AuthService.getLoggedInUserId());
        interviewProcess.setName(req.getName());
        if (req.getWorkLocation() != null) interviewProcess.setWorkLocation(req.getWorkLocation());
        if (req.getNotes() != null) interviewProcess.setNotes(req.getNotes());
        if (req.getStatus() != null) interviewProcess.setStatus(req.getStatus());
        else if (req.getStatus() == null) interviewProcess.setStatus(0);

        InterviewProcess saved = interviewProcessRepository.save(interviewProcess);

        if (req.getRounds() != null) {
            for (InterviewProcessRound roundTemplate : req.getRounds()) {
                roundTemplate.setIptId(saved.getId());
                interviewProcessRoundRepository.save(roundTemplate);
            }
        }

        return saved.getPublicId();
    }

    @Transactional
    public String create(InterviewProcessReq req) {
        Long currentUserId = AuthService.getLoggedInUserId();
        InterviewProcess interviewProcess = new InterviewProcess();
        interviewProcess.setCreatedById(currentUserId);
        return this.save(interviewProcess, req);
    }

    @Transactional
    public String update(InterviewProcessReq req) {        
        if (req.getId() == null) 
            throw new InternalServerException("Invalid Interview Process Id: " + req.getId());

        InterviewProcess interviewProcess = interviewProcessRepository.findByPublicId(req.getId())
                .orElseThrow(() -> new InternalServerException("Interview Process not found: " + req.getId()));

        return this.save(interviewProcess, req);
    }

    @Transactional
    public void deactivate(String publicId, Integer status) {
        InterviewProcess template = interviewProcessRepository.findByPublicId(publicId)
                .orElseThrow(() -> new InternalServerException("Interview Process not found: " + publicId));
        template.setStatus(status);
        interviewProcessRepository.save(template);
    }

}
