package com.trainitek.backtothefuture.application;

import com.trainitek.backtothefuture.config.MutableClockTestExecutionListener;
import com.trainitek.backtothefuture.domain.*;
import com.trainitek.backtothefuture.test.fixtures.Fixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import spock.util.time.MutableClock;

import java.time.Instant;

import static java.time.Duration.ofDays;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestExecutionListeners(mergeMode =
        TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS, listeners = MutableClockTestExecutionListener.class)
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
        var today = date("2023-09-11");
        clock.setInstant(today);
        var tomorrow = date("2023-09-12");
        var enrollment = initialEnrollment(today, tomorrow);
        enrollmentRepository.save(enrollment);

        // when
        assertThatThrownBy(() -> startEnrollmentHandler.start(enrollment.getId()))
                .hasMessageContaining("Cannot start the enrollment");

        // move into the future
        moveToFutureByDays(2);

        // and start
        startEnrollmentHandler.start(enrollment.getId());

        // and verify that it's started
        assertEnrollmentIsStarted(enrollment);

        // move back in time
        moveToPastByDays(2);

        // check if you can complete it
        assertThatThrownBy(() -> completeEnrollmentHandler.complete(enrollment.getId()))
                .hasMessageContaining("Cannot complete the enrollment");

        // then
        moveToFutureByDays(3);
        completeEnrollmentHandler.complete(enrollment.getId());

        // then
        assertThatEnrollmentIsCompleted(enrollment);
    }

    private Enrollment initialEnrollment(Instant enrolledAt, Instant availableFrom) {
        return Fixtures.initialEnrollment(enrolledAt, availableFrom, clock)
                .student(student)
                .enroller(student)
                .course(course)
                .build();
    }

    private void assertThatEnrollmentIsCompleted(Enrollment enrollment) {
        assertThat(enrollmentRepository.findById(enrollment.getId())).
                hasValueSatisfying(e -> assertThat(e.isCompleted()).isTrue());
    }

    private void assertEnrollmentIsStarted(Enrollment enrollment) {
        assertThat(enrollmentRepository.findById(enrollment.getId()))
                .hasValueSatisfying(e -> assertThat(e.isStarted()).isTrue());
    }

    private void moveToFutureByDays(int days) {
        clock.setInstant(Instant.now(clock).plus(ofDays(days)));
    }

    private void moveToPastByDays(int days) {
        clock.setInstant(Instant.now(clock).minus(ofDays(days)));
    }

    Instant date(String dateString) {
        return Instant.parse(dateString + "T00:10:00Z");
    }
}