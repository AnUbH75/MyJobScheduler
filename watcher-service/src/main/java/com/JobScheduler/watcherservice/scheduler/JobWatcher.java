package com.JobScheduler.watcherservice.scheduler;

import com.JobScheduler.watcherservice.dto.JobEvent;
import com.JobScheduler.watcherservice.entity.Job;
import com.JobScheduler.watcherservice.enums.JobStatus;
import com.JobScheduler.watcherservice.kafka.WatcherProducer;
import com.JobScheduler.watcherservice.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobWatcher {

    private final JobRepository jobRepository;
    private final WatcherProducer watcherProducer;
    private final StringRedisTemplate redisTemplate;

    @Value("${watcher.poll-window-minutes}")
    private int pollWindowMinutes;

    @Value("${watcher.stuck-job-threshold-seconds}")
    private int stuckJobThresholdSeconds;

    // Runs every 10 seconds
    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void pollAndQueueJobs() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime window = now.plusMinutes(pollWindowMinutes);

        log.info("Watcher polling at {} — window until {}", now, window);

        // --- Phase 1: Pick up scheduled jobs ---
        List<Job> scheduledJobs = jobRepository.findScheduledJobsInWindow(
                JobStatus.SCHEDULED, now, window
        );

        for (Job job : scheduledJobs) {
            jobRepository.updateStatus(job.getId(), job.getScheduleTime(),
                    JobStatus.QUEUED, LocalDateTime.now());

            JobEvent event = JobEvent.builder()
                    .jobId(job.getId())
                    .jobName(job.getName())
                    .payload(job.getPayload())
                    .scheduleTime(job.getScheduleTime())
                    .attemptNumber(1)
                    .build();

            watcherProducer.publishToRunTopic(event);
            log.info("Queued job {} scheduled at {}", job.getId(), job.getScheduleTime());
        }

        // --- Phase 2: Detect stuck jobs ---
        LocalDateTime stuckThreshold = now.minusSeconds(stuckJobThresholdSeconds);
        List<Job> stuckJobs = jobRepository.findStuckJobs(JobStatus.RUNNING, stuckThreshold);

        for (Job job : stuckJobs) {
            JobEvent event = JobEvent.builder()
                    .jobId(job.getId())
                    .jobName(job.getName())
                    .payload(job.getPayload())
                    .scheduleTime(job.getScheduleTime())
                    .attemptNumber(1)
                    .build();

            watcherProducer.publishToRetryTopic(event);
            log.warn("Detected stuck job {} — pushed to retry topic", job.getId());
        }

        // --- Phase 3: Update last polled time in Redis ---
        redisTemplate.opsForValue().set("watcher:last_polled_time", now.toString());
    }
}