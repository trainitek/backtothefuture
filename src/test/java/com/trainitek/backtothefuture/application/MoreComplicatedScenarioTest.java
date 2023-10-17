package com.trainitek.backtothefuture.application;

import com.trainitek.backtothefuture.domain.*;
import com.trainitek.backtothefuture.test.fixtures.Fixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spock.util.time.MutableClock;

import java.time.Instant;

import static java.time.Duration.ofDays;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MoreComplicatedScenarioTest {

    @Autowired
    StartEnrollmentHandler startEnrollmentHandler;

    @Autowired
    CompleteEnrollmentHandler completeEnrollmentHandler;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    MutableClock clock;
    private User student;
    private Course course;

    @BeforeEach
    void setUp() {
        student = Fixtures.someStudent();
        userRepository.save(student);

        course = Fixtures.someCourse();
        courseRepository.save(course);
    }

    @Test
    void shouldCreateASimpleEnrollmentAndStartIt() {
        // given an initial enrollment available from Tomorrow
        Instant today = Instant.now(clock);
        Instant tomorrow = today.plus(ofDays(1));
        Enrollment enrollment = Enrollment.initialEnrollment(student, student, course, today, tomorrow);
        enrollmentRepository.save(enrollment);

        // when
        assertThatThrownBy( () ->
            startEnrollmentHandler.start(enrollment.getId()))
                .hasMessageContaining("Cannot start the enrollment");

        // move into the future
        moveIntoFutureByDays(2);

        // and start
        startEnrollmentHandler.start(enrollment.getId());

        // and verify that it's started
        assertThat(enrollmentRepository.findById(enrollment.getId())).
                hasValueSatisfying(Enrollment::isStarted);

        // move back in time
        moveIntoPastByDays(2);

        // check if you can complete it
        assertThatThrownBy(() ->
                completeEnrollmentHandler.complete(enrollment.getId()))
                .hasMessageContaining("Cannot complete the enrollment");

        // then
        moveIntoFutureByDays(3);
        completeEnrollmentHandler.complete(enrollment.getId());

        // then
        assertThat(enrollmentRepository.findById(enrollment.getId())).
                hasValueSatisfying(Enrollment::isCompleted);
    }

    private void moveIntoFutureByDays(int days) {
        clock.setInstant(Instant.now(clock).plus(ofDays(days)));
    }

    private void moveIntoPastByDays(int days) {
        clock.setInstant(Instant.now(clock).minus(ofDays(days)));
    }
}