package com.JobScheduler.MyJobScheduler.repository;

import com.JobScheduler.MyJobScheduler.entity.JobRun;
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
public interface JobRunRepo extends JpaRepository<JobRun, UUID> {

    // All runs for a job — needed for job detail / history
    List<JobRun> findByJobIdOrderByCreatedAtDesc(UUID jobId);

    // Latest run for a job — needed for GET /jobs/{jobId}/status
    @Query("SELECT jr FROM JobRun jr WHERE jr.jobId = :jobId ORDER BY jr.createdAt DESC LIMIT 1")
    Optional<JobRun> findLatestByJobId(@Param("jobId") UUID jobId);

    // Stuck job detection — watcher uses this
    // Finds runs that are RUNNING but haven't sent a heartbeat in over 19s
    @Query("SELECT jr FROM JobRun jr WHERE jr.status = 'RUNNING' AND jr.modifiedTime < :cutoff")
    List<JobRun> findStuckRuns(@Param("cutoff") LocalDateTime cutoff);

    // Update status of a run
    @Modifying
    @Query("UPDATE JobRun jr SET jr.status = :status, jr.modifiedTime = :now WHERE jr.id = :id")
    int updateStatus(@Param("id") UUID id, @Param("status") String status, @Param("now") LocalDateTime now);

    // Find active run for a job — used when cancelling
    @Query("SELECT jr FROM JobRun jr WHERE jr.jobId = :jobId AND jr.status IN ('QUEUED', 'RUNNING') ORDER BY jr.createdAt DESC LIMIT 1")
    Optional<JobRun> findActiveRunByJobId(@Param("jobId") UUID jobId);
}