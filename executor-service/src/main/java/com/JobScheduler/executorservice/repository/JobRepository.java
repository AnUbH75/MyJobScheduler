package com.JobScheduler.executorservice.repository;

import com.JobScheduler.executorservice.entity.Job;
import com.JobScheduler.executorservice.entity.JobId;
import com.JobScheduler.executorservice.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, JobId> {

    @Modifying
    @Transactional
    @Query("UPDATE Job j SET j.status = :status, j.updatedAt = :updatedAt " +
            "WHERE j.id = :id AND j.scheduleTime = :scheduleTime")
    int updateStatus(@Param("id") UUID id,
                     @Param("scheduleTime") LocalDateTime scheduleTime,
                     @Param("status") JobStatus status,
                     @Param("updatedAt") LocalDateTime updatedAt);
}