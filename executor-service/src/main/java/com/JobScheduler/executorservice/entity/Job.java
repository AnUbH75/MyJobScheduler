package com.JobScheduler.executorservice.entity;

import com.JobScheduler.executorservice.enums.JobStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@IdClass(JobId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @Column(name = "id")
    private Long id;

    @Id
    @Column(name = "schedule_time")
    private LocalDateTime scheduleTime;

    @Column(name = "job_name")
    private String jobName;

    @Column(name = "payload")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private JobStatus status;

    @Column(name = "attempt_number")
    private int attemptNumber;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}