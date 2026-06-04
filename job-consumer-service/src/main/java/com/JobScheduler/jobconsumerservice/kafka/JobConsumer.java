package com.JobScheduler.jobconsumerservice.kafka;

import com.JobScheduler.jobconsumerservice.dto.JobEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "run", groupId = "job-consumer-group")
    public void consumeRunTopic(String message) {
        processMessage(message, "run");
    }

    @KafkaListener(topics = "retry", groupId = "job-consumer-group")
    public void consumeRetryTopic(String message) {
        processMessage(message, "retry");
    }

    private void processMessage(String message, String topic) {
        try {
            JobEvent event = objectMapper.readValue(message, JobEvent.class);
            log.info("Consumed from [{}]: jobId={}, jobName={}, attempt={}",
                    topic, event.getJobId(), event.getJobName(), event.getAttemptNumber());

            // TODO: dispatch to executor-service
            log.info("Job {} ready for execution", event.getJobId());

        } catch (Exception e) {
            log.error("Failed to process message from [{}]: {}", topic, e.getMessage(), e);
        }
    }
}