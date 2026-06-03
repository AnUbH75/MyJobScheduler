package com.JobScheduler.MyJobScheduler.kafka;

import com.JobScheduler.MyJobScheduler.dto.JobEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishToRunTopic(JobEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            // keyed by jobId so same job always goes to same partition
            kafkaTemplate.send("run", event.getJobId().toString(), message);
            log.info("Published job {} to run topic", event.getJobId());
        } catch (Exception e) {
            log.error("Failed to publish job {} to run topic: {}", event.getJobId(), e.getMessage());
            throw new RuntimeException("Failed to publish job event", e);
        }
    }
}