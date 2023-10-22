package com.trainitek.backtothefuture.domain

import com.trainitek.backtothefuture.test.fixtures.Fixtures
import com.trainitek.backtothefuture.test.support.UnitClockSupport
import spock.lang.Ignore
import spock.lang.Specification

import java.time.Instant
import java.util.concurrent.TimeUnit

import static java.time.Duration.ofDays

/**
 * Examples of bad implementations of tests depending on TIME.
 */
class BadEnrollmentSpec extends Specification implements UnitClockSupport {

    @Ignore
    def "Bad test 1: Enrollment can be started"() {
        given:
        def enrolledAt = date("2023-11-10")
        def availableFrom = enrolledAt
        Enrollment enrollment = initialEnrollment(enrolledAt, availableFrom)

        when:
        enrollment.start() // -> you need to uncomment code in Enrollment.start()

        then:
        enrollment.isStarted()
    }

    @Ignore
    def "Millis verification fail"() {
        when:
        def now = Instant.now()
        def now2 = Instant.now()
        then:
        now == now2

    }

    def "Millis verification works"() {
        when:
        def now = Instant.now(clock)
        def now2 = Instant.now(clock)
        then:
        now == now2
    }

    @Ignore
    def "Bad test 2: Enrollment can be started"() {
        given:
        def enrolledAt = Instant.now()
        def availableFrom = enrolledAt
        Enrollment enrollment = initialEnrollment(enrolledAt, availableFrom)

        when:
        sleep()
        enrollment.start() // -> you need to uncomment code in Enrollment.start()

        then:
        enrollment.isStarted()

        where:
        i << (1..100)
    }

    @Ignore
    def "Bad test 3: Enrollment can be started"() {
        given:
        def enrolledAt = Instant.now() - ofDays(1) // starting to hack
        def availableFrom = enrolledAt
        Enrollment enrollment = initialEnrollment(enrolledAt, availableFrom)

        when:
        sleep() // starting to hack
        enrollment.start() // -> you need to uncomment code in Enrollment.start()

        then:
        enrollment.isStarted()

        where:
        i << (1..100)
    }

    @Ignore
    def """
           What if you need to test that something happens in the future?
           How will you test it?
           E.g.
           - Notification about expiring completion is sent 2 weeks before it's become invalid?
           - Proctored enrollments - when start and end must be minimum X hours apart?
           - and so on.
           Without controlling time you will hack domain objects, put sleeps here and there,
           do some ninja jumping (hold my beer).
           Is there any other way?

        """() {
        given:
        def enrolledAt = Instant.now() - ofDays(1) // starting to hack
        def availableFrom = enrolledAt
        Enrollment enrollment = initialEnrollment(enrolledAt, availableFrom)

        when:
        sleep() // starting to hack
        enrollment.start() // -> you need to uncomment code in Enrollment.start()

        then:
        enrollment.isStarted()

        where:
        i << (1..100)
    }

    private sleep() {
//        Thread.sleep(1000)
        TimeUnit.NANOSECONDS.sleep(1)
    }

    def date(String dateString) {
        return Instant.parse(dateString + "T00:10:00Z")
    }

    def initialEnrollment(Instant enrolledAt, Instant availableFrom) {
        return Fixtures.someInitialEnrollment(enrolledAt, availableFrom, clock)
    }
}