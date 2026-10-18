package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.TaskRecord;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRecordRepository extends JpaRepository<TaskRecord, Long> {

    /** SELECT ... FOR UPDATE NOWAIT: throws a PessimisticLockingFailureException when another node holds the lock. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "0"))
    @Query("select t from TaskRecord t where t.taskId = :taskId order by t.id")
    List<TaskRecord> lockAllByTaskId(@Param("taskId") String taskId);

    default Optional<TaskRecord> lockFirstByTaskId(String taskId) {
        return lockAllByTaskId(taskId).stream().findFirst();
    }
}
