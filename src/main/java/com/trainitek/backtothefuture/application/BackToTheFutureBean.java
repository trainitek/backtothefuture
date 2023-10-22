package com.trainitek.backtothefuture.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;

@Slf4j
@Service
public class BackToTheFutureBean {

    Clock clock;

    public BackToTheFutureBean(Clock clock) {
        this.clock = clock;
    }

    public void log(String msg) {
        log.info("({}): Message: \"{}}\" at ({}})\n", getClass().getSimpleName(), msg, LocalDate.now(clock));
    }
}
