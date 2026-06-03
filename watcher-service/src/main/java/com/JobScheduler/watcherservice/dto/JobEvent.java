package com.JobScheduler.watcherservice.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobEvent {
    private UUID jobId;
    private String jobName;
    private String payload;
    private LocalDateTime scheduleTime;
    private int attemptNumber;
}