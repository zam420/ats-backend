package com.deepconnect.ats.service;

import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.model.Exam;
import com.deepconnect.ats.model.ExamForward;
import com.deepconnect.ats.repository.ExamForwardRepository;
import com.deepconnect.ats.repository.ExamRepository;
import com.deepconnect.ats.service.auth.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final ExamForwardRepository examForwardRepository;

    public AtsPageRsp<Exam> listExams(AtsPageQuery query) {
        Page<Exam> page = examRepository.findAll(query.toPageable());
        return AtsPageRsp.of(page);
    }

    public List<Exam> listByCandidateResumeId(Long resumeId) {
        return examRepository.findByCandidateResumeId(resumeId);
    }

    public Optional<Exam> getById(Long id) {
        return examRepository.findById(id);
    }

    @Transactional
    public Long createExam(Exam exam) {
        Long currentUserId = AuthService.getLoggedInUserId();
        if (currentUserId == null) currentUserId = 1L;

        exam.setCreatedById(currentUserId);
        if (exam.getStatus() == null) exam.setStatus(0);
        if (exam.getDataStatus() == null) exam.setDataStatus(0);
        return examRepository.save(exam).getId();
    }

    @Transactional
    public void cancelExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new InternalServerException("Exam not found: " + examId));
        exam.setStatus(-1); // cancelled
        examRepository.save(exam);
    }

    @Transactional
    public Long createForward(Long examId, Long candidateResumeId, List<Long> toUserIds) {
        Long currentUserId = AuthService.getLoggedInUserId();
        if (currentUserId == null) currentUserId = 1L;

        Long lastId = null;
        for (Long toUserId : toUserIds) {
            ExamForward forward = ExamForward.builder()
                    .examId(examId)
                    .candidateResumeId(candidateResumeId)
                    .fromUserId(currentUserId)
                    .toUserId(toUserId)
                    .status(0)
                    .dataStatus(0)
                    .build();
            lastId = examForwardRepository.save(forward).getId();
        }
        return lastId;
    }

    public List<ExamForward> listForward(Long resumeId) {
        return examForwardRepository.findByCandidateResumeId(resumeId);
    }

    @Transactional
    public void submitForwardFeedback(Long forwardId, String feedbackNotes) {
        ExamForward forward = examForwardRepository.findById(forwardId)
                .orElseThrow(() -> new InternalServerException("Exam forward not found: " + forwardId));
        forward.setFeedbackNotes(feedbackNotes);
        forward.setStatus(1); // completed
        examForwardRepository.save(forward);
    }

    @Transactional
    public void submitHRFeedback(Long examId, Integer hrFeedback) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new InternalServerException("Exam not found: " + examId));
        exam.setHrFeedback(hrFeedback);
        examRepository.save(exam);
    }
}
