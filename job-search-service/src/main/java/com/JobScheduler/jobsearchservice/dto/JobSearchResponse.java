package com.JobScheduler.jobsearchservice.dto;

import com.JobScheduler.jobsearchservice.enums.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSearchResponse {

    private UUID id;
    private String name;
    private JobStatus status;
    private String payload;
    private String scheduleType;
    private LocalDateTime scheduleTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
