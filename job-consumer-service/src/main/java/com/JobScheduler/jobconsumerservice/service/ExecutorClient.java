package com.JobScheduler.jobconsumerservice.service;

import com.JobScheduler.jobconsumerservice.dto.JobEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExecutorClient {

    private final RestClient executorRestClient;

    public void dispatch(JobEvent event) {
        try {
            Map<String, Object> request = Map.of(
                    "jobId", event.getJobId(),
                    "jobName", event.getJobName(),
                    "payload", event.getPayload() != null ? event.getPayload() : "",
                    "scheduleTime", event.getScheduleTime().toString(),
                    "attemptNumber", event.getAttemptNumber()
            );

            String response = executorRestClient.post()
                    .uri("/v1/api/execute")
                    .header("Content-Type", "application/json")
                    .body(request)
                    .retrieve()
                    .body(String.class);

            log.info("Dispatched job {} to executor — response: {}", event.getJobId(), response);
        } catch (Exception e) {
            log.error("Failed to dispatch job {} to executor: {}", event.getJobId(), e.getMessage(), e);
        }
    }
}
