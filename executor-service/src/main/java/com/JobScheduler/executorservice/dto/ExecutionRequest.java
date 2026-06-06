package com.JobScheduler.executorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionRequest {
    private UUID jobId;
    private String jobName;
    private String payload;
    private LocalDateTime scheduleTime;
    private int attemptNumber;
}