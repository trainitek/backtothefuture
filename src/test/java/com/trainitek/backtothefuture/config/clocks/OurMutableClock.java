package com.trainitek.backtothefuture.config.clocks;

import lombok.NonNull;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.TemporalAmount;

public class OurMutableClock extends Clock {

    private Instant instant;
    private ZoneId zone;

    public OurMutableClock(@NonNull Instant instant, @NonNull ZoneId zone) {
        this.instant = instant;
        this.zone = zone;
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(@NonNull ZoneId zone) {
        return new OurMutableClock(instant, zone);
    }

    @Override
    public Instant instant() {
        return instant;
    }

    public void setClockTo(@NonNull Instant instant) {
        this.instant = instant;
    }

    public void moveIntoFutureBy(@NonNull TemporalAmount temporalAmount) {
        setClockTo(instant().plus(temporalAmount));
    }

    public void moveIntoPastBy(@NonNull TemporalAmount temporalAmount) {
        setClockTo(instant().minus(temporalAmount));
    }
}