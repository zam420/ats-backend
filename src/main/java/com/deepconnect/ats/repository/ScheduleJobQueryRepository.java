package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.CandidateEducationHistory;
import com.deepconnect.ats.model.CandidateProjectExperience;
import com.deepconnect.ats.model.CandidateResume;
import com.deepconnect.ats.model.CandidateWorkExperience;
import com.deepconnect.ats.model.Exam;
import com.deepconnect.ats.model.InterviewCandidate;
import com.deepconnect.ats.model.InterviewRound;
import com.deepconnect.ats.model.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/** Queries needed by the schedule jobs (candidate resume / interview time / meeting response). */
@Repository
public class ScheduleJobQueryRepository {

    private static final int IN_CLAUSE_BATCH = 1000;

    @PersistenceContext
    private EntityManager em;

    private final JdbcTemplate jdbcTemplate;

    public ScheduleJobQueryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void detach(Object entity) {
        em.detach(entity);
    }

    // ---- candidate resume ----

    public List<CandidateResume> findResumesByStatus(int status, Integer subStatus) {
        if (subStatus == null) {
            return em.createQuery("select r from CandidateResume r where r.status = :status", CandidateResume.class)
                    .setParameter("status", status).getResultList();
        }
        return em.createQuery("select r from CandidateResume r where r.status = :status and r.subStatus = :sub",
                        CandidateResume.class)
                .setParameter("status", status).setParameter("sub", subStatus).getResultList();
    }

    public long countResumesUpdatedAfter(LocalDateTime timeFrom) {
        if (timeFrom == null) {
            return em.createQuery("select count(r) from CandidateResume r", Long.class).getSingleResult();
        }
        return em.createQuery("select count(r) from CandidateResume r where r.updatedAt > :t", Long.class)
                .setParameter("t", timeFrom).getSingleResult();
    }

    public List<CandidateResume> findResumesUpdatedAfter(LocalDateTime timeFrom, int offset, int limit) {
        TypedQuery<CandidateResume> q;
        if (timeFrom == null) {
            q = em.createQuery("select r from CandidateResume r order by r.updatedAt desc, r.id desc",
                    CandidateResume.class);
        } else {
            q = em.createQuery("select r from CandidateResume r where r.updatedAt > :t order by r.updatedAt desc, r.id desc",
                    CandidateResume.class).setParameter("t", timeFrom);
        }
        return q.setFirstResult(offset).setMaxResults(limit).getResultList();
    }

    public List<CandidateEducationHistory> findEducationByResumeIds(Collection<Long> resumeIds) {
        return findByResumeIds("CandidateEducationHistory", CandidateEducationHistory.class, resumeIds);
    }

    public List<CandidateWorkExperience> findWorkByResumeIds(Collection<Long> resumeIds) {
        return findByResumeIds("CandidateWorkExperience", CandidateWorkExperience.class, resumeIds);
    }

    public List<CandidateProjectExperience> findProjectByResumeIds(Collection<Long> resumeIds) {
        return findByResumeIds("CandidateProjectExperience", CandidateProjectExperience.class, resumeIds);
    }

    private <T> List<T> findByResumeIds(String entity, Class<T> type, Collection<Long> resumeIds) {
        if (resumeIds.isEmpty()) {
            return List.of();
        }
        return em.createQuery("select e from " + entity + " e where e.candidateResumeId in :ids and e.dataStatus <> 2", type)
                .setParameter("ids", resumeIds).getResultList();
    }

    // ---- tags ----

    /** tag_service.get_by_update_time_from: updated_at > from, newest first. */
    public List<Tag> findTagsUpdatedAfter(LocalDateTime timeFrom) {
        if (timeFrom == null) {
            return em.createQuery("select t from Tag t order by t.updatedAt desc", Tag.class).getResultList();
        }
        return em.createQuery("select t from Tag t where t.updatedAt > :t order by t.updatedAt desc", Tag.class)
                .setParameter("t", timeFrom).getResultList();
    }

    public List<Tag> findAllTags() {
        return em.createQuery("select t from Tag t", Tag.class).getResultList();
    }

    /** Django bulk_create(update_conflicts=True, update_fields=[match_type, match_count]). */
    public void upsertResumeTags(List<Object[]> rows) {
        // each row: candidate_resume_id, tag_id, match_type, match_count
        jdbcTemplate.batchUpdate(
                "INSERT INTO ats_resume_tags (candidate_resume_id, tag_id, match_type, match_count) VALUES (?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE match_type = VALUES(match_type), match_count = VALUES(match_count)",
                rows);
    }

    // ---- exam ----

    /** exam_service.get_latest_exam_by_resume_id: data_status = SUBMITTED(1), newest id first. */
    public Exam findLatestSubmittedExam(Long resumeId) {
        List<Exam> exams = em.createQuery(
                        "select e from Exam e where e.dataStatus = 1 and e.candidateResumeId = :rid order by e.id desc",
                        Exam.class)
                .setParameter("rid", resumeId).setMaxResults(1).getResultList();
        return exams.isEmpty() ? null : exams.get(0);
    }

    // ---- interview ----

    public List<InterviewRound> findRoundsByStatus(Collection<Integer> statuses) {
        return em.createQuery("select r from InterviewRound r where r.status in :s", InterviewRound.class)
                .setParameter("s", statuses).getResultList();
    }

    public List<InterviewCandidate> findInterviewCandidatesByRoundsAndStatus(Collection<Long> roundIds,
                                                                             Collection<Integer> statuses) {
        if (roundIds.isEmpty()) {
            return List.of();
        }
        return em.createQuery("select c from InterviewCandidate c where c.status in :s and c.roundId in :r",
                        InterviewCandidate.class)
                .setParameter("s", statuses).setParameter("r", roundIds).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<InterviewRound> findRoundsByTeamsMeetingIds(List<String> meetingIds) {
        if (meetingIds.isEmpty()) {
            return List.of();
        }
        List<InterviewRound> result = new java.util.ArrayList<>();
        for (int i = 0; i < meetingIds.size(); i += IN_CLAUSE_BATCH) {
            List<String> part = meetingIds.subList(i, Math.min(i + IN_CLAUSE_BATCH, meetingIds.size()));
            result.addAll(em.createNativeQuery(
                            "SELECT * FROM ats_interview_rounds WHERE teams_meeting ->> '$.meeting_id' IN (:ids)",
                            InterviewRound.class)
                    .setParameter("ids", part).getResultList());
        }
        return result;
    }
}
