package com.trainitek.backtothefuture.domain

import com.trainitek.backtothefuture.test.fixtures.Fixtures
import com.trainitek.backtothefuture.test.support.UnitClockSupport
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant

import static java.time.Duration.ofDays
import static java.time.Duration.ofHours

class EnrollmentSpec extends Specification implements UnitClockSupport {

    @Unroll
    def "Enrollment can be created"() {
        when:
        initialEnrollment(enrolledAt, availableFrom)

        then:
        noExceptionThrown()

        where:
        enrolledAt         | availableFrom
        date("2023-09-10") | date("2023-09-10")
        date("2023-09-10") | date("2023-09-11")
        date("2023-09-10") | date("2023-10-10")
    }

    @Unroll
    def "Enrollment can't be made available before enrollment date"() {
        when:
        initialEnrollment(enrolledAt, availableFrom)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Enrolled date ${enrolledAt} should be before available date ${availableFrom}."

        where:
        enrolledAt         | availableFrom
        date("2023-09-10") | date("2023-09-09")
        date("2023-09-10") | date("2022-09-09")
    }

    def "Enrollment initially isn't started"() {
        given:
        def enrolledAt = date("2023-09-10")
        def availableFrom = enrolledAt

        when:
        Enrollment enrollment = initialEnrollment(enrolledAt, availableFrom)

        then:
        !enrollment.isStarted()
    }

    def "Enrollment can be started"() {
        given:
        def enrolledAt = date("2023-09-10")
        setClockTo(enrolledAt)
        def availableFrom = enrolledAt
        Enrollment enrollment = initialEnrollment(enrolledAt, availableFrom)

        when:
        adjustClock { it + ofDays(1) }
        enrollment.startAt(clock)

        then:
        enrollment.isStarted()
    }

    @Unroll
    def "Enrollment cannot be started too early"() {
        given:
        setClockTo(date("2023-09-10"))
        def enrolledAt = date("2023-09-10") + ofDays(1)
        def availableFrom = enrolledAt
        def enrollment = initialEnrollment(enrolledAt, availableFrom)

        when:
        enrollment.startAt(clock)

        then:
        def e = thrown(IllegalArgumentException)
        def expectedStartedAt = date("2023-09-10")
        e.message == "Cannot start the enrollment. Current date ${expectedStartedAt} is before available date ${availableFrom}."
    }

    def "Started enrollment can be completed"() {
        given:
        setClockTo(date("2023-09-10"))
        def enrolledAt = date("2023-09-10")
        def availableFrom = enrolledAt
        def startedAt = availableFrom
        def enrollment = startedEnrollment(enrolledAt, availableFrom, startedAt)

        when:
        adjustClock { it + ofHours(1) }
        enrollment.completeAt(clock)

        then:
        enrollment.isCompleted()
    }

    def "When enrollment is completed it receives valid until date"() {
        given:
        setClockTo(date("2023-09-10"))
        def enrolledAt = date("2023-09-10")
        def availableFrom = enrolledAt
        def startedAt = availableFrom
        def enrollment = startedEnrollment(enrolledAt, availableFrom, startedAt)
        def zone = clock.getZone()

        when:
        adjustClock { it + ofHours(1) }
        enrollment.completeAt(clock)

        then:
        def expectedValidUntilDate = (date("2023-09-10") + ofHours(1)).atZone(zone)
                .plusMonths(Enrollment.COMPLETION_VALID_DURATION).toInstant()
        enrollment.completionValidUntil == expectedValidUntilDate
    }

    def "Enrollment can't complete if it hasn't started"() {
        given:
        setClockTo(date("2023-09-10"))
        def enrolledAt = date("2023-09-10")
        def availableFrom = enrolledAt
        def enrollment = initialEnrollment(enrolledAt, availableFrom)

        when:
        enrollment.completeAt(clock)

        then:
        def e = thrown(IllegalArgumentException)
        e.message.contains("Cannot complete an enrollment that hasn't started.")
    }

    def "Started enrollment can't be completed if time was tampered with"() {
        given:
        setClockTo(date("2023-09-10"))
        def startedAt = date("2023-09-11")
        def enrolledAt = startedAt
        def availableFrom = startedAt
        def enrollment = startedEnrollment(enrolledAt, availableFrom, startedAt)

        when:
        enrollment.completeAt(clock)

        then:
        def e = thrown(IllegalArgumentException)
        def expectedCompletedAt = date("2023-09-10")

        e.message == "Cannot complete the enrollment. Completion date ${expectedCompletedAt} " +
                "is not after started date ${startedAt}."
    }

    def "Enrollment can be created, started and then completed during a longer period"() {
        given:
        setClockTo(date("2023-09-10"))
        def enrolledAt = date("2023-09-10")
        def availableFrom = enrolledAt + ofDays(14)
        def enrollment = initialEnrollment(enrolledAt, availableFrom)

        when: 'time passes'
        adjustClock { it + ofDays(30) }

        and: 'enrollment is started'
        enrollment.startAt(clock)

        and: 'some more time time passes'
        adjustClock { it + ofDays(7) }

        and: 'enrollment is completed'
        enrollment.completeAt(clock)

        then:
        enrollment.startedAt == date("2023-09-10") + ofDays(30)
        enrollment.completedAt == date("2023-09-10") + ofDays(30 + 7)
        def expectedValidUntilDate = (date("2023-09-10") + ofDays(30 + 7))
                .atZone(clock.zone).plusMonths(Enrollment.COMPLETION_VALID_DURATION).toInstant()
        enrollment.completionValidUntil == expectedValidUntilDate
    }

    def date(String dateString) {
        return Instant.parse(dateString + "T00:10:00Z")
    }

    def startedEnrollment(Instant enrolledAt, Instant availableFrom, Instant startedAt) {
        return Fixtures.someStartedEnrollment(enrolledAt, availableFrom, startedAt, clock)
    }

    def initialEnrollment(Instant enrolledAt, Instant availableFrom) {
        return Fixtures.someInitialEnrollment(enrolledAt, availableFrom, clock)
    }
}