package com.tibell.integrations.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration class for asynchronous processing.
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(cores);
        taskExecutor.setMaxPoolSize(cores * 4);
        taskExecutor.setQueueCapacity(100);
        taskExecutor.setThreadNamePrefix("Async-");
        taskExecutor.initialize();
        return taskExecutor;
    }
}
