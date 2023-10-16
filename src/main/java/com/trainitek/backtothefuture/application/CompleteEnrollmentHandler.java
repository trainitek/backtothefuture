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
public class CompleteEnrollmentHandler {
    private final EnrollmentRepository repository;
    private final Clock clock;

    public CompleteEnrollmentHandler(EnrollmentRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public void complete(@NonNull UUID enrollmentId) {
        Enrollment enrollment = repository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment id=%s not found".formatted(enrollmentId)));
        enrollment.completeAt(clock);
        repository.save(enrollment);
    }
}

