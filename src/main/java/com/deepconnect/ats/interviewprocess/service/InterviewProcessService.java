package com.deepconnect.ats.interviewprocess.service;
import org.springframework.transaction.annotation.Transactional;
import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.interviewprocess.dto.InterviewProcessReq;
import com.deepconnect.ats.interviewprocess.model.InterviewProcess;
import com.deepconnect.ats.interviewprocess.repo.InterviewProcessRepository;
import com.deepconnect.ats.dto.AtsPageQueryWithStatus;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.interviewprocess.dto.InterviewProcessRes;
import com.deepconnect.ats.interviewprocess.dto.InterviewProcessDetailsRes;
import com.deepconnect.ats.interviewprocess.model.InterviewProcessRound;
import com.deepconnect.ats.interviewprocess.repo.InterviewProcessRoundRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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

    private String save(InterviewProcess interviewProcess, InterviewProcessReq req, boolean saveRounds) {
        interviewProcess.setModifiedById(AuthService.getLoggedInUserId());
        interviewProcess.setName(req.getName());
        interviewProcess.setNotes(req.getNotes());
        interviewProcess.setWorkLocation(req.getWorkLocation());
        if (req.getStatus() == null) 
            interviewProcess.setStatus(0);
        else
            interviewProcess.setStatus(req.getStatus());

        interviewProcess = interviewProcessRepository.save(interviewProcess);

        if (saveRounds && req.getRounds() != null) {
            for (InterviewProcessRound roundTemplate : req.getRounds()) {
                roundTemplate.setIptId(interviewProcess.getId());
                roundTemplate.setInterviewProcess(interviewProcess);
                roundTemplate.setInterviewProcessId(interviewProcess.getId());
                interviewProcessRoundRepository.save(roundTemplate);
            }
        }

        return interviewProcess.getPublicId();
    }

    @Transactional
    public String create(InterviewProcessReq req) {
        Long currentUserId = AuthService.getLoggedInUserId();
        InterviewProcess interviewProcess = new InterviewProcess();
        interviewProcess.setCreatedById(currentUserId);
        return this.save(interviewProcess, req, true);
    }

    @Transactional
    public String update(InterviewProcessReq req) {        
        if (req.getId() == null) 
            throw new InternalServerException("Invalid Interview Process Id: " + req.getId());

        InterviewProcess interviewProcess = interviewProcessRepository.findByPublicId(req.getId())
                .orElseThrow(() -> new InternalServerException("Interview Process not found: " + req.getId()));

        boolean roundsChanged = !roundsEqual(interviewProcess.getRounds(), req.getRounds());
        if (roundsChanged) {
            interviewProcessRoundRepository.deleteByInterviewProcessId(interviewProcess.getId());
        }

        return this.save(interviewProcess, req, roundsChanged);
    }

    private static boolean roundsEqual(List<InterviewProcessRound> existing, List<InterviewProcessRound> incoming) {
        List<InterviewProcessRound> a = existing == null ? List.of() : existing;
        List<InterviewProcessRound> b = incoming == null ? List.of() : incoming;
        if (a.size() != b.size())
            return false;
        for (int i = 0; i < a.size(); i++) {
            InterviewProcessRound x = a.get(i);
            InterviewProcessRound y = b.get(i);
            if (!Objects.equals(x.getOrder(), y.getOrder())
                    || !Objects.equals(x.getRoundType(), y.getRoundType())) {
                return false;
            }
            List<String> xi = x.getInterviewers() == null ? List.of() : x.getInterviewers();
            List<String> yi = y.getInterviewers() == null ? List.of() : y.getInterviewers();
            if (xi.size() != yi.size())
                return false;
            for (int j = 0; j < xi.size(); j++) {
                if (!Objects.equals(String.valueOf(xi.get(j)), String.valueOf(yi.get(j))))
                    return false;
            }
        }
        return true;
    }

    @Transactional
    public void deactivate(String publicId, Integer status) {
        InterviewProcess template = interviewProcessRepository.findByPublicId(publicId)
                .orElseThrow(() -> new InternalServerException("Interview Process not found: " + publicId));
        template.setStatus(status);
        interviewProcessRepository.save(template);
    }

}
