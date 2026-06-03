package com.JobScheduler.MyJobScheduler.entity;

import com.JobScheduler.MyJobScheduler.enums.JobStatus;
import com.JobScheduler.MyJobScheduler.enums.ScheduleType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@IdClass(JobId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Id
    @Column(name = "schedule_time")
    private LocalDateTime scheduleTime;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false)
    private ScheduleType scheduleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private JobStatus status;

    @Column(name = "cron_expression")
    private String cronExpression;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "retries")
    private Integer retries;

    @Column(name = "meta", columnDefinition = "TEXT")
    private String meta;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = JobStatus.SCHEDULED;
        if (retries == null) retries = 3;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}