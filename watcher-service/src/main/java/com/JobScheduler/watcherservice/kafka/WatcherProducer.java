package com.JobScheduler.watcherservice.kafka;

import com.JobScheduler.watcherservice.dto.JobEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WatcherProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishToRunTopic(JobEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("run", event.getJobId().toString(), message);
            log.info("Watcher published job {} to run topic", event.getJobId());
        } catch (Exception e) {
            log.error("Failed to publish job {} to run topic: {}", event.getJobId(), e.getMessage());
        }
    }

    public void publishToRetryTopic(JobEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("retry", event.getJobId().toString(), message);
            log.info("Watcher published stuck job {} to retry topic", event.getJobId());
        } catch (Exception e) {
            log.error("Failed to publish job {} to retry topic: {}", event.getJobId(), e.getMessage());
        }
    }
}