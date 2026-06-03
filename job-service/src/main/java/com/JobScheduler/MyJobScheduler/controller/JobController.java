package com.JobScheduler.MyJobScheduler.controller;

import com.JobScheduler.MyJobScheduler.dto.CreateJobRequest;
import com.JobScheduler.MyJobScheduler.dto.JobResponse;
import com.JobScheduler.MyJobScheduler.enums.JobStatus;
import com.JobScheduler.MyJobScheduler.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class JobController {

    private final JobService jobService;

    // POST /v1/api/jobs
    @PostMapping("/v1/api/jobs")
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody CreateJobRequest request) {
        log.info("Received create job request: {}", request.getName());
        JobResponse response = jobService.createJob(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /v1/api/jobs/{jobId}
    @GetMapping("/v1/api/jobs/{jobId}")
    public ResponseEntity<JobResponse> getJob(@PathVariable UUID jobId) {
        return ResponseEntity.ok(jobService.getJob(jobId));
    }

    // GET /v1/jobs/{jobId}/status
    @GetMapping("/v1/jobs/{jobId}/status")
    public ResponseEntity<JobStatus> getJobStatus(@PathVariable UUID jobId) {
        return ResponseEntity.ok(jobService.getJobStatus(jobId));
    }

    // PUT /v1/jobs/{jobId}
    @PutMapping("/v1/jobs/{jobId}")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable UUID jobId,
            @RequestBody CreateJobRequest request) {
        return ResponseEntity.ok(jobService.updateJob(jobId, request));
    }

    // POST /v1/jobs/{jobId}/cancel
    @PostMapping("/v1/jobs/{jobId}/cancel")
    public ResponseEntity<String> cancelJob(@PathVariable UUID jobId) {
        jobService.cancelJob(jobId);
        return ResponseEntity.ok("Job cancelled successfully");
    }

    // POST /v1/jobs/{jobId}/runNow
    @PostMapping("/v1/jobs/{jobId}/runNow")
    public ResponseEntity<JobResponse> runNow(@PathVariable UUID jobId) {
        return ResponseEntity.ok(jobService.runNow(jobId));
    }
}