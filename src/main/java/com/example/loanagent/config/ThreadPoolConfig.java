package com.example.loanagent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ThreadPoolConfig {
    @Bean("agentTaskExecutor")
    public Executor agentTaskExecutor(@Value("${agent.async.core-pool-size:10}") int corePoolSize,
                                      @Value("${agent.async.max-pool-size:50}") int maxPoolSize,
                                      @Value("${agent.async.queue-capacity:200}") int queueCapacity) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("loan-agent-");
        executor.initialize();
        return executor;
    }
}
