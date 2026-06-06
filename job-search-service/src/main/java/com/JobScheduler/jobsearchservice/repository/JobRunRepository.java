package com.JobScheduler.jobsearchservice.repository;

import com.JobScheduler.jobsearchservice.entity.JobRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobRunRepository extends JpaRepository<JobRun, Long> {

    List<JobRun> findByJobIdOrderByStartedAtDesc(UUID jobId);
}
