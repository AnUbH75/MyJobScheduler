package com.JobScheduler.jobsearchservice.controller;

import com.JobScheduler.jobsearchservice.dto.JobSearchResponse;
import com.JobScheduler.jobsearchservice.dto.JobStatsResponse;
import com.JobScheduler.jobsearchservice.entity.JobRun;
import com.JobScheduler.jobsearchservice.enums.JobStatus;
import com.JobScheduler.jobsearchservice.service.JobSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api/jobs")
@RequiredArgsConstructor
public class JobSearchController {

    private final JobSearchService jobSearchService;

    @GetMapping("/search")
    public ResponseEntity<Page<JobSearchResponse>> searchJobs(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) JobStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(jobSearchService.searchJobs(name, status, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobSearchResponse> getJobById(@PathVariable UUID id) {
        return ResponseEntity.ok(jobSearchService.getJobById(id));
    }

    @GetMapping("/{id}/runs")
    public ResponseEntity<List<JobRun>> getJobRuns(@PathVariable UUID id) {
        return ResponseEntity.ok(jobSearchService.getJobRuns(id));
    }

    @GetMapping("/stats")
    public ResponseEntity<JobStatsResponse> getStats() {
        return ResponseEntity.ok(jobSearchService.getStats());
    }
}
