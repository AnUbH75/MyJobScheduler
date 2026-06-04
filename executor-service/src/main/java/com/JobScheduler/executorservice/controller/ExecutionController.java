package com.JobScheduler.executorservice.controller;

import com.JobScheduler.executorservice.dto.ExecutionRequest;
import com.JobScheduler.executorservice.service.JobExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/execute")
@RequiredArgsConstructor
@Slf4j
public class ExecutionController {

    private final JobExecutorService jobExecutorService;

    @PostMapping
    public ResponseEntity<String> executeJob(@RequestBody ExecutionRequest request) {
        log.info("Received execution request for job: {} ({})", request.getJobId(), request.getJobName());
        jobExecutorService.submitJob(request);
        return ResponseEntity.accepted().body("Job " + request.getJobId() + " submitted for execution");
    }
}