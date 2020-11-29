package com.example.springbootmultithreading.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/*once we add @EnableAsync, it will inform to spring framework,
to run the async tasks in background threads using thread pool concepts*/

@Configuration
@EnableAsync
public class AsyncConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncConfig.class);

    /* If we don't configure this executor, then Spring Boot will create SimpleAsyncTaskExecutor */

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        LOGGER.debug("Creating Async Task Executor");
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);    // initializing two threads, so only these number of threads can work simultaneously at one time
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(100); // at a time, 100 numbers of tasks can be waiting in the blocking queue.
        executor.setThreadNamePrefix("userThread-");
        executor.initialize();
        return executor;
    }
}
