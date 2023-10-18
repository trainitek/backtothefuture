package com.trainitek.backtothefuture.domain

import com.trainitek.backtothefuture.test.fixtures.Fixtures
import com.trainitek.backtothefuture.test.support.UnitClockSupport
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant

import static java.time.Duration.ofDays

class EnrollmentSpec extends Specification implements UnitClockSupport {

    User student = Fixtures.someStudent()
    Course course = Fixtures.someCourse()

    @Unroll
    def "Enrollment should succeed if enrolledAt is before or equal as availableFrom"() {
        when:
        Enrollment.initialEnrollment(student, student, course, enrolledAt, availableFrom)

        then:
        noExceptionThrown()

        where:
        enrolledAt | availableFrom
        instant()  | enrolledAt
        instant()  | enrolledAt + ofDays(1)
        instant()  | enrolledAt + ofDays(30)
    }

    @Unroll
    def "Enrollment should fail if enrolledAt is after as availableFrom"() {
        when:
        Enrollment.initialEnrollment(student, student, course, enrolledAt, availableFrom)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Enrolled date ${enrolledAt} should be before available date ${availableFrom}."

        where:
        enrolledAt | availableFrom
        instant()  | enrolledAt - ofDays(1)
        instant()  | enrolledAt - ofDays(1)
    }

    def "Enrollment initially isn't started"() {
        given:
        def enrolledAt = Instant.now(clock)
        def availableFrom = enrolledAt

        when:
        Enrollment enrollment = Enrollment.initialEnrollment(student, student, course, enrolledAt, availableFrom)

        then:
        !enrollment.isStarted()
    }

    def "Enrollment can be started"() {
        given:
        def enrolledAt = Instant.now(clock)
        def availableFrom = enrolledAt
        Enrollment enrollment = Enrollment.initialEnrollment(student, student, course, enrolledAt, availableFrom)

        when:
        adjustClock { it + ofDays(1) }
        enrollment.startAt(clock)

        then:
        enrollment.isStarted()
    }

    @Unroll
    def "Enrollment should not start if started too early"() {
        given:
        def enrolledAt = Instant.now(clock)
        def availableFrom = enrolledAt
        def enrollment = Enrollment.initialEnrollment(student, student, course, enrolledAt, availableFrom)

        when:
        adjustClock { it - ofDays(1) }
        enrollment.startAt(clock)

        then:
        def e = thrown(IllegalArgumentException)
        def expectedStartedAt = Instant.now(clock)
        e.message == "Cannot start the enrollment. Current date ${expectedStartedAt} is before available date ${availableFrom}."
    }

    def "Started enrollment can completed"() {
        given:
        def enrolledAt = Instant.now(clock)
        def availableFrom = enrolledAt
        def startedAt = availableFrom
        def enrollment = Enrollment.startedEnrollment(student, student, course, enrolledAt, availableFrom,
                startedAt)

        when:
        enrollment.completeAt(clock)

        then:
        enrollment.isCompleted()
    }

    def "When enrollment is completed it receives valid until date"() {
        given:
        def enrolledAt = Instant.now(clock)
        def availableFrom = enrolledAt
        def startedAt = availableFrom
        def enrollment = Enrollment.startedEnrollment(student, student, course, enrolledAt, availableFrom,
                startedAt)
        def zone = clock.getZone()
        def expectedValidUntilDate = clock.instant().atZone(zone)
                .plusMonths(Enrollment.COMPLETION_VALID_DURATION).toInstant()

        when:
        enrollment.completeAt(clock)

        then:
        enrollment.completionValidUntil.atZone(clock.zone).toInstant() == expectedValidUntilDate
    }

    def "Enrollment should complete only if it has started"() {
        given:
        def enrolledAt = Instant.now(clock)
        def availableFrom = enrolledAt
        def enrollment = Enrollment.initialEnrollment(student, student, course, enrolledAt, availableFrom)

        when:
        enrollment.completeAt(clock)

        then:
        thrown(IllegalArgumentException)
    }

    def "Started enrollment can't be completed if time was tampered with"() {
        given:
        def enrolledAt = Instant.now(clock)
        def availableFrom = enrolledAt
        def startedAt = availableFrom
        def enrollment = Enrollment.startedEnrollment(student, student, course, enrolledAt, availableFrom,
                startedAt)

        when:
        adjustClock { it - ofDays(1) }
        enrollment.completeAt(clock)

        then:
        def e = thrown(IllegalArgumentException)
        def expectedCompletedAt = Instant.now(clock)
        e.message == "Cannot complete the enrollment. Started at date ${startedAt} is after current date " +
                "(completion date) ${expectedCompletedAt}."
    }

    def "Enrollment can be created, started and then completed during a longer period"() {
        given:
        def enrolledAt = Instant.now(clock)
        def availableFrom = enrolledAt + ofDays(14)
        def enrollment = Enrollment.initialEnrollment(student, student, course, enrolledAt, availableFrom)

        when: 'time passes'
        adjustClock { it + ofDays(30) }

        and: 'enrollment is started'
        enrollment.startAt(clock)

        and: 'some more time time passes'
        adjustClock { it + ofDays(7) }

        and: 'enrollment is completed'
        enrollment.completeAt(clock)

        then:
        enrollment.startedAt == enrolledAt + ofDays(30)
        enrollment.completedAt == enrolledAt + ofDays(30 + 7)
        def expectedValidUntilDate = (enrolledAt + ofDays(30 + 7))
                .atZone(clock.zone)
                .plusMonths(Enrollment.COMPLETION_VALID_DURATION)
                .toInstant()
        enrollment.completionValidUntil == expectedValidUntilDate
    }
}