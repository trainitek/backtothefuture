package com.trainitek.backtothefuture.test.support

import spock.util.time.MutableClock

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

trait UnitClockSupport {
    final MutableClock clock = new MutableClock()

    /**
     * Examples:
     * adjustClock { it + ofHours(30) }
     * adjustClock { it - ofMinutes(10) }
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
