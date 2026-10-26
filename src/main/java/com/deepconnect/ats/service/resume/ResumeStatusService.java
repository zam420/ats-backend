package com.deepconnect.ats.service.resume;

import com.deepconnect.ats.common.constant.enums.CandidateStatusCodes;
import com.deepconnect.ats.common.constant.enums.ResumeOperationType;
import com.deepconnect.ats.common.utils.TimeUtil;
import com.deepconnect.ats.model.CandidateResume;
import com.deepconnect.ats.model.ResumeOperationRecord;
import com.deepconnect.ats.user.model.User;
import com.deepconnect.ats.repository.CandidateResumeRepository;
import com.deepconnect.ats.repository.ResumeOperationRecordRepository;
import com.deepconnect.ats.repository.ScheduleJobQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Subset of Django candidate_resume_service needed by the schedule jobs. */
@Service
@RequiredArgsConstructor
public class ResumeStatusService {

    private final ScheduleJobQueryRepository queryRepository;
    private final CandidateStatusCalculator calculator;
    private final CandidateResumeRepository candidateResumeRepository;
    private final ResumeOperationRecordRepository operationRecordRepository;

    public List<CandidateResume> getResumeByStatus(int status, Integer subStatus) {
        return queryRepository.findResumesByStatus(status, subStatus);
    }

    @SuppressWarnings("unchecked")
    public void updateResumeStatusByOperate(User operator, List<CandidateResume> resumes, ResumeOperation operation) {
        if (resumes == null || resumes.isEmpty()) {
            return;
        }
        List<ResumeOperationRecord> records = new ArrayList<>();
        for (CandidateResume resume : resumes) {
            Object args = operation.getOperationArgs();
            CandidateStatusCalculator.StatusResult result = calculator.calculateStatusByOperation(operation);
            Map<String, Object> operateArgs = args instanceof Map<?, ?> m ? (Map<String, Object>) m : null;
            records.add(ResumeOperationRecord.builder()
                    .candidateResumeId(resume.getId())
                    .operatorId(operator.getId())
                    .operationType(operation.getOperationType().getValue())
                    .operateArgs(operateArgs)
                    .operateAt(TimeUtil.now())
                    .build());
            resume.setStatus(result.status());
            resume.setSubStatus(result.subStatus() == null ? null : result.subStatus().getCode());
            processManagementStatus(resume, operation);
        }
        operationRecordRepository.saveAll(records);
        candidateResumeRepository.saveAll(resumes);
    }

    public void processManagementStatus(CandidateResume resume, ResumeOperation operation) {
        ResumeOperationType type = operation == null ? null : operation.getOperationType();
        if (type == ResumeOperationType.MOVE_TO || type == ResumeOperationType.SCHEDULED_INTERVIEW) {
            resume.setManagementStatus(resume.getStatus());
            resume.setManagementSubStatus(resume.getSubStatus());
        } else if (type == ResumeOperationType.FORWARD) {
            Integer ms = resume.getManagementStatus();
            if (ms != null && ms == CandidateStatusCodes.UN_SCREENED) {
                resume.setManagementStatus(resume.getStatus());
                resume.setManagementSubStatus(resume.getSubStatus());
            } else if (ms != null && ms.equals(resume.getStatus())) {
                resume.setManagementSubStatus(resume.getSubStatus());
            }
        } else if (resume.getManagementStatus() != null && resume.getManagementStatus().equals(resume.getStatus())) {
            resume.setManagementSubStatus(resume.getSubStatus());
        }
    }

    /** generate_candidate_resume_aggregates for a page of resumes ordered by updated_at desc. */
    public List<CandidateResumeAggregate> buildAggregates(List<CandidateResume> resumes) {
        List<Long> ids = resumes.stream().map(CandidateResume::getId).toList();
        var edu = queryRepository.findEducationByResumeIds(ids);
        var work = queryRepository.findWorkByResumeIds(ids);
        var project = queryRepository.findProjectByResumeIds(ids);
        List<CandidateResumeAggregate> result = new ArrayList<>();
        for (CandidateResume r : resumes) {
            result.add(new CandidateResumeAggregate(r,
                    edu.stream().filter(e -> r.getId().equals(e.getCandidateResumeId())).toList(),
                    work.stream().filter(e -> r.getId().equals(e.getCandidateResumeId())).toList(),
                    project.stream().filter(e -> r.getId().equals(e.getCandidateResumeId())).toList()));
        }
        return result;
    }
}
