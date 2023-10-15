package com.trainitek.backtothefuture.application;

import com.trainitek.backtothefuture.domain.Enrollment;
import com.trainitek.backtothefuture.domain.EnrollmentRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
@Transactional
public class StartEnrollmentHandler {
    private final EnrollmentRepository repository;
    private final Clock clock;

    public StartEnrollmentHandler(EnrollmentRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public void startEnrollment(@NonNull UUID enrollmentId) {
        Enrollment enrollment = repository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment id=%s not found".formatted(enrollmentId)));
        enrollment.startAt(clock);
        repository.save(enrollment);
    }
}

