package com.trainitek.backtothefuture.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import spock.util.time.MutableClock;

@Configuration
public class TestClockConfig {

    @Bean
    MutableClock clock() {
        return new MutableClock();
    }
}