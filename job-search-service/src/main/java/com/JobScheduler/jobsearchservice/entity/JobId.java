package com.JobScheduler.jobsearchservice.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class JobId implements Serializable {

    private UUID id;
    private LocalDateTime scheduleTime;

    public JobId() {}

    public JobId(UUID id, LocalDateTime scheduleTime) {
        this.id = id;
        this.scheduleTime = scheduleTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobId jobId = (JobId) o;
        return Objects.equals(id, jobId.id) && Objects.equals(scheduleTime, jobId.scheduleTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, scheduleTime);
    }
}
