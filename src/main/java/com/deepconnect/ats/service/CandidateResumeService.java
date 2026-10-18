package com.deepconnect.ats.service;

import com.deepconnect.ats.common.constant.enums.ResumeOperationType;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.dto.*;
import com.deepconnect.ats.model.*;
import com.deepconnect.ats.repository.*;
import com.deepconnect.ats.service.auth.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateResumeService {

    private final CandidateRepository candidateRepository;
    private final CandidateResumeRepository candidateResumeRepository;
    private final CandidateWorkExperienceRepository workExperienceRepository;
    private final CandidateEducationHistoryRepository educationHistoryRepository;
    private final CandidateProjectExperienceRepository projectExperienceRepository;
    private final CandidateResumeOfferRepository offerRepository;
    private final CandidateResumeForwardRepository forwardRepository;
    private final ResumeCommentRepository commentRepository;
    private final ResumeTagRepository resumeTagRepository;
    private final ResumeOperationRecordRepository operationRecordRepository;
    private final ResumeViewRecordRepository viewRecordRepository;
    private final JobPositionRepository jobPositionRepository;

    public AtsPageRsp<CandidateResume> pageCandidateResume(AtsPageQuery query) {
        Page<CandidateResume> page = candidateResumeRepository.findByDataStatus(0, query.toPageable());
        return AtsPageRsp.of(page);
    }

    public CandidateResumeDetailRsp getCandidateDetailById(Long resumeId) {
        CandidateResume resume = candidateResumeRepository.findById(resumeId)
                .orElseThrow(() -> new InternalServerException("Candidate resume not found: " + resumeId));

        Candidate candidate = candidateRepository.findById(resume.getCandidateId()).orElse(null);
        JobPosition position = resume.getApplyingPositionId() != null ?
                jobPositionRepository.findById(resume.getApplyingPositionId()).orElse(null) : null;
        CandidateResumeOffer offer = offerRepository.findByCandidateResumeId(resumeId).orElse(null);

        List<CandidateWorkExperience> workExperiences = workExperienceRepository.findByCandidateResumeId(resumeId);
        List<CandidateEducationHistory> educationHistories = educationHistoryRepository.findByCandidateResumeId(resumeId);
        List<CandidateProjectExperience> projectExperiences = projectExperienceRepository.findByCandidateResumeId(resumeId);
        List<ResumeComment> comments = commentRepository.findByCandidateResumeIdOrderByCommentAtDesc(resumeId);
        List<ResumeTag> tags = resumeTagRepository.findByCandidateResumeId(resumeId);

        return CandidateResumeDetailRsp.builder()
                .candidateResume(resume)
                .candidate(candidate)
                .position(position)
                .offer(offer)
                .workExperiences(workExperiences)
                .educationHistories(educationHistories)
                .projectExperiences(projectExperiences)
                .comments(comments)
                .tags(tags)
                .build();
    }

    @Transactional
    public Long saveCandidateResume(CandidateResumeSaveReq req) {
        Long rawUserId = AuthService.getLoggedInUserId();
        final Long currentUserId = rawUserId != null ? rawUserId : 1L;

        CandidateResume resumeReq = req.getCandidateResume();
        if (resumeReq == null) throw new InternalServerException("Candidate resume payload is missing");

        Candidate candidate;
        if (resumeReq.getCandidateId() != null) {
            candidate = candidateRepository.findById(resumeReq.getCandidateId())
                    .orElseGet(() -> createCandidateFromResume(resumeReq, currentUserId));
        } else {
            candidate = createCandidateFromResume(resumeReq, currentUserId);
        }
        candidate = candidateRepository.save(candidate);

        resumeReq.setCandidateId(candidate.getId());
        resumeReq.setCreateById(currentUserId);
        if (resumeReq.getAppliedAt() == null) {
            resumeReq.setAppliedAt(LocalDateTime.now());
        }
        if (resumeReq.getStatus() == null) {
            resumeReq.setStatus(0);
        }
        if (resumeReq.getDataStatus() == null) {
            resumeReq.setDataStatus(0);
        }

        CandidateResume savedResume = candidateResumeRepository.save(resumeReq);
        Long resumeId = savedResume.getId();

        // Save work experiences
        if (req.getWorkExperiences() != null) {
            workExperienceRepository.deleteByCandidateResumeId(resumeId);
            for (CandidateWorkExperience exp : req.getWorkExperiences()) {
                exp.setCandidateId(candidate.getId());
                exp.setCandidateResumeId(resumeId);
                workExperienceRepository.save(exp);
            }
        }

        // Save education histories
        if (req.getEducationHistories() != null) {
            educationHistoryRepository.deleteByCandidateResumeId(resumeId);
            for (CandidateEducationHistory edu : req.getEducationHistories()) {
                edu.setCandidateId(candidate.getId());
                edu.setCandidateResumeId(resumeId);
                educationHistoryRepository.save(edu);
            }
        }

        // Save project experiences
        if (req.getProjectExperiences() != null) {
            projectExperienceRepository.deleteByCandidateResumeId(resumeId);
            for (CandidateProjectExperience proj : req.getProjectExperiences()) {
                proj.setCandidateId(candidate.getId());
                proj.setCandidateResumeId(resumeId);
                projectExperienceRepository.save(proj);
            }
        }

        // Record operation
        recordOperation(resumeId, currentUserId, ResumeOperationType.MOVE_TO.getValue(),
                Collections.singletonMap("action", "save_candidate_resume"));

        return resumeId;
    }

    private Candidate createCandidateFromResume(CandidateResume r, Long userId) {
        return Candidate.builder()
                .name(r.getName())
                .gender(r.getGender())
                .birthDate(r.getBirthDate())
                .age(r.getAge())
                .phoneNumber(r.getPhoneNumber())
                .phoneCountryCode(r.getPhoneCountryCode())
                .email(r.getEmail())
                .currentPlace(r.getCurrentPlace())
                .nativePlaceProvince(r.getNativePlaceProvince())
                .nativePlaceCity(r.getNativePlaceCity())
                .employmentStatus(r.getEmploymentStatus())
                .expectedWorkLocation(r.getExpectedWorkLocation())
                .expectedSalary(r.getExpectedSalary())
                .skills(r.getSkills())
                .languages(r.getLanguages())
                .selfAssessment(r.getSelfAssessment())
                .originalResume(r.getOriginalResume())
                .attachments(r.getAttachments())
                .attachmentLinks(r.getAttachmentLinks())
                .notes(r.getNotes())
                .highestDegree(r.getHighestDegree())
                .dataStatus(0)
                .createById(userId)
                .build();
    }

    @Transactional
    public void quickSaveResume(QuickSaveResumeReq req) {
        CandidateResume resume = candidateResumeRepository.findById(req.getId())
                .orElseThrow(() -> new InternalServerException("Candidate resume not found: " + req.getId()));
        if (req.getNotes() != null) resume.setNotes(req.getNotes());
        if (req.getPrivacyNotes() != null) resume.setPrivacyNotes(req.getPrivacyNotes());
        candidateResumeRepository.save(resume);
    }

    @Transactional
    public void deleteCandidateResume(Long id) {
        CandidateResume resume = candidateResumeRepository.findById(id)
                .orElseThrow(() -> new InternalServerException("Candidate resume not found: " + id));
        resume.setDataStatus(1); // 1 = deleted
        candidateResumeRepository.save(resume);
    }

    @Transactional
    public void moveTo(MoveToReq req) {
        Long currentUserId = AuthService.getLoggedInUserId();
        if (currentUserId == null) currentUserId = 1L;

        if (req.getResumeIds() == null || req.getResumeIds().isEmpty()) return;

        List<CandidateResume> list = candidateResumeRepository.findAllById(req.getResumeIds());
        for (CandidateResume r : list) {
            if (req.getTargetStatus() != null) r.setStatus(req.getTargetStatus());
            if (req.getTargetSubStatus() != null) r.setSubStatus(req.getTargetSubStatus());
            if (req.getTargetPositionId() != null) r.setApplyingPositionId(req.getTargetPositionId());
            candidateResumeRepository.save(r);

            Map<String, Object> args = new HashMap<>();
            args.put("targetStatus", req.getTargetStatus());
            args.put("targetSubStatus", req.getTargetSubStatus());
            recordOperation(r.getId(), currentUserId, ResumeOperationType.MOVE_TO.getValue(), args);
        }
    }

    @Transactional
    public void forward(ForwardResumeReq req) {
        Long currentUserId = AuthService.getLoggedInUserId();
        if (currentUserId == null) currentUserId = 1L;

        if (req.getResumeIds() == null || req.getToUserIds() == null) return;

        for (Long resumeId : req.getResumeIds()) {
            for (Long toUserId : req.getToUserIds()) {
                CandidateResumeForward forward = CandidateResumeForward.builder()
                        .candidateResumeId(resumeId)
                        .toUserId(toUserId)
                        .fromUserId(currentUserId)
                        .createById(currentUserId)
                        .updateById(currentUserId)
                        .status(0)
                        .forwardNotes(req.getForwardNotes())
                        .resumePositionId(req.getPositionId())
                        .build();
                forwardRepository.save(forward);

                recordOperation(resumeId, currentUserId, ResumeOperationType.FORWARD.getValue(),
                        Collections.singletonMap("toUserId", toUserId));
            }
        }
    }

    @Transactional
    public void feedbackForward(FeedbackForwardReq req) {
        CandidateResumeForward forward = forwardRepository.findById(req.getForwardId())
                .orElseThrow(() -> new InternalServerException("Forward not found: " + req.getForwardId()));
        forward.setStatus(req.getStatus());
        forward.setFeedbackNotes(req.getFeedbackNotes());
        forwardRepository.save(forward);

        Long currentUserId = AuthService.getLoggedInUserId();
        if (currentUserId == null) currentUserId = 1L;

        recordOperation(forward.getCandidateResumeId(), currentUserId,
                ResumeOperationType.FORWARD_FEEDBACK.getValue(),
                Collections.singletonMap("feedbackNotes", req.getFeedbackNotes()));
    }

    public List<CandidateResumeForward> listForward(Long resumeId, Integer status, Integer forwardType) {
        List<CandidateResumeForward> list;
        if (resumeId != null) {
            list = forwardRepository.findByCandidateResumeId(resumeId);
        } else {
            Long userId = AuthService.getLoggedInUserId();
            if (userId == null) {
                return new ArrayList<>();
            }
            // forward_type 0: shared by me, 1: shared to me
            List<CandidateResumeForward> acc = new ArrayList<>();
            if (forwardType == null || forwardType == 0) {
                acc.addAll(forwardRepository.findByFromUserId(userId));
            }
            if (forwardType == null || forwardType == 1) {
                for (CandidateResumeForward f : forwardRepository.findByToUserId(userId)) {
                    if (!acc.contains(f)) acc.add(f);
                }
            }
            list = acc;
        }
        if (status != null) {
            List<CandidateResumeForward> src = list;
            list = src.stream().filter(f -> status.equals(f.getStatus())).collect(java.util.stream.Collectors.toList());
        }
        return list;
    }

    public Map<String, Long> statusCount() {
        Map<String, Long> counts = new HashMap<>();
        List<CandidateResume> list = candidateResumeRepository.findAll();
        for (CandidateResume r : list) {
            String key = String.valueOf(r.getStatus());
            counts.put(key, counts.getOrDefault(key, 0L) + 1);
        }
        return counts;
    }

    @Transactional
    public Long commentResume(ResumeCommentReq req) {
        Long currentUserId = AuthService.getLoggedInUserId();
        if (currentUserId == null) currentUserId = 1L;

        ResumeComment comment = ResumeComment.builder()
                .candidateResumeId(req.getCandidateResumeId())
                .commentatorId(currentUserId)
                .comment(req.getComment())
                .shareTo(req.getShareTo())
                .build();
        return commentRepository.save(comment).getId();
    }

    @Transactional
    public void commentResumeInBulk(ResumeCommentReq req) {
        if (req.getCandidateResumeIds() == null) return;
        Long currentUserId = AuthService.getLoggedInUserId();
        if (currentUserId == null) currentUserId = 1L;

        for (Long resumeId : req.getCandidateResumeIds()) {
            ResumeComment comment = ResumeComment.builder()
                    .candidateResumeId(resumeId)
                    .commentatorId(currentUserId)
                    .comment(req.getComment())
                    .shareTo(req.getShareTo())
                    .build();
            commentRepository.save(comment);
        }
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public void createOffer(OfferReq req) {
        CandidateResumeOffer offer = CandidateResumeOffer.builder()
                .candidateResumeId(req.getCandidateResumeId())
                .feedbackStatus(req.getFeedbackStatus())
                .build();
        offerRepository.save(offer);

        Long currentUserId = AuthService.getLoggedInUserId();
        if (currentUserId == null) currentUserId = 1L;
        recordOperation(req.getCandidateResumeId(), currentUserId,
                ResumeOperationType.SENT_OFFER.getValue(), Collections.emptyMap());
    }

    @Transactional
    public void editOffer(OfferReq req) {
        CandidateResumeOffer offer = offerRepository.findByCandidateResumeId(req.getCandidateResumeId())
                .orElseThrow(() -> new InternalServerException("Offer not found for resume: " + req.getCandidateResumeId()));
        offer.setFeedbackStatus(req.getFeedbackStatus());
        offerRepository.save(offer);
    }

    @Transactional
    public void recordView(Long resumeId) {
        Long currentUserId = AuthService.getLoggedInUserId();
        if (currentUserId == null) currentUserId = 1L;

        Optional<ResumeViewRecord> existing = viewRecordRepository.findByCandidateResumeIdAndViewBy(resumeId, currentUserId);
        if (existing.isEmpty()) {
            viewRecordRepository.save(ResumeViewRecord.builder()
                    .candidateResumeId(resumeId)
                    .viewBy(currentUserId)
                    .build());
        }
    }

    public List<ResumeOperationRecord> listHistoricalOperations(Long resumeId) {
        return operationRecordRepository.findByCandidateResumeIdOrderByOperateAtDesc(resumeId);
    }

    private void recordOperation(Long resumeId, Long operatorId, Integer opType, Map<String, Object> args) {
        operationRecordRepository.save(ResumeOperationRecord.builder()
                .candidateResumeId(resumeId)
                .operatorId(operatorId)
                .operationType(opType)
                .operateArgs(args)
                .build());
    }
}
