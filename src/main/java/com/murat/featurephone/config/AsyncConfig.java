package com.murat.featurephone.config;


import org.springframework.beans.factory.annotation.Value;import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {


    @Bean(name = "telegramUpdateExecutor")
    public ThreadPoolTaskExecutor telegramUpdateExecutor(
            @Value("${telegram.executor.core-pool-size}") int corePoolSize,
            @Value("${telegram.executor.max-pool-size}") int maxPoolSize,
            @Value("${telegram.executor.queue-capacity}") int queueCapacity
    ){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);

        executor.setMaxPoolSize(4);

        executor.setQueueCapacity(500);

        executor.setThreadNamePrefix("telegram-db-");

        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.AbortPolicy()
        );

        executor.initialize();

        return executor;

    }
}
