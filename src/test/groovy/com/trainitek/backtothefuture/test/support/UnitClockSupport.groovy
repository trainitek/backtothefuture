package com.trainitek.backtothefuture.test.support

import spock.util.time.MutableClock

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

trait UnitClockSupport {
    final MutableClock clock = new MutableClock(ZoneId.of("UTC"))
    /**
     * Example: adjustClock { it + ofHours(30) }
     */
    MutableClock adjustClock(Closure<MutableClock> adjuster) {
        adjuster(clock)
    }

    MutableClock setClockTo(Instant instant) {
        clock.setInstant(instant)
        clock
    }

    LocalDateTime localDateTime() { LocalDateTime.now(clock) }

    LocalDate localDate() { LocalDate.now(clock) }

    Instant instant() { Instant.now(clock) }
}
