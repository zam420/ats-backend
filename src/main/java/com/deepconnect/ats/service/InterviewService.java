package com.deepconnect.ats.service;

import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.dto.*;
import com.deepconnect.ats.model.*;
import com.deepconnect.ats.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final InterviewRoundRepository interviewRoundRepository;
    private final InterviewCandidateRepository interviewCandidateRepository;
    private final InterviewFeedbackRepository interviewFeedbackRepository;
    private final InterviewProcessTemplateRepository processTemplateRepository;
    private final InterviewRoundTemplateRepository roundTemplateRepository;

    // Process Templates
    public AtsPageRsp<InterviewProcessTemplate> pageProcessTemplates(AtsPageQuery query) {
        Page<InterviewProcessTemplate> page = processTemplateRepository.findAll(query.toPageable());
        return AtsPageRsp.of(page);
    }

    public List<InterviewProcessTemplate> listProcessTemplates() {
        return processTemplateRepository.findByStatus(0);
    }

    public Optional<InterviewProcessTemplate> getProcessTemplateById(Long id) {
        return processTemplateRepository.findById(id);
    }

    @Transactional
    public Long saveProcessTemplate(ProcessTemplateReq req) {
        Long currentUserId = AuthService.getLoggedInUserId();
        if (currentUserId == null) currentUserId = 1L;

        InterviewProcessTemplate template;
        if (req.getId() != null) {
            template = processTemplateRepository.findById(req.getId())
                    .orElseThrow(() -> new InternalServerException("Process template not found: " + req.getId()));
            template.setModifiedById(currentUserId);
        } else {
            template = new InterviewProcessTemplate();
            template.setCreatedById(currentUserId);
            template.setModifiedById(currentUserId);
        }

        if (req.getName() != null) template.setName(req.getName());
        if (req.getWorkLocation() != null) template.setWorkLocation(req.getWorkLocation());
        if (req.getNotes() != null) template.setNotes(req.getNotes());
        if (req.getStatus() != null) template.setStatus(req.getStatus());
        else if (template.getStatus() == null) template.setStatus(0);

        InterviewProcessTemplate saved = processTemplateRepository.save(template);

        if (req.getRounds() != null) {
            for (InterviewRoundTemplate round : req.getRounds()) {
                round.setIptId(saved.getId());
                roundTemplateRepository.save(round);
            }
        }

        return saved.getId();
    }

    @Transactional
    public void toggleProcessTemplateStatus(Long id, Integer status) {
        InterviewProcessTemplate template = processTemplateRepository.findById(id)
                .orElseThrow(() -> new InternalServerException("Template not found: " + id));
        template.setStatus(status);
        processTemplateRepository.save(template);
    }

    // Interviews
    @Transactional
    public Long setupInterview(InterviewSetupReq req) {
        Long currentUserId = AuthService.getLoggedInUserId();
        if (currentUserId == null) currentUserId = 1L;

        Interview interview = Interview.builder()
                .targetJobId(req.getTargetJobId())
                .interviewType(req.getInterviewType())
                .interviewTemplateId(req.getInterviewTemplateId())
                .interviewStatus(0)
                .createdById(currentUserId)
                .modifiedById(currentUserId)
                .build();
        Interview savedInterview = interviewRepository.save(interview);

        // Create initial round
        InterviewRound round = InterviewRound.builder()
                .interviewId(savedInterview.getId())
                .roundCode("ROUND_1")
                .order(1)
                .roundType(0)
                .createdById(currentUserId)
                .modifiedById(currentUserId)
                .status(0)
                .build();
        InterviewRound savedRound = interviewRoundRepository.save(round);

        // Associate candidates
        if (req.getCandidateResumeIds() != null) {
            for (Long resumeId : req.getCandidateResumeIds()) {
                InterviewCandidate candidate = InterviewCandidate.builder()
                        .roundId(savedRound.getId())
                        .candidateResumeId(resumeId)
                        .status(0)
                        .build();
                interviewCandidateRepository.save(candidate);
            }
        }

        return savedInterview.getId();
    }

    @Transactional
    public void submitInterview(InterviewSubmitReq req) {
        Long currentUserId = AuthService.getLoggedInUserId();
        if (currentUserId == null) currentUserId = 1L;

        InterviewRound round = interviewRoundRepository.findById(req.getRoundId())
                .orElseThrow(() -> new InternalServerException("Interview round not found: " + req.getRoundId()));

        if (req.getFromTime() != null) round.setFromTime(req.getFromTime());
        if (req.getToTime() != null) round.setToTime(req.getToTime());
        if (req.getIsOnline() != null) round.setIsOnline(req.getIsOnline());
        if (req.getInterviewLocation() != null) round.setInterviewLocation(req.getInterviewLocation());
        if (req.getInterviewers() != null) round.setInterviewers(req.getInterviewers());
        if (req.getTeamsMeeting() != null) round.setTeamsMeeting(req.getTeamsMeeting());
        if (req.getEmailTemplateId() != null) round.setEmailTemplateId(req.getEmailTemplateId());
        round.setModifiedById(currentUserId);
        round.setStatus(1); // scheduled

        interviewRoundRepository.save(round);
    }

    @Transactional
    public void cancelInterview(Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new InternalServerException("Interview not found: " + interviewId));
        interview.setInterviewStatus(-1); // cancelled
        interviewRepository.save(interview);
    }

    public InterviewDetailRsp getInterviewDetail(Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new InternalServerException("Interview not found: " + interviewId));

        List<InterviewRound> rounds = interviewRoundRepository.findByInterviewIdOrderByOrderAsc(interviewId);
        List<InterviewCandidate> candidates = new ArrayList<>();
        for (InterviewRound r : rounds) {
            candidates.addAll(interviewCandidateRepository.findByRoundId(r.getId()));
        }

        return InterviewDetailRsp.builder()
                .interview(interview)
                .rounds(rounds)
                .candidates(candidates)
                .build();
    }

    // Feedback
    @Transactional
    public Long submitFeedback(InterviewFeedbackReq req) {
        Long currentUserId = AuthService.getLoggedInUserId();
        if (currentUserId == null) currentUserId = 1L;

        InterviewFeedback feedback = interviewFeedbackRepository.findByInterviewCandidateId(req.getInterviewCandidateId())
                .orElse(InterviewFeedback.builder()
                        .interviewCandidateId(req.getInterviewCandidateId())
                        .interviewRoundId(req.getInterviewRoundId())
                        .createdById(currentUserId)
                        .modifiedById(currentUserId)
                        .build());

        feedback.setInterviewResult(req.getInterviewResult());
        feedback.setRoundSpecificFeedback(req.getRoundSpecificFeedback());
        feedback.setModifiedById(currentUserId);

        return interviewFeedbackRepository.save(feedback).getId();
    }

    public InterviewFeedback getFeedbackDetail(Long id) {
        return interviewFeedbackRepository.findById(id).orElse(null);
    }

    public List<InterviewFeedback> listFeedbackByResume(Long candidateResumeId) {
        List<InterviewCandidate> candidates = interviewCandidateRepository.findByCandidateResumeId(candidateResumeId);
        List<InterviewFeedback> feedbacks = new ArrayList<>();
        for (InterviewCandidate ic : candidates) {
            interviewFeedbackRepository.findByInterviewCandidateId(ic.getId()).ifPresent(feedbacks::add);
        }
        return feedbacks;
    }
}
