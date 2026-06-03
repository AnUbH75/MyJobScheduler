package com.JobScheduler.watcherservice.entity;

import com.JobScheduler.watcherservice.enums.JobStatus;
import com.JobScheduler.watcherservice.enums.ScheduleType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

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
    @Column(name = "id")
    private UUID id;

    @Id
    @Column(name = "schedule_time")
    private LocalDateTime scheduleTime;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type")
    private ScheduleType scheduleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private JobStatus status;

    @Column(name = "payload")
    private String payload;

    @Column(name = "retries")
    private int retries;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}