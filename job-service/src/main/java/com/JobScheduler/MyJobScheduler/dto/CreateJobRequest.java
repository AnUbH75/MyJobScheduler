package com.JobScheduler.MyJobScheduler.dto;

import com.JobScheduler.MyJobScheduler.enums.ScheduleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateJobRequest {

    @NotBlank(message = "Job name is required")
    private String name;

    @NotNull(message = "Schedule type is required")
    private ScheduleType scheduleType;

    // Required for ONE_TIME and RECURRING
    private LocalDateTime scheduleTime;

    // Required for CRON
    private String cronExpression;

    private String payload;
    private String meta;
    private Integer retries;
}