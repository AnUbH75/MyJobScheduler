CREATE TABLE job_runs (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          job_id UUID NOT NULL,
                          status VARCHAR(50) NOT NULL DEFAULT 'QUEUED',
                          start_time TIMESTAMP,
                          end_time TIMESTAMP,
                          modified_time TIMESTAMP DEFAULT NOW(),
                          executor_id VARCHAR(100),
                          attempt_number INT DEFAULT 1,
                          error_msg TEXT,
                          created_at TIMESTAMP DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_job_runs_job_id ON job_runs(job_id);
CREATE INDEX idx_job_runs_status ON job_runs(status);
CREATE INDEX idx_job_runs_modified_time ON job_runs(modified_time);