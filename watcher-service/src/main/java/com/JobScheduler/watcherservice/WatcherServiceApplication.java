package com.JobScheduler.watcherservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WatcherServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WatcherServiceApplication.class, args);
    }
}