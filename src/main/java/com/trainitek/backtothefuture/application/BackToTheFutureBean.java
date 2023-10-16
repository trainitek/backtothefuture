package com.trainitek.backtothefuture.application;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;

@Service
public class BackToTheFutureBean {

    Clock clock;

    public BackToTheFutureBean(Clock clock) {
        this.clock = clock;
    }

    public void println(String msg) {
        System.out.printf("(%s): Message: \"%s\" at (%s)\n", getClass().getSimpleName(), msg, LocalDate.now(clock));
    }
}
