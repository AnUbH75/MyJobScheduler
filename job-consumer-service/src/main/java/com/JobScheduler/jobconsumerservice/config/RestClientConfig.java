package com.JobScheduler.jobconsumerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${executor.base-url}")
    private String executorBaseUrl;

    @Bean
    public RestClient executorRestClient() {
        return RestClient.builder()
                .baseUrl(executorBaseUrl)
                .build();
    }
}
