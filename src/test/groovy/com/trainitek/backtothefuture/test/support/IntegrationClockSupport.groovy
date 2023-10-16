package com.trainitek.backtothefuture.test.support

import org.springframework.beans.factory.annotation.Autowired
import spock.util.time.MutableClock

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

trait IntegrationClockSupport {
    @Autowired
    MutableClock clock

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

    def cleanup() {
        // reset the Clock after each test
        // in a real project we could create a Spring TestExecutionListener in order to do that
        clock.setInstant(Instant.now().atZone(ZoneId.of("UTC")).toInstant())
    }
}
