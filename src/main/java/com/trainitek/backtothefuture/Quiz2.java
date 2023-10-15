package com.trainitek.backtothefuture;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
public class Quiz2 {

    public static void main(String[] args) {
        // 1. Using java.util.Date
        log.info("Date: {}", new Date());

        // 2. Using LocalDateTime
        log.info("LocalDateTime: {}", LocalDateTime.now());

        log.info("LocalDateTime with UTC zone: {}", LocalDateTime.now(ZoneId.of("UTC")));

        // 3. Using java.sql.Timestamp
        log.info("Timestamp: {}", new Timestamp(System.currentTimeMillis()));

        // 4. Using Instant
        log.info("Instant: {}", Instant.now());

        Instant instantWithClock = Instant.now(Clock.systemUTC());
        log.info("Instant with Clock (UTC): {}", instantWithClock);

        // 5. Using Clock
        Clock clock = Clock.systemDefaultZone();
        log.info("Default Zone: {}", clock.getZone());
        log.info("Clock (Default Zone): {}", clock.instant());

        Clock clockUTC = Clock.system(ZoneId.of("UTC"));
        log.info("UTC Zone: {}", clockUTC.getZone());
        log.info("Clock (UTC): {}", clockUTC.instant());
    }
}
