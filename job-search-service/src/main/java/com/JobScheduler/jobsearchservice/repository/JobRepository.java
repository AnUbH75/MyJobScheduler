package com.JobScheduler.jobsearchservice.repository;

import com.JobScheduler.jobsearchservice.entity.Job;
import com.JobScheduler.jobsearchservice.entity.JobId;
import com.JobScheduler.jobsearchservice.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, JobId> {

    @Query("SELECT j FROM Job j WHERE " +
           "(:name IS NULL OR LOWER(j.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:status IS NULL OR j.status = :status) " +
           "ORDER BY j.createdAt DESC")
    Page<Job> searchJobs(@Param("name") String name,
                         @Param("status") JobStatus status,
                         Pageable pageable);

    @Query("SELECT j FROM Job j WHERE j.id = :id")
    List<Job> findAllById(@Param("id") UUID id);

    @Query("SELECT j.status, COUNT(j) FROM Job j GROUP BY j.status")
    List<Object[]> countByStatus();
}
