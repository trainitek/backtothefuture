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
public class StartEnrollmentHandler {
    private final EnrollmentRepository repository;
    private final Clock clock;

    public StartEnrollmentHandler(EnrollmentRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public void start(@NonNull UUID enrollmentId) {
        var enrollment = repository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment id=%s not found".formatted(enrollmentId)));
        enrollment.startAt(clock);
        repository.save(enrollment);
    }
}

