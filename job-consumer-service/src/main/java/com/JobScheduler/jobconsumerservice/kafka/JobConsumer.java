package com.JobScheduler.jobconsumerservice.kafka;

import com.JobScheduler.jobconsumerservice.dto.JobEvent;
import com.JobScheduler.jobconsumerservice.service.ExecutorClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobConsumer {

    private final ObjectMapper objectMapper;
    private final ExecutorClient executorClient;

    @KafkaListener(topics = {"run", "retry"}, groupId = "job-consumer-group")
    public void consume(String message) {
        try {
            JobEvent event = objectMapper.readValue(message, JobEvent.class);
            log.info("Consumed job: {} (attempt {})", event.getJobName(), event.getAttemptNumber());
            executorClient.dispatch(event);
        } catch (Exception e) {
            log.error("Failed to process message: {}", e.getMessage(), e);
        }
    }
}