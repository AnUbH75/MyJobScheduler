package com.JobScheduler.MyJobScheduler.dto;

import com.JobScheduler.MyJobScheduler.enums.JobStatus;
import com.JobScheduler.MyJobScheduler.enums.ScheduleType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class JobResponse {
    private UUID id;
    private String name;
    private ScheduleType scheduleType;
    private JobStatus status;
    private LocalDateTime scheduleTime;
    private String cronExpression;
    private String payload;
    private Integer retries;
    private String meta;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}