CREATE TABLE jobs (
                      id UUID DEFAULT gen_random_uuid(),
                      name VARCHAR(255) NOT NULL,
                      schedule_type VARCHAR(50) NOT NULL,
                      status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
                      schedule_time TIMESTAMP,
                      cron_expression VARCHAR(100),
                      payload TEXT,
                      retries INT DEFAULT 3,
                      meta TEXT,
                      created_at TIMESTAMP DEFAULT NOW(),
                      updated_at TIMESTAMP DEFAULT NOW(),
                      PRIMARY KEY (id, schedule_time)
) PARTITION BY RANGE (schedule_time);

-- Create partitions
CREATE TABLE jobs_default PARTITION OF jobs DEFAULT;
CREATE TABLE jobs_2025 PARTITION OF jobs
    FOR VALUES FROM ('2025-01-01') TO ('2026-01-01');
CREATE TABLE jobs_2026 PARTITION OF jobs
    FOR VALUES FROM ('2026-01-01') TO ('2027-01-01');

-- Indexes
CREATE INDEX idx_jobs_status ON jobs(status);
CREATE INDEX idx_jobs_schedule_time ON jobs(schedule_time);