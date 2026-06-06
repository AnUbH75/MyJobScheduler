package com.JobScheduler.jobconsumerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobEvent {
    private String jobId;
    private String jobName;
    private String payload;
    private LocalDateTime scheduleTime;
    private int attemptNumber;
}