package com.JobScheduler.MyJobScheduler.service;

import com.JobScheduler.MyJobScheduler.dto.CreateJobRequest;
import com.JobScheduler.MyJobScheduler.dto.JobEvent;
import com.JobScheduler.MyJobScheduler.dto.JobResponse;
import com.JobScheduler.MyJobScheduler.entity.Job;
import com.JobScheduler.MyJobScheduler.enums.JobStatus;
import com.JobScheduler.MyJobScheduler.exception.InvalidJobRequestException;
import com.JobScheduler.MyJobScheduler.exception.InvalidJobStateException;
import com.JobScheduler.MyJobScheduler.exception.JobNotFoundException;
import com.JobScheduler.MyJobScheduler.kafka.JobProducer;
import com.JobScheduler.MyJobScheduler.repository.JobRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private final JobRepo jobRepo;
    private final JobProducer jobProducer;

    // POST /v1/api/jobs
    @Transactional
    public JobResponse createJob(CreateJobRequest request) {
        validateRequest(request);

        Job job = Job.builder()
                .name(request.getName())
                .scheduleType(request.getScheduleType())
                .scheduleTime(request.getScheduleTime())
                .cronExpression(request.getCronExpression())
                .payload(request.getPayload())
                .meta(request.getMeta())
                .retries(request.getRetries() != null ? request.getRetries() : 3)
                .status(JobStatus.SCHEDULED)
                .build();

        Job saved = jobRepo.save(job);
        log.info("Job created with id: {}", saved.getId());
        return toResponse(saved);
    }

    // GET /v1/api/jobs/{jobId}
    public JobResponse getJob(UUID jobId) {
        Job job = jobRepo.findByJobId(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId.toString()));
        return toResponse(job);
    }

    // GET /v1/jobs/{jobId}/status
    public JobStatus getJobStatus(UUID jobId) {
        Job job = jobRepo.findByJobId(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId.toString()));
        return job.getStatus();
    }

    // PUT /v1/jobs/{jobId}
    @Transactional
    public JobResponse updateJob(UUID jobId, CreateJobRequest request) {
        Job job = jobRepo.findByJobId(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId.toString()));

        if (request.getName() != null) job.setName(request.getName());
        if (request.getScheduleTime() != null) job.setScheduleTime(request.getScheduleTime());
        if (request.getCronExpression() != null) job.setCronExpression(request.getCronExpression());
        if (request.getPayload() != null) job.setPayload(request.getPayload());
        if (request.getMeta() != null) job.setMeta(request.getMeta());
        if (request.getRetries() != null) job.setRetries(request.getRetries());

        Job updated = jobRepo.save(job);
        log.info("Job updated: {}", jobId);
        return toResponse(updated);
    }

    // POST /v1/jobs/{jobId}/cancel
    @Transactional
    public void cancelJob(UUID jobId) {
        Job job = jobRepo.findByJobId(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId.toString()));

        if (job.getStatus() == JobStatus.SUCCESS || job.getStatus() == JobStatus.FAILED) {
            throw new InvalidJobStateException("Cannot cancel a job that is already " + job.getStatus());
        }

        jobRepo.updateStatusById(jobId, JobStatus.CANCELLED, LocalDateTime.now());
        log.info("Job cancelled: {}", jobId);
    }

    // POST /v1/jobs/{jobId}/runNow
    @Transactional
    public JobResponse runNow(UUID jobId) {
        Job job = jobRepo.findByJobId(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId.toString()));

        if (job.getStatus() == JobStatus.RUNNING) {
            throw new InvalidJobStateException("Job is already running: " + jobId);
        }

        // Override schedule time to now so watcher picks it up immediately
        job.setScheduleTime(LocalDateTime.now());
        job.setStatus(JobStatus.SCHEDULED);
        Job updated = jobRepo.save(job);

        JobEvent event = JobEvent.builder()
                .jobId(updated.getId())
                .jobName(updated.getName())
                .payload(updated.getPayload())
                .scheduleTime(updated.getScheduleTime())
                .attemptNumber(1)
                .build();

        jobProducer.publishToRunTopic(event);

        log.info("Job triggered for immediate run: {}", jobId);
        return toResponse(updated);
    }

    // --- Private helpers ---

    private void validateRequest(CreateJobRequest request) {
        switch (request.getScheduleType()) {
            case ONE_TIME, RECURRING -> {
                if (request.getScheduleTime() == null) {
                    throw new InvalidJobRequestException("scheduleTime is required for " + request.getScheduleType());
                }
            }
            case CRON -> {
                if (request.getCronExpression() == null || request.getCronExpression().isBlank()) {
                    throw new InvalidJobRequestException("cronExpression is required for CRON jobs");
                }
            }
        }
    }

    private JobResponse toResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .name(job.getName())
                .scheduleType(job.getScheduleType())
                .status(job.getStatus())
                .scheduleTime(job.getScheduleTime())
                .cronExpression(job.getCronExpression())
                .payload(job.getPayload())
                .retries(job.getRetries())
                .meta(job.getMeta())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}