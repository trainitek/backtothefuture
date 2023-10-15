package com.trainitek.backtothefuture.config.clocks;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

public class OurMutableClock extends Clock {
    private Instant currentInstant;
    private final ZoneId zone;

    private OurMutableClock(Instant initialInstant, ZoneId zone) {
        this.currentInstant = initialInstant;
        this.zone = zone;
    }

    public static OurMutableClock now(ZoneId zone) {
        return new OurMutableClock(Instant.now(), zone);
    }

    public static OurMutableClock nowUTC() {
        return now(ZoneId.of("UTC"));
    }

    public static OurMutableClock systemDefaultZone() {
        return now(ZoneId.systemDefault());
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new OurMutableClock(currentInstant, zone);
    }

    @Override
    public Instant instant() {
        return currentInstant;
    }

    public void adjustTo(Instant newInstant) {
        this.currentInstant = newInstant;
    }

    public void moveTo(Instant newInstant) {
        adjustTo(newInstant);
    }

    public void jumpToFutureBy(Duration duration) {
        this.currentInstant = this.currentInstant.plus(duration);
    }

    public void jumpToPastBy(Duration duration) {
        this.currentInstant = this.currentInstant.minus(duration);
    }

    public void moveForwardBy(Duration duration) {
        jumpToFutureBy(duration);
    }

    public void moveBackBy(Duration duration) {
        jumpToPastBy(duration);
    }
}