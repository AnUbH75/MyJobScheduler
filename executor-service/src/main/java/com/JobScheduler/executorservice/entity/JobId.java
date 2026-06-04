package com.JobScheduler.executorservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobId implements Serializable {
    private Long id;
    private LocalDateTime scheduleTime;
}