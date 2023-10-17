package com.trainitek.backtothefuture.domain

import com.trainitek.backtothefuture.test.support.UnitClockSupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class BackToTheFutureSpec extends Specification implements UnitClockSupport {

    Logger log = LoggerFactory.getLogger(BackToTheFutureSpec)

    def "Back to the Future"() {
        when:
        setClockTo(midnightOf(LocalDate.of(1985, 10, 26)))
        log.info("Base Time: " + localDate())
        log.info("Marty travels to 1955\n")

        setClockTo(midnightOf(LocalDate.of(1955, 11, 5)))
        log.info("Time Travel: " + localDate())
        log.info("Marty meets young Doc\n") // Explanation

        setClockTo(midnightOf(LocalDate.of(1955, 11, 12)))
        log.info("Enchantment Under the Sea Dance: " + localDate())
        log.info("Marty saves his parents\n") // Explanation

        setClockTo(midnightOf(LocalDate.of(2015, 10, 21)))
        log.info("Future: " + localDate())
        log.info("Marty and Doc arrive\n")

        setClockTo(midnightOf(LocalDate.of(1885, 1, 1)))
        log.info("Wild West: " + localDate())
        log.info("Marty rescues Doc\n")

        then:
        localDate() == LocalDate.of(1885, 1, 1)
    }

    /**
     * Can be moved to trait if needed
     */
    private Instant midnightOf(LocalDate date) {
        date.atStartOfDay().toInstant(ZoneOffset.ofHours(0))
    }
}