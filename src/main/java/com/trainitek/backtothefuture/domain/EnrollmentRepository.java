package com.trainitek.backtothefuture.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface EnrollmentRepository extends CrudRepository<Enrollment, UUID> {
}
