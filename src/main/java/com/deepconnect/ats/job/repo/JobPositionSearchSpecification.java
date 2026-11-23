package com.deepconnect.ats.job.repo;

import com.deepconnect.ats.job.dto.JobPositionSearchQuery;
import com.deepconnect.ats.job.enums.JobStatus;
import com.deepconnect.ats.job.enums.JobPostedDateType;
import com.deepconnect.ats.job.enums.JobEmploymentType;
import com.deepconnect.ats.job.enums.JobWorkArrangementType;
import com.deepconnect.ats.job.enums.JobExperienceLevelType;
import com.deepconnect.ats.common.enums.LocalizedBaseEnum;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.jobapplication.dto.JobApplicationSearchQuery;
import com.deepconnect.ats.jobapplication.model.JobApplication;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public final class JobPositionSearchSpecification {
    private JobPositionSearchSpecification() {}

    public static Specification<JobPosition> forTenantUser(JobPositionSearchQuery search, Long tenantId,
            Long userId, String userPublicId, boolean admin) {
        return forUser(search, tenantId, userId, userPublicId, admin, false);
    }

    public static Specification<JobPosition> forCandidate(JobPositionSearchQuery search) {
        return forUser(search, null, null, null, false, true);
    }

    public static Specification<JobApplication> forApplications(JobApplicationSearchQuery search, Long candidateUserId) {
        return (root, query, cb) -> {
            HibernateCriteriaBuilder hcb = (HibernateCriteriaBuilder) cb;
            From<JobApplication, JobPosition> position = root.join("jobPosition");
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("candidateUserId"), candidateUserId));
            addJobFilters(predicates, position, search, hcb);
            if (search.getCompanyId() != null && !search.getCompanyId().isBlank()) {
                predicates.add(cb.equal(position.get("tenant").get("publicId"), search.getCompanyId()));
            }
            if (search.getJobAppliedDateType() != null) {
                JobPostedDateType type = Arrays.stream(JobPostedDateType.values())
                    .filter(option -> option.getId().equals(search.getJobAppliedDateType()))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("Invalid date applied filter"));
                LocalDateTime now = LocalDateTime.now();
                predicates.add(cb.between(root.get("createdAt"), type.cutoff(now), now));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static Specification<JobPosition> forUser(JobPositionSearchQuery search, Long tenantId,
            Long userId, String userPublicId, boolean admin, boolean isForCandidate) {
        return (root, query, cb) -> {
            HibernateCriteriaBuilder hcb = (HibernateCriteriaBuilder) cb;
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isFalse(root.get("deactivated")));

            if(isForCandidate) {
                predicates.add(cb.equal(root.get("status"), JobStatus.OPEN));
                if (search.getCompanyId() != null && !search.getCompanyId().isBlank()) {
                    predicates.add(cb.equal(root.get("tenant").get("publicId"), search.getCompanyId()));
                }
            }
            else {
                predicates.add(cb.equal(root.get("tenantId"), tenantId));

                if (!admin) {
                    predicates.add(cb.or(
                        cb.equal(root.get("ownerId"), userId),
                        cb.equal(cb.function("ats_json_member", Integer.class, root.get("recruiters"), hcb.value(userPublicId)), 1),
                        cb.equal(cb.function("ats_json_member", Integer.class, root.get("hiringManagers"), hcb.value(userPublicId)), 1)
                    ));
                }

                if (search.getJobStatuses() != null && !search.getJobStatuses().isEmpty()) {
                    List<JobStatus> statuses = search.getJobStatuses().stream()
                        .map(id -> Arrays.stream(JobStatus.values())
                            .filter(status -> status.getId().equals(id))
                            .findFirst().orElseThrow(() -> new IllegalArgumentException("Invalid job status: " + id)))
                        .toList();
                    predicates.add(root.get("status").in(statuses));
                }
            }
            
            addJobFilters(predicates, root, search, hcb);
            if (search.getJobPostedDateType() != null) {
                JobPostedDateType type = Arrays.stream(JobPostedDateType.values())
                    .filter(option -> option.getId().equals(search.getJobPostedDateType()))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("Invalid date posted filter"));
                LocalDateTime now = LocalDateTime.now();
                predicates.add(cb.between(root.get("publishedAt"), type.cutoff(now), now));
            }
            
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static void addJobFilters(List<Predicate> predicates, From<?, JobPosition> position,
            JobPositionSearchQuery search, HibernateCriteriaBuilder cb) {
        if (search.getTitle() != null && !search.getTitle().isBlank()) {
            predicates.add(cb.greaterThan(
                cb.function("ats_match_title", Double.class, position.get("title"), cb.value(search.getTitle().trim())), 0.0));
        }
        if (search.getWorkLocations() != null && !search.getWorkLocations().isEmpty()) {
            List<Predicate> locations = search.getWorkLocations().stream()
                .<Predicate>map(location -> cb.equal(cb.function("ats_json_member", Integer.class,
                    position.get("workLocation"), cb.value(location)), 1))
                .toList();
            predicates.add(cb.or(locations.toArray(Predicate[]::new)));
        }
        addInPredicate(predicates, position.get("employmentType"), search.getEmploymentTypes(), JobEmploymentType.values());
        addInPredicate(predicates, position.get("workArrangementType"), search.getWorkArrangementTypes(), JobWorkArrangementType.values());
        addInPredicate(predicates, position.get("experienceLevelType"), search.getExperienceLevelTypes(), JobExperienceLevelType.values());
    }


    private static <T extends Enum<T> & LocalizedBaseEnum> void addInPredicate(List<Predicate> predicates,
            jakarta.persistence.criteria.Path<T> path, List<Integer> values, T[] options) {
        if (values != null && !values.isEmpty()) {
            List<T> selected = values.stream()
                .map(id -> Arrays.stream(options).filter(option -> option.getId().equals(id))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("Invalid filter value: " + id)))
                .toList();
            predicates.add(path.in(selected));
        }
    }
}
