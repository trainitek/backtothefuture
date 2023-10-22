package com.trainitek.backtothefuture.test.support

import com.trainitek.backtothefuture.config.MutableClockTestExecutionListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestExecutionListeners
import spock.util.time.MutableClock

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

@TestExecutionListeners(mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
        listeners = MutableClockTestExecutionListener)
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
}
