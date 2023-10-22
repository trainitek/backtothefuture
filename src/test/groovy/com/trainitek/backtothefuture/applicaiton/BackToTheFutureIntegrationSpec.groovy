package com.trainitek.backtothefuture.applicaiton

import com.trainitek.backtothefuture.application.BackToTheFutureBean
import com.trainitek.backtothefuture.test.support.IntegrationClockSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@SpringBootTest
class BackToTheFutureIntegrationSpec extends Specification implements IntegrationClockSupport {

    @Autowired
    BackToTheFutureBean bean

    def "Back to the Future"() {
        when:
        setClockTo(midnightOf(LocalDate.of(1985, 10, 26)))
        bean.log("Marty travels to 1955")

        setClockTo(midnightOf(LocalDate.of(1955, 11, 5)))
        bean.log("Marty meets young Doc") // Explanation

        setClockTo(midnightOf(LocalDate.of(1955, 11, 12)))
        bean.log("Marty saves his parents") // Explanation

        setClockTo(midnightOf(LocalDate.of(2015, 10, 21)))
        bean.log("Marty and Doc arrive")

        setClockTo(midnightOf(LocalDate.of(1885, 1, 1)))
        bean.log("Marty rescues Doc")

        then:
        localDate() == LocalDate.of(1885, 1, 1)
    }

    private Instant midnightOf(LocalDate date) {
        date.atStartOfDay().toInstant(ZoneOffset.ofHours(0))
    }
}