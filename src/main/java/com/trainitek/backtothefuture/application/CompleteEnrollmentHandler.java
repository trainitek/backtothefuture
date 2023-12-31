package com.trainitek.backtothefuture.application;

import com.trainitek.backtothefuture.domain.EnrollmentRepository;
import jakarta.persistence.EntityNotFoundException;
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
        var enrollment = repository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment id=%s not found".formatted(enrollmentId)));
        enrollment.completeAt(clock);
        repository.save(enrollment);
    }
}

