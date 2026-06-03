package com.JobScheduler.watcherservice.repository;

import com.JobScheduler.watcherservice.entity.Job;
import com.JobScheduler.watcherservice.entity.JobId;
import com.JobScheduler.watcherservice.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, JobId> {

    // Jobs due in the next N minutes
    @Query("SELECT j FROM Job j WHERE j.status = :status " +
            "AND j.scheduleTime BETWEEN :now AND :window")
    List<Job> findScheduledJobsInWindow(
            @Param("status") JobStatus status,
            @Param("now") LocalDateTime now,
            @Param("window") LocalDateTime window
    );

    // Stuck jobs — RUNNING but not updated recently
    @Query("SELECT j FROM Job j WHERE j.status = :status " +
            "AND j.updatedAt < :threshold")
    List<Job> findStuckJobs(
            @Param("status") JobStatus status,
            @Param("threshold") LocalDateTime threshold
    );

    @Modifying
    @Query("UPDATE Job j SET j.status = :status, j.updatedAt = :now " +
            "WHERE j.id = :id AND j.scheduleTime = :scheduleTime")
    void updateStatus(
            @Param("id") UUID id,
            @Param("scheduleTime") LocalDateTime scheduleTime,
            @Param("status") JobStatus status,
            @Param("now") LocalDateTime now
    );
}