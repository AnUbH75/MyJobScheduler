package com.JobScheduler.jobsearchservice.service;

import com.JobScheduler.jobsearchservice.dto.JobSearchResponse;
import com.JobScheduler.jobsearchservice.dto.JobStatsResponse;
import com.JobScheduler.jobsearchservice.entity.Job;
import com.JobScheduler.jobsearchservice.entity.JobRun;
import com.JobScheduler.jobsearchservice.enums.JobStatus;
import com.JobScheduler.jobsearchservice.repository.JobRepository;
import com.JobScheduler.jobsearchservice.repository.JobRunRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobSearchService {

    private final JobRepository jobRepository;
    private final JobRunRepository jobRunRepository;

    public Page<JobSearchResponse> searchJobs(String name, JobStatus status, int page, int size) {
        log.info("Searching jobs — name: {}, status: {}, page: {}, size: {}", name, status, page, size);
        Page<Job> jobs = jobRepository.searchJobs(name, status, PageRequest.of(page, size));
        return jobs.map(this::toResponse);
    }

    public JobSearchResponse getJobById(UUID id) {
        log.info("Fetching job by id: {}", id);
        List<Job> jobs = jobRepository.findAllById(id);
        if (jobs.isEmpty()) {
            throw new RuntimeException("Job not found with id: " + id);
        }
        // Partitioned table may return multiple rows for same UUID across partitions;
        // return the most recently updated one
        return jobs.stream()
                .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
                .map(this::toResponse)
                .findFirst()
                .orElseThrow();
    }

    public List<JobRun> getJobRuns(UUID jobId) {
        log.info("Fetching runs for job: {}", jobId);
        return jobRunRepository.findByJobIdOrderByStartedAtDesc(jobId);
    }

    public JobStatsResponse getStats() {
        log.info("Fetching job stats");
        List<Object[]> results = jobRepository.countByStatus();

        Map<String, Long> countByStatus = new LinkedHashMap<>();
        long total = 0;
        for (Object[] row : results) {
            String status = ((JobStatus) row[0]).name();
            Long count = (Long) row[1];
            countByStatus.put(status, count);
            total += count;
        }

        return JobStatsResponse.builder()
                .totalJobs(total)
                .countByStatus(countByStatus)
                .build();
    }

    private JobSearchResponse toResponse(Job job) {
        return JobSearchResponse.builder()
                .id(job.getId())
                .name(job.getName())
                .status(job.getStatus())
                .payload(job.getPayload())
                .scheduleType(job.getScheduleType())
                .scheduleTime(job.getScheduleTime())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}
