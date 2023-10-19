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

import java.time.Duration;
import java.time.Instant;

import static java.time.Duration.ofDays;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestExecutionListeners(mergeMode =
        TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS, listeners = MutableClockTestExecutionListener.class)
class StartEnrollmentHandlerTest {

    @Autowired
    StartEnrollmentHandler handler;

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
    void shouldNotAllowToStartEnrollmentTooEarly() {
        // given
        var enrolledAt = date("2023-09-10");
        clock.setInstant(enrolledAt);
        var availableFrom = enrolledAt.plus(ofDays(1));
        var enrollment = Enrollment.initialEnrollment(student, student, course, enrolledAt, availableFrom);
        enrollmentRepository.save(enrollment);

        // when & then
        assertThatThrownBy(() -> handler.start(enrollment.getId()))
                .hasMessageContaining("Cannot start the enrollment");
    }

    @Test
    void shouldCreateASimpleEnrollmentAndStartIt() {
        // given
        var enrolledAt = date("2023-09-11");
        clock.setInstant(enrolledAt);
        var availableFrom = enrolledAt.plus(ofDays(1));
        var enrollment = Enrollment.initialEnrollment(student, student, course, enrolledAt, availableFrom);
        enrollmentRepository.save(enrollment);

        // when
        moveToFutureBy(ofDays(2));
        handler.start(enrollment.getId());

        // then
        assertThat(enrollmentRepository.findById(enrollment.getId()))
                .hasValueSatisfying(e -> assertThat(e.isStarted()).isTrue());
    }

    private void moveToFutureBy(Duration amount) {
        clock.adjust(adjuster -> adjuster.plus(amount));
    }

    Instant date(String dateString) {
        return Instant.parse(dateString + "T00:10:00Z");
    }
}