package com.trainitek.backtothefuture.application;

import com.trainitek.backtothefuture.domain.*;
import com.trainitek.backtothefuture.test.fixtures.Fixtures;
import org.junit.jupiter.api.Assertions;
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
    void shouldCreateASimpleEnrollmentAndStartIt() {
        // given
        Instant enrolledAt = Instant.now(clock);
        Instant availableFrom = enrolledAt.plus(ofDays(1));
        Enrollment enrollment = Enrollment.initialEnrollment(student, student, course, enrolledAt, availableFrom);
        enrollmentRepository.save(enrollment);

        // when
        clock.adjust(adjuster -> adjuster.plus(ofDays(2)));
        handler.start(enrollment.getId());

        // then
        assertThat(enrollmentRepository.findById(enrollment.getId()))
                .hasValueSatisfying(Enrollment::isStarted);
    }

    @Test
    void shouldNotAllowToStartEnrollmentTooEarly() {
        // given
        Instant enrolledAt = Instant.now(clock);
        Instant availableFrom = enrolledAt.plus(ofDays(1));
        Enrollment enrollment = Enrollment.initialEnrollment(student, student, course, enrolledAt, availableFrom);
        enrollmentRepository.save(enrollment);

        // when
        assertThatThrownBy(() -> handler.start(enrollment.getId()))
                        .hasMessageContaining("Cannot start the enrollment");

        // then
        assertThat(enrollmentRepository.findById(enrollment.getId()))
                .hasValueSatisfying(Enrollment::isStarted);
    }
}