package com.JobScheduler.executorservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobId implements Serializable {
    private UUID id;
    private LocalDateTime scheduleTime;
}