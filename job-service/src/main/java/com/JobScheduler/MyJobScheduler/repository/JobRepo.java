package com.JobScheduler.MyJobScheduler.repository;

import com.JobScheduler.MyJobScheduler.entity.Job;
import com.JobScheduler.MyJobScheduler.entity.JobId;
import com.JobScheduler.MyJobScheduler.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepo extends JpaRepository<Job, JobId> {

    // Find by just the id — needed for GET /jobs/{jobId}
    @Query("SELECT j FROM Job j WHERE j.id = :id")
    Optional<Job> findByJobId(@Param("id") UUID id);

    // Find by status — needed by watcher to pull SCHEDULED jobs
    List<Job> findByStatus(String status);

    // Find jobs scheduled in a time window — watcher polls this
    @Query("SELECT j FROM Job j WHERE j.scheduleTime BETWEEN :from AND :to AND j.status = 'SCHEDULED'")
    List<Job> findJobsDueForExecution(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // Update status by id — used across multiple services
    @Modifying
    @Query("UPDATE Job j SET j.status = :status, j.updatedAt = :now WHERE j.id = :id")
    int updateStatusById(@Param("id") UUID id, @Param("status") JobStatus status, @Param("now") LocalDateTime now);
}