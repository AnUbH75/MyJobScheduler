package com.JobScheduler.executorservice.service;

import com.JobScheduler.executorservice.dto.ExecutionRequest;
import com.JobScheduler.executorservice.enums.JobStatus;
import com.JobScheduler.executorservice.repository.JobRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobExecutorService {

    private final JobRepository jobRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${executor.thread-pool-size}")
    private int threadPoolSize;

    @Value("${executor.heartbeat-interval-seconds}")
    private int heartbeatIntervalSeconds;

    @Value("${executor.heartbeat-ttl-seconds}")
    private int heartbeatTtlSeconds;

    @Value("${executor.cancel-poll-interval-seconds}")
    private int cancelPollIntervalSeconds;

    private ExecutorService executorPool;

    @PostConstruct
    public void init() {
        executorPool = Executors.newFixedThreadPool(threadPoolSize);
        log.info("Executor thread pool initialized with size: {}", threadPoolSize);
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down executor thread pool...");
        executorPool.shutdown();
        try {
            if (!executorPool.awaitTermination(30, TimeUnit.SECONDS)) {
                executorPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void submitJob(ExecutionRequest request) {
        executorPool.submit(() -> executeJob(request));
    }

    private void executeJob(ExecutionRequest request) {
        Long jobId = request.getJobId();
        LocalDateTime scheduleTime = request.getScheduleTime();
        String heartbeatKey = "heartbeat:" + jobId;
        String cancelKey = "cancel:" + jobId;

        log.info("Starting execution for job: {} ({})", jobId, request.getJobName());

        try {
            // 1. Mark as RUNNING
            int updated = jobRepository.updateStatus(jobId, scheduleTime, JobStatus.RUNNING, LocalDateTime.now());
            if (updated == 0) {
                log.warn("Could not mark job {} as RUNNING — skipping execution", jobId);
                return;
            }

            // 2. Parse duration from payload (default 30s)
            int durationSeconds = parseDuration(request.getPayload());
            log.info("Job {} will run for {} seconds", jobId, durationSeconds);

            // 3. Execute with heartbeat + cancel polling
            int elapsed = 0;
            while (elapsed < durationSeconds) {
                // Check cancel signal
                if (Boolean.TRUE.equals(redisTemplate.hasKey(cancelKey))) {
                    log.info("Cancel signal detected for job {}", jobId);
                    jobRepository.updateStatus(jobId, scheduleTime, JobStatus.CANCELLED, LocalDateTime.now());
                    cleanupRedis(heartbeatKey, cancelKey);
                    return;
                }

                // Write heartbeat
                redisTemplate.opsForValue().set(heartbeatKey, String.valueOf(System.currentTimeMillis()),
                        Duration.ofSeconds(heartbeatTtlSeconds));

                // Sleep for the smaller of cancel-poll-interval or remaining time
                int sleepTime = Math.min(cancelPollIntervalSeconds, durationSeconds - elapsed);
                Thread.sleep(sleepTime * 1000L);
                elapsed += sleepTime;
            }

            // 4. Success
            jobRepository.updateStatus(jobId, scheduleTime, JobStatus.SUCCESS, LocalDateTime.now());
            cleanupRedis(heartbeatKey, cancelKey);
            log.info("Job {} completed successfully", jobId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Job {} interrupted", jobId);
            jobRepository.updateStatus(jobId, scheduleTime, JobStatus.FAILED, LocalDateTime.now());
            cleanupRedis(heartbeatKey, cancelKey);
        } catch (Exception e) {
            log.error("Job {} failed with error: {}", jobId, e.getMessage(), e);
            jobRepository.updateStatus(jobId, scheduleTime, JobStatus.FAILED, LocalDateTime.now());
            cleanupRedis(heartbeatKey, cancelKey);
        }
    }

    private int parseDuration(String payload) {
        // Expects payload like: {"duration": 30}
        // Simple parse — no Jackson dependency needed for this
        try {
            if (payload != null && payload.contains("duration")) {
                String cleaned = payload.replaceAll("[^0-9]", " ").trim();
                String[] parts = cleaned.split("\\s+");
                for (String part : parts) {
                    int val = Integer.parseInt(part);
                    if (val > 0 && val <= 300) return val;
                }
            }
        } catch (Exception e) {
            log.warn("Could not parse duration from payload: {}, defaulting to 30s", payload);
        }
        return 30;
    }

    private void cleanupRedis(String heartbeatKey, String cancelKey) {
        try {
            redisTemplate.delete(heartbeatKey);
            redisTemplate.delete(cancelKey);
        } catch (Exception e) {
            log.warn("Redis cleanup failed for keys {}, {}: {}", heartbeatKey, cancelKey, e.getMessage());
        }
    }
}