package com.trainitek.backtothefuture.application;

import com.trainitek.backtothefuture.domain.*;
import com.trainitek.backtothefuture.test.fixtures.Fixtures;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spock.util.time.MutableClock;

import java.time.LocalDateTime;

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

    @Test
    void shouldCreateASimpleEnrollmentAndStartIt() {
        // given
        User student = Fixtures.someStudent();
        userRepository.save(student);

        Course course = Fixtures.someCourse();
        courseRepository.save(course);

        LocalDateTime today = LocalDateTime.now(clock).minusDays(1);
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .enroller(student)
                .enrolledAt(today)
                .availableFrom(today)
                .build();
        enrollmentRepository.save(enrollment);

        // when
        handler.startEnrollment(enrollment.getId());

        // then
        Assertions.assertThat(enrollmentRepository.findById(enrollment.getId())).
                hasValueSatisfying(Enrollment::isStarted);
    }

}