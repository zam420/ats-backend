package com.deepconnect.ats.interviewplan.service;
import org.springframework.transaction.annotation.Transactional;
import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.interviewplan.dto.InterviewPlanReq;
import com.deepconnect.ats.interviewplan.model.InterviewPlan;
import com.deepconnect.ats.interviewplan.repo.InterviewProcessRepository;
import com.deepconnect.ats.dto.AtsPageQueryWithStatus;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.interviewplan.dto.InterviewPlanRes;
import com.deepconnect.ats.interviewplan.dto.InterviewPlanDetailsRes;
import com.deepconnect.ats.interviewplan.model.InterviewPlanRound;
import com.deepconnect.ats.interviewplan.repo.InterviewProcessRoundRepository;

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
public class InterviewPlanService {

    private final InterviewProcessRepository interviewProcessRepository;
    private final InterviewProcessRoundRepository interviewProcessRoundRepository;

    private String save(InterviewPlan interviewPlan, InterviewPlanReq req, boolean saveRounds) {
        interviewPlan.setModifiedByUserId(AuthService.getLoggedInUserId());
        interviewPlan.setName(req.getName());
        interviewPlan.setNotes(req.getNotes());
        interviewPlan.setWorkLocation(req.getWorkLocation());
        if (req.getStatus() == null) 
            interviewPlan.setStatus(0);
        else
            interviewPlan.setStatus(req.getStatus());

        interviewPlan = interviewProcessRepository.save(interviewPlan);

        if (saveRounds && req.getRounds() != null) {
            for (InterviewPlanRound roundTemplate : req.getRounds()) {
                roundTemplate.setInterviewPlan(interviewPlan);
                roundTemplate.setInterviewPlanId(interviewPlan.getId());
                interviewProcessRoundRepository.save(roundTemplate);
            }
        }

        return interviewPlan.getPublicId();
    }

    @Transactional
    public String create(InterviewPlanReq req) {
        Long currentUserId = AuthService.getLoggedInUserId();
        InterviewPlan interviewPlan = new InterviewPlan();
        interviewPlan.setCreatedByUserId(currentUserId);
        return this.save(interviewPlan, req, true);
    }

    @Transactional
    public String update(InterviewPlanReq req) {        
        if (req.getId() == null) 
            throw new InternalServerException("Invalid Interview Process Id: " + req.getId());

        InterviewPlan interviewPlan = interviewProcessRepository.findByPublicId(req.getId())
                .orElseThrow(() -> new InternalServerException("Interview Process not found: " + req.getId()));

        boolean roundsChanged = !roundsEqual(interviewPlan.getRounds(), req.getRounds());
        if (roundsChanged) {
            interviewProcessRoundRepository.deleteByInterviewPlanId(interviewPlan.getId());
        }

        return this.save(interviewPlan, req, roundsChanged);
    }

    private static boolean roundsEqual(List<InterviewPlanRound> existing, List<InterviewPlanRound> incoming) {
        List<InterviewPlanRound> a = existing == null ? List.of() : existing;
        List<InterviewPlanRound> b = incoming == null ? List.of() : incoming;
        if (a.size() != b.size())
            return false;
        for (int i = 0; i < a.size(); i++) {
            InterviewPlanRound x = a.get(i);
            InterviewPlanRound y = b.get(i);
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
        InterviewPlan template = interviewProcessRepository.findByPublicId(publicId)
                .orElseThrow(() -> new InternalServerException("Interview Process not found: " + publicId));
        template.setStatus(status);
        interviewProcessRepository.save(template);
    }

    @Transactional(readOnly = true)
    public Optional<InterviewPlanDetailsRes> findByPublicId(String publicId) {
        return interviewProcessRepository.findByPublicId(publicId).map(InterviewPlanDetailsRes::from);
    }

    public AtsPageRsp<InterviewPlanRes> getAllByPage(AtsPageQueryWithStatus query) {
        Page<InterviewPlan> page;
        if(query.getStatus() == null) {
            page = interviewProcessRepository.findAll(query.toPageable());
        } else {
            page = interviewProcessRepository.findByStatus(query.getStatus(), query.toPageable());
        }
        return AtsPageRsp.of(page.map(InterviewPlanRes::from));
    }

}
