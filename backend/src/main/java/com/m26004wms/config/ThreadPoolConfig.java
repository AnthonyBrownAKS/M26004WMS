package com.m26004wms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 */

@Configuration
public class ThreadPoolConfig {

    @Bean(name = "taskExecutor")
    public ThreadPoolExecutor taskExecutor() {

        return new ThreadPoolExecutor(
                3,                      // 核心线程数（堆垛机数量）
                5,                      // 最大线程数
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

}
