package com.JobScheduler.jobsearchservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobStatsResponse {

    private long totalJobs;
    private Map<String, Long> countByStatus;
}
